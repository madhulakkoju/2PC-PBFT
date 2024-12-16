package org.cse535.threadimpls;

import org.cse535.configs.GlobalConfigs;
import org.cse535.configs.PBFTSignUtils;
import org.cse535.configs.Utils;
import org.cse535.loggers.LogUtils;
import org.cse535.node.Node;
import org.cse535.proto.*;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CrossShardTnxProcessingThreadExtraReceiver extends Thread {

    public Node node;
    public TransactionInputConfig transactionInputConfig;

    public Transaction tnx;


    public CrossShardTnxProcessingThreadExtraReceiver(Node viewServer, TransactionInputConfig transactionInputConfig) {
        this.node = viewServer;
        this.transactionInputConfig = transactionInputConfig;
        this.tnx = transactionInputConfig.getTransaction();
    }

    public void run(){
        String failureReason = "";
        boolean success = false;
        LogUtils logger = this.node.logger;

        try{
            if(this.tnx == null){
                this.node.logger.log("CST: Transaction is null");
                return;
            }

            this.node.logger.log("CST=="+Utils.toDataStoreString(this.tnx) + " "+ "Processing Cross Shard Transaction WITH EXTRA RECEIVER2");

            //this.node.logger.log(this.transactionInputConfig.toString());

//            if( PBFTSignUtils.verifySignature( this.tnx.getTransactionHash() , this.transactionInputConfig.getDigest(),
//                    GlobalConfigs.serversToSignKeys.get(this.transactionInputConfig.getProcessId()).getPublic() )
//            ){
//
//                this.node.logger.log("CST: Signature Verification Failed for Transaction: " + this.tnx.getTransactionNum());
//                return;
//            }

        Transaction transaction = this.transactionInputConfig.getTransaction();
        logger.log("CST=="+Utils.toDataStoreString(transaction) + " "+ "Processing Cross Shard Transaction");

        boolean isCoordinator = Utils.FindClusterOfDataItem(transaction.getSender()) == this.node.clusterNumber;
            AtomicBoolean isCrossShardPrepareSuccess = new AtomicBoolean(false);

            if(isCoordinator) {

                int receiverCluster = Utils.FindClusterOfDataItem(transaction.getReceiver());
                int receiverPrimary = GlobalConfigs.primaryServers.get(receiverCluster);

                int receiver2Cluster = Utils.FindClusterOfDataItem(transaction.getReceiver2());
                int receiver2Primary = GlobalConfigs.primaryServers.get(receiver2Cluster);

                int currentClusterSeqNum = -1;
                int receiverClusterSeqNum = -1;
                int receiver2ClusterSeqNum = -1;




                boolean isLocked = true;

                if (this.node.database.isDataItemLockedWithTnx(transaction.getSender(), transaction.getTransactionNum()) ||
                        this.node.database.isDataItemLockedWithTnx(transaction.getReceiver(), transaction.getTransactionNum())) {
                    isLocked = false;
                }

                //Wait until locks released if locked.
                if (isLocked && (this.node.database.isDataItemLocked(transaction.getSender()) ||
                        this.node.database.isDataItemLocked(transaction.getReceiver()))) {
                    failureReason += "Data Items Locked";
                    success = false;
                    Thread.sleep(1000);
                }


                //If still locked, move on
                if (isLocked && (this.node.database.isDataItemLocked(this.tnx.getSender()) ||
                        this.node.database.isDataItemLocked(this.tnx.getReceiver()))) {
                    failureReason += "Data Items Locked";
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
                    this.node.database.lockDataItem(this.tnx.getReceiver2(), this.tnx.getTransactionNum());

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
                                .setDigest(PBFTSignUtils.signMessage( this.tnx.getTransactionHash() , GlobalConfigs.serversToSignKeys.get(this.node.serverName).getPrivate() ) )
                                .build();

                        // this.logger.log(prePrepareRequest.toString());

                        this.node.logger.log("CST=="+"Initiating Pre Prepare for SeqNum: " + currentSeqNum + " View: " +
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
                                    .setDigest(PBFTSignUtils.signMessage( prePrepareRequest.toString() ,
                                            GlobalConfigs.serversToSignKeys.get(this.node.serverName).getPrivate() ) )
                                    .build();

                            this.node.logger.log("CST=="+"Initiating Prepare for SeqNum: " + currentSeqNum + " View: " +

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

                                this.node.logger.log("CST=="+"Prepare Success for SeqNum: " + currentSeqNum + " View: " +
                                        this.node.database.currentViewNum.get() + " Transaction ID: " +
                                        tnx.getTransactionNum());

                                this.node.logger.log("CST=="+"Initiating Commit for SeqNum: " + currentSeqNum + " View: " +
                                        this.node.database.currentViewNum.get() + " Transaction ID: " +
                                        tnx.getTransactionNum());

                                //this.node.sendExecutionReplyToClient(tnx, true, failureReason, "COMMITED");

                                CommitRequest commitRequest = CommitRequest.newBuilder()
                                        .setSequenceNumber(currentSeqNum)
                                        .setView(this.node.database.currentViewNum.get())
                                        .setProcessId(this.node.serverName)
                                        .setTransaction(this.tnx)
                                        .setDigest(PBFTSignUtils.signMessage( prePrepareRequest.toString() ,
                                                GlobalConfigs.serversToSignKeys.get(this.node.serverName).getPrivate() ) )
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

                                this.node.database.addCrossShardPrepareToDataStore(commitRequest);

                                //this.node.handleCommit(commitRequest);
                                // Execute Transactions

                                this.node.database.transactionStatusMap.put(currentSeqNum, TransactionStatus.COMMITTED);
                                this.node.database.initiateExecutions();

                                isCrossShardPrepareSuccess.set(true);


                                //Wait for all servers to respond
                                for (int j = 0; j < i; j++) {
                                    intraCommitThreads[j].join();
                                }






                                logger.log("CST=="+Utils.toDataStoreString(transaction) + " "+ "Coordinator for Cross Shard Transaction");

                                this.node.database.crossShardPrepareResponses.put(transaction.getTransactionNum(), new HashMap<>());

                                // Coordinate between shards


                                logger.log("CST=="+Utils.toDataStoreString(transaction) + " "+ "Starting Intra Shard Transaction Processing");


                                logger.log("CST=="+Utils.toDataStoreString(transaction) + " "+ "Intra Shard Transaction Processing Completed");

                                currentClusterSeqNum = this.node.database.transactionNumSeqNumMap.get(transaction.getTransactionNum());

                                logger.log("CST=="+Utils.toDataStoreString(transaction) + " "+ "Current Cluster Seq Num: " + currentClusterSeqNum + " Prep Success? " + isCrossShardPrepareSuccess.get());

                                //Coordinator Prepare Phase completed
                                if(isCrossShardPrepareSuccess.get()){
                                    logger.log("CST=="+Utils.toDataStoreString(transaction) + " "+ "Cross Shard - current Shard Prepare Success");
                                    // Send to other shard for Prepare



                                    logger.log("CST=="+Utils.toDataStoreString(transaction) + " "+ "Sending Cross Shard Prepare to " + receiverCluster);



                                    TransactionInputConfig crossShardPrepareRequest = TransactionInputConfig.newBuilder()
                                            .setTransaction(transaction)
                                            .setProcessId(this.node.serverName)
                                            .setDigest(PBFTSignUtils.signMessage( transaction.toString() ,
                                                    GlobalConfigs.serversToSignKeys.get(this.node.serverName).getPrivate() ) )
                                            .setSetNumber(transactionInputConfig.getSetNumber())
                                            .setView(this.node.database.currentViewNum.get())
                                            .addAllServerNames(transactionInputConfig.getServerNamesList())
                                            .addAllPrimaryServers(transactionInputConfig.getPrimaryServersList())
                                            .build();


                                    logger.log("CST=="+Utils.toDataStoreString(transaction) + " "+ "Sending Cross Shard Prepare \n" + crossShardPrepareRequest.toString());

                                    this.node.serversToPaxosStub.get(receiverPrimary).request(crossShardPrepareRequest);


                                    if(receiver2Cluster != receiverCluster && receiver2Cluster != this.node.clusterNumber){
                                        // Send to other shard for Prepare
                                        logger.log("CST=="+Utils.toDataStoreString(transaction) + " Receiver 2 Cluster "+ "Sending Cross Shard Prepare \n" + crossShardPrepareRequest.toString());

                                        this.node.serversToPaxosStub.get(receiver2Primary).request(crossShardPrepareRequest);

                                    }



                                    //Wait for response from other shard
                                    Thread.sleep((long) (GlobalConfigs.TransactionTimeout*1.5));

                                    boolean recieverClusterPrepSuccess = false;
                                    boolean reciever2ClusterPrepSuccess = receiverCluster == receiver2Cluster;

                                    if(this.node.database.crossShardPrepareResponses.containsKey(transaction.getTransactionNum())
                                            && this.node.database.crossShardPrepareResponses.get(transaction.getTransactionNum()).containsKey(receiverCluster) ){

                                        if(!this.node.database.crossShardPrepareResponses.get(transaction.getTransactionNum()).get(receiverCluster).getAbort()){
                                            recieverClusterPrepSuccess = true;
                                        }
                                    }

                                    if(receiver2Cluster != receiverCluster && receiver2Cluster != this.node.clusterNumber){

                                        if(this.node.database.crossShardPrepareResponses.containsKey(transaction.getTransactionNum())
                                                && this.node.database.crossShardPrepareResponses.get(transaction.getTransactionNum()).containsKey(receiver2Cluster) ){

                                            if(!this.node.database.crossShardPrepareResponses.get(transaction.getTransactionNum()).get(receiver2Cluster).getAbort()){
                                                reciever2ClusterPrepSuccess = true;
                                            }
                                        }

                                    }

                                    logger.log("CST=="+Utils.toDataStoreString(transaction) + " "+ "Cross Shard Prepare Success");

                                    if(recieverClusterPrepSuccess && reciever2ClusterPrepSuccess){
                                        this.node.logger.log("CST=="+Utils.toDataStoreString(transaction) + " "+ "Cross Shard - Receiver end Prepare Success");

                                        //Check if response received
                                        // if received, Commit in this shard

                                        commitRequest = CommitRequest.newBuilder()
                                                .setTransaction(transaction)
                                                .setAbort(false)
                                                .setProcessId(this.node.serverName)
                                                .setClusterId(this.node.clusterNumber)
                                                .setSequenceNumber(currentClusterSeqNum)
                                                .build();


                                        sendCommit(commitRequest, this.node.clusterNumber);

                                        logger.log("CST=="+Utils.toDataStoreString(transaction) + " "+ "Cross Shard Commit Sending to sender cluster ");
                                        Thread.sleep(GlobalConfigs.TransactionTimeout);

                                        logger.log("CST=="+Utils.toDataStoreString(transaction) + " "+ "Cross Shard Prepare Responses: " +
                                                this.node.database.crossShardPrepareResponses.toString());

                                        logger.log("CST=="+Utils.toDataStoreString(transaction) + " "+ "Cross Shard Prepare Responses: " +
                                                this.node.database.crossShardPrepareResponses.get(transaction.getTransactionNum()).toString());

                                        logger.log("CST== Receiver Cluster response:: "+this.node.database.crossShardPrepareResponses
                                                .get(transaction.getTransactionNum()).get(receiverCluster));

                                        // Commit in Other shard
                                        receiverClusterSeqNum = this.node.database.crossShardPrepareResponses
                                                .get(transaction.getTransactionNum()).get(receiverCluster).getSequenceNumber();

                                        CommitRequest commitRequestReceiver = CommitRequest.newBuilder()
                                                .setTransaction(transaction)
                                                .setAbort(false)
                                                .setProcessId(this.node.serverName)
                                                .setClusterId(receiverCluster)
                                                .setSequenceNumber(receiverClusterSeqNum)
                                                .build();

                                        sendCommit(commitRequestReceiver, this.node.clusterNumber);


                                        // Commit in Other shard - receiver 2
                                        receiver2ClusterSeqNum = this.node.database.crossShardPrepareResponses
                                                .get(transaction.getTransactionNum()).get(receiverCluster).getSequenceNumber();

                                        CommitRequest commitRequest2Receiver = CommitRequest.newBuilder()
                                                .setTransaction(transaction)
                                                .setAbort(false)
                                                .setProcessId(this.node.serverName)
                                                .setClusterId(receiver2Cluster)
                                                .setSequenceNumber(receiver2ClusterSeqNum)
                                                .build();

                                        sendCommit(commitRequest2Receiver, this.node.clusterNumber);




                                        logger.log("CST=="+Utils.toDataStoreString(transaction) + " "+ "Cross Shard Commit Sending to receiver cluster");

                                    }
                                    else{
                                        this.node.logger.log("CST=="+Utils.toDataStoreString(transaction) + " "+ "Cross Shard Prepare Failed");
                                        //ABORT now

                                        commitRequest = CommitRequest.newBuilder()
                                                .setTransaction(transaction)
                                                .setAbort(true)
                                                .setProcessId(this.node.serverName)
                                                .setClusterId(this.node.clusterNumber)
                                                .setSequenceNumber(currentClusterSeqNum)
                                                .build();

                                        // If not received, ABORT and WAL Rollback
                                        sendCommit(commitRequest, this.node.clusterNumber);

                                        commitRequest = CommitRequest.newBuilder()
                                                .setTransaction(transaction)
                                                .setAbort(true)
                                                .setProcessId(this.node.serverName)
                                                .setClusterId(receiverCluster)
                                                .setSequenceNumber(receiverClusterSeqNum)
                                                .build();

                                        // If not received, ABORT and WAL Rollback
                                        sendCommit(commitRequest, receiverCluster);

                                        if(receiver2Cluster != receiverCluster && receiver2Cluster != this.node.clusterNumber){
                                            commitRequest = CommitRequest.newBuilder()
                                                    .setTransaction(transaction)
                                                    .setAbort(true)
                                                    .setProcessId(this.node.serverName)
                                                    .setClusterId(receiver2Cluster)
                                                    .setSequenceNumber(receiver2ClusterSeqNum)
                                                    .build();
                                            //Abort
                                            sendCommit(commitRequest, receiver2Cluster);
                                        }

                                        return;
                                    }
                                }

                            }
                            else {
                                failureReason += "Prepare Failed";
                                this.node.logger.log("CST=="+"Prepare Failed for SeqNum: " + currentSeqNum + " View: " +
                                        this.node.database.currentViewNum.get() + " Transaction ID: " +
                                        tnx.getTransactionNum());

                                success = false;
                            }


                        } else {
                            failureReason += "Pre Prepare Failed";
                            this.node.logger.log("CST=="+"Pre Prepare Failed for SeqNum: " + currentSeqNum + " View: " +
                                    this.node.database.currentViewNum.get() + " Transaction ID: " +
                                    tnx.getTransactionNum());
                        }



                    }
                    else{
                        failureReason += "Insufficient Balance";
                        success = false;
                        //this.node.database.transactionStatusMap.put(tnx.getTransactionNum(), TransactionStatus.ABORTED);
                    }

                }



            }

        }
        catch (Exception e){
            System.err.println("Error occurred while processing cross shard transaction: " + e.getMessage());
            this.node.logger.log( "CST=="+Utils.toDataStoreString(this.transactionInputConfig.getTransaction()) + " "+  "Error occurred while processing cross shard transaction: " + e.getMessage());
        }
        finally {
            if(!success){

                this.node.sendExecutionReplyToClient(tnx, false, failureReason, "ABORTED");
            }
        }

    }




    public void sendCommit(CommitRequest request, int cluster){

        for(int serverId : GlobalConfigs.clusterToServersMap.get(cluster)){
//            if(serverId == this.node.serverNumber){
//                continue;
//            }

            Thread thread = new Thread(() -> {
                try {
                    this.node.serversToPaxosStub.get(serverId).crossShardCommit(request);
                } catch (Exception e) {
                    System.err.println("Error occurred while processing receiver response: " + e.getMessage());
                    this.node.logger.log( "CST=="+Utils.toDataStoreString(this.transactionInputConfig.getTransaction()) + " "+  "Error occurred while processing receiver response: " + e.getMessage());
                }
            });
            thread.start();
        }
    }


}
