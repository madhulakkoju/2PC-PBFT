package org.cse535.threadimpls;

import org.cse535.configs.GlobalConfigs;
import org.cse535.database.DatabaseService;
import org.cse535.node.Node;
import org.cse535.proto.*;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class IntraShardTnxProcessingThread extends Thread {

    public Transaction tnx;
    public Node node;
    public int ballotNumber;

    public IntraShardTnxProcessingThread(Node node, Transaction tnx, int ballotNumber) {
        this.tnx = tnx;
        this.node = node;
        this.ballotNumber = ballotNumber;
        this.node.database.addTransaction(ballotNumber, tnx);
    }



    public void run() {
        try {

            String failureReason = "";
            boolean success = false;

            //Wait until locks released if locked.
            if (this.node.database.isDataItemLocked(this.tnx.getSender()) ||
                    this.node.database.isDataItemLocked(this.tnx.getReceiver())) {
                failureReason = "Data Items Locked";
                Thread.sleep(100);
            }

            //If still locked, move on
            if (this.node.database.isDataItemLocked(this.tnx.getSender()) ||
                    this.node.database.isDataItemLocked(this.tnx.getReceiver())) {
                failureReason = "Data Items Locked";
                success = false;
            }
            else {
                System.out.println("Processing transaction " + this.tnx.getTransactionNum() + " "
                        + this.tnx.getSender() + " -> "
                        + this.tnx.getReceiver() + " = "
                        + this.tnx.getAmount());

                //Acquire the locks
                this.node.database.lockDataItem(this.tnx.getSender(), this.tnx.getTransactionNum());
                this.node.database.lockDataItem(this.tnx.getReceiver(), this.tnx.getTransactionNum());

                //Check if the transaction is valid
                if (this.node.database.isValidTransaction(this.tnx)) {
                    // Valid transaction

                    boolean prePrepareSuccess = false;


                    int currentSeqNum = this.node.database.currentSeqNum.incrementAndGet();

                    this.node.database.maxAddedSeqNum.set(currentSeqNum);

                    this.node.database.transactionMap.put(currentSeqNum, this.tnx);
                    this.node.database.seqNumViewMap.put(currentSeqNum, this.node.database.currentViewNum.get());
                    this.node.database.transactionStatusMap.put(currentSeqNum, TransactionStatus.REQUESTED);

                    PrePrepareRequest prePrepareRequest = PrePrepareRequest.newBuilder()
                            .setTransaction(this.tnx)
                            .setSequenceNumber(currentSeqNum)
                            .setView(this.node.database.currentViewNum.get())
                            .setProcessId(this.node.serverName)
                            .setDigest(this.tnx.getTransactionHash())
                            .build();

                    // this.logger.log(prePrepareRequest.toString());

                    this.node.logger.log("Initiating Pre Prepare for SeqNum: " + currentSeqNum + " View: " +
                            this.node.database.currentViewNum.get() + " Transaction ID: " +
                            tnx.getTransactionNum());

                    //Send PrePrepare to all servers
                    IntraPrePrepareThread[] intraPrePrepareThreads = new IntraPrePrepareThread[GlobalConfigs.numServersPerCluster];

                    AtomicInteger successPrePrepares = new AtomicInteger(1);
                    int i = 0;
                    for (int serverNumber : GlobalConfigs.clusterToServersMap.get(this.node.clusterNumber)) {
                        if (serverNumber == this.node.serverNumber)
                            continue;

                        intraPrePrepareThreads[i] = new IntraPrePrepareThread(this.node, prePrepareRequest, serverNumber, successPrePrepares);
                        intraPrePrepareThreads[i].start();
                        i++;
                    }

                    //Wait for all servers to respond
                    for (int j = 0; j < i; j++) {
                        intraPrePrepareThreads[j].join();
                    }

                    //Check if the transaction is successful
                    if (successPrePrepares.get() >= GlobalConfigs.ShardConsesusThreshold) {
                        prePrepareSuccess = true;
                    } else {
                        failureReason = "Pre Prepare Failed";
                        this.node.logger.log("Pre Prepare Failed for SeqNum: " + currentSeqNum + " View: " +
                                this.node.database.currentViewNum.get() + " Transaction ID: " +
                                tnx.getTransactionNum());
                    }

                    if (prePrepareSuccess) {
                        this.node.logger.log("Pre Prepare Success for SeqNum: " + currentSeqNum + " View: " +
                                this.node.database.currentViewNum.get() + " Transaction ID: " +
                                tnx.getTransactionNum());

                        this.node.database.transactionStatusMap.put(currentSeqNum, TransactionStatus.PREPARED);

                        // Prepare the transaction


                        PrepareRequest prepareRequest = PrepareRequest.newBuilder()
                                .setSequenceNumber(currentSeqNum)
                                .setView(this.node.database.currentViewNum.get())
                                .setProcessId(this.node.serverName)
                                .setDigest(this.tnx.getTransactionHash())
                                .build();

                        this.node.logger.log("Initiating Prepare for SeqNum: " + currentSeqNum + " View: " +

                                this.node.database.currentViewNum.get() + " Transaction ID: " +
                                tnx.getTransactionNum());

                        //Send Prepare to all servers

                        IntraPrepareThread[] intraPrepareThreads = new IntraPrepareThread[GlobalConfigs.numServersPerCluster];

                        AtomicInteger successPrepares = new AtomicInteger(1);
                        i = 0;
                        for (int serverNumber : GlobalConfigs.clusterToServersMap.get(this.node.clusterNumber)) {
                            if (serverNumber == this.node.serverNumber)
                                continue;

                            intraPrepareThreads[i] = new IntraPrepareThread(this.node, prepareRequest, serverNumber, successPrepares);
                            intraPrepareThreads[i].start();
                            i++;
                        }

                        //Wait for all servers to respond
                        for (int j = 0; j < i; j++) {
                            intraPrepareThreads[j].join();
                        }

                        //Check if the transaction is successful
                        if (successPrepares.get() >= GlobalConfigs.ShardConsesusThreshold) {
                            success = true;
                            this.node.database.transactionStatusMap.put(currentSeqNum, TransactionStatus.PREPARED);


                            //Initiate Commit

                            this.node.logger.log("Prepare Success for SeqNum: " + currentSeqNum + " View: " +
                                    this.node.database.currentViewNum.get() + " Transaction ID: " +
                                    tnx.getTransactionNum());

                            this.node.logger.log("Initiating Commit for SeqNum: " + currentSeqNum + " View: " +
                                    this.node.database.currentViewNum.get() + " Transaction ID: " +
                                    tnx.getTransactionNum());

                            this.node.sendExecutionReplyToClient(tnx, true, failureReason, "COMMITED");

                            CommitRequest commitRequest = CommitRequest.newBuilder()
                                    .setSequenceNumber(currentSeqNum)
                                    .setView(this.node.database.currentViewNum.get())
                                    .setProcessId(this.node.serverName)
                                    .setDigest(this.tnx.getTransactionHash())
                                    .build();

                            //Send Commit to all servers

                            IntraCommitThread[] intraCommitThreads = new IntraCommitThread[GlobalConfigs.numServersPerCluster];

                            AtomicInteger successCommits = new AtomicInteger(1);
                            i = 0;
                            for (int serverNumber : GlobalConfigs.clusterToServersMap.get(this.node.clusterNumber)) {
                                if (serverNumber == this.node.serverNumber)
                                    continue;

                                intraCommitThreads[i] = new IntraCommitThread(this.node, commitRequest, serverNumber, successCommits);
                                intraCommitThreads[i].start();
                                i++;
                            }

                            //this.node.handleCommit(commitRequest);
                            // Execute Transactions

                            this.node.database.transactionStatusMap.put(currentSeqNum, TransactionStatus.COMMITTED);
                            this.node.database.initiateExecutions();

                            //Wait for all servers to respond
                            for (int j = 0; j < i; j++) {
                                intraCommitThreads[j].join();
                            }





                        }
                        else {
                            failureReason = "Prepare Failed";
                            this.node.logger.log("Prepare Failed for SeqNum: " + currentSeqNum + " View: " +
                                    this.node.database.currentViewNum.get() + " Transaction ID: " +
                                    tnx.getTransactionNum());

                            success = false;
                        }

                    }

                }
                else{
                    failureReason = "Insufficient Balance";
                    success = false;
                }

            }

            if (!tnx.getIsCrossShard() && !success )
                this.node.sendExecutionReplyToClient(tnx, success, failureReason, "ABORTED");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
            this.node.logger.log("Error processing transaction " + this.tnx.getTransactionNum() + " " + e.getMessage());
        } finally {

            this.node.database.unlockDataItem(this.tnx.getSender(), this.tnx.getTransactionNum());
            this.node.database.unlockDataItem(this.tnx.getReceiver(), this.tnx.getTransactionNum());

        }


    }
}
