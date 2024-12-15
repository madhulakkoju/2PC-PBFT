package org.cse535.threadimpls;

import org.cse535.configs.GlobalConfigs;
import org.cse535.configs.Utils;
import org.cse535.database.DatabaseService;
import org.cse535.node.Node;
import org.cse535.proto.*;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IntraShardTnxProcessingThread extends Thread {

    public Transaction tnx;
    public TransactionInputConfig tnxInput;
    public Node node;
    AtomicBoolean isCrossShardSuccess;

    public IntraShardTnxProcessingThread(Node node, TransactionInputConfig tnxInput, AtomicBoolean isCrossShardSuccess) {
        this.tnxInput = tnxInput;
        this.tnx = tnxInput.getTransaction();
        this.node = node;
        this.isCrossShardSuccess = isCrossShardSuccess;
    }



    public void run() {
        try {


            int checkSeqNum = -1;

            if(this.node.database.transactionNumSeqNumMap.containsKey(this.tnx.getTransactionNum())){
                checkSeqNum = this.node.database.transactionNumSeqNumMap.get(this.tnx.getTransactionNum());
            }


            if( checkSeqNum != -1 && this.node.database.transactionStatusMap.containsKey(checkSeqNum)){
                if(this.node.database.transactionStatusMap.get(checkSeqNum) == TransactionStatus.COMMITTED){
                    this.node.sendExecutionReplyToClient(tnx, true, "", "COMMITED");
                    this.node.database.initiateExecutions();
                    return;
                }
                else if(this.node.database.transactionStatusMap.get(checkSeqNum) == TransactionStatus.EXECUTED){
                    this.node.sendExecutionReplyToClient(tnx, true, "", "EXECUTED");
                    return;
                }
                else if(this.node.database.transactionStatusMap.get(checkSeqNum) == TransactionStatus.ABORTED){
                    this.node.sendExecutionReplyToClient(tnx, false, "Transaction Aborted", "ABORTED");
                    return;
                }
                this.node.logger.log("Transaction already done: " + this.tnx.getTransactionNum());
            }


            String failureReason = "";
            boolean success = false;

            boolean isLocked = true;

            if (this.node.database.isDataItemLockedWithTnx(this.tnx.getSender(), this.tnx.getTransactionNum()) ||
                    this.node.database.isDataItemLockedWithTnx(this.tnx.getReceiver(), this.tnx.getTransactionNum())) {
                isLocked = false;
                this.node.logger.log("IST:"+Utils.toDataStoreString(this.tnx)+"Not-Locked");
            }

            //Wait until locks released if locked.
            if (isLocked && (this.node.database.isDataItemLocked(this.tnx.getSender()) ||
                    this.node.database.isDataItemLocked(this.tnx.getReceiver()))) {
                failureReason = "Data Items Locked";
                this.node.logger.log("IST:"+Utils.toDataStoreString(this.tnx)+"Items are Locked-> waiting");
                Thread.sleep(100);
            }


            //If still locked, move on
            if (isLocked && (this.node.database.isDataItemLocked(this.tnx.getSender()) ||
                    this.node.database.isDataItemLocked(this.tnx.getReceiver()))) {
                failureReason = "Data Items Locked";
                this.node.logger.log("IST:"+Utils.toDataStoreString(this.tnx)+"Still Locked");
                success = false;
            }
            else {
                this.node.logger.log("IST:"+Utils.toDataStoreString(this.tnx)+"Still Locked");
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

                    int currentSeqNum;

                    if(this.node.database.transactionNumSeqNumMap.containsKey(this.tnx.getTransactionNum())){
                        currentSeqNum = this.node.database.transactionNumSeqNumMap.get(this.tnx.getTransactionNum());
                    }
                    else{
                        currentSeqNum = this.node.database.currentSeqNum.incrementAndGet();
                        this.node.database.addTransaction(currentSeqNum, tnx);
                    }

                    this.node.database.transactionNumSeqNumMap.put(this.tnx.getTransactionNum(), currentSeqNum);

                    this.node.database.setMaxAddedSeqNum(currentSeqNum);

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

                                if(!tnx.getIsCrossShard()) {
                                    this.node.sendExecutionReplyToClient(tnx, true, failureReason, "COMMITED");
                                }
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

                                if(this.tnx.getIsCrossShard() ){

                                    if(Utils.FindClusterOfDataItem(this.tnx.getSender()) == this.node.clusterNumber){
                                        this.isCrossShardSuccess.set(true);
                                    }
                                    else{
                                        this.node.logger.log("IST: Sending Cross Shard Prepare for SeqNum: " + currentSeqNum + " View: " +
                                                this.node.database.currentViewNum.get() + " Transaction ID: " +
                                                tnx.getTransactionNum());

                                        CommitRequest crossShardCommitRequest = CommitRequest.newBuilder()
                                                .setTransaction(this.tnx)
                                                .setAbort(false)
                                                .setProcessId(this.node.serverName)
                                                .setClusterId(this.node.clusterNumber)
                                                .setSequenceNumber(currentSeqNum)
                                                .build();

                                        this.node.logger.log("IST: Sending Cross Shard Prepare for SeqNum: " + currentSeqNum +
                                                " :: "+ crossShardCommitRequest);


                                        int coordinatorServerNumber = GlobalConfigs.primaryServers.get(Utils.FindClusterOfDataItem(this.tnx.getSender()));

                                        this.node.logger.log("IST: Coordinator Server Number: " + coordinatorServerNumber);

                                        //Send to coordinator
                                        this.node.serversToPaxosStub.get(coordinatorServerNumber).crossShardPrepare(crossShardCommitRequest);
                                        this.node.logger.log("IST: Sent Cross Shard Prepare for SeqNum: " + currentSeqNum );
                                        return;

                                    }

                                }




                            }
                            else {
                                failureReason = "Prepare Failed";
                                this.node.logger.log("Prepare Failed for SeqNum: " + currentSeqNum + " View: " +
                                        this.node.database.currentViewNum.get() + " Transaction ID: " +
                                        tnx.getTransactionNum());

                                success = false;
                            }


                    } else {
                        failureReason = "Pre Prepare Failed";
                        this.node.logger.log("Pre Prepare Failed for SeqNum: " + currentSeqNum + " View: " +
                                this.node.database.currentViewNum.get() + " Transaction ID: " +
                                tnx.getTransactionNum());
                    }



                }
                else{
                    failureReason = "Insufficient Balance";
                    success = false;
                    this.node.logger.log("IST:"+Utils.toDataStoreString(this.tnx)+" Insufficient Balance");
                    //this.node.database.transactionStatusMap.put( curre , TransactionStatus.ABORTED);
                }

            }

            if (!tnx.getIsCrossShard() && !success )
                this.node.sendExecutionReplyToClient(tnx, success, failureReason, "ABORTED");

        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            e.printStackTrace();
            this.node.logger.log("Error processing transaction " + this.tnx.getTransactionNum() + " " + e.getMessage());
        }
        finally {


        }


    }
}
