package org.cse535.node;

import org.cse535.configs.GlobalConfigs;
import org.cse535.configs.PBFTSignUtils;
import org.cse535.configs.Utils;
import org.cse535.database.DatabaseService;
import org.cse535.proto.*;
import org.cse535.threadimpls.CrossShardTnxProcessingThread;
import org.cse535.threadimpls.CrossShardTnxProcessingThreadExtraReceiver;
import org.cse535.threadimpls.IntraPrepareThread;
import org.cse535.threadimpls.IntraShardTnxProcessingThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Node extends NodeServer {

    public Thread transactionWorkerThread;

    public Node(Integer serverNum, int port) {
        super(serverNum, port);

        this.transactionWorkerThread = new Thread(this::processTnxsInQueue);


        try {
            this.server.start();
            this.transactionWorkerThread.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processTnxsInQueue() {
        logger.log("Transaction Worker Thread Started");
        while (true) {
            try {
                Thread.sleep(1);

                if( !this.isServerActive.get()){
                    //logger.log("Pausing Transaction Service until Server is Active");
                    Thread.sleep(100);
                    continue;
                }

               // this.database.initiateExecutions();

                // TransactionInputConfig transactionInput = this.database.incomingTransactionsQueue.take();

                TransactionInputConfig transactionInput = this.database.incomingTransactionsQueue.peek();
                if(transactionInput == null){
                    Thread.sleep(10);
                    continue;
                }

                Transaction transaction = transactionInput.getTransaction();

                int checkSeqNum = -1;

                if(this.database.transactionNumSeqNumMap.containsKey(transaction.getTransactionNum())){
                    checkSeqNum = this.database.transactionNumSeqNumMap.get(transaction.getTransactionNum());
                }

                if( checkSeqNum != -1 && this.database.transactionStatusMap.containsKey(checkSeqNum)){

                    if(this.database.transactionStatusMap.get(checkSeqNum) == TransactionStatus.EXECUTED){
                        reSendExecutionReplyToClient(transaction);
                    }

                }

//
//                if(this.database.transactionStatusMap.containsKey(transaction.getTransactionNum()) &&
//                        this.database.transactionStatusMap.get(transaction.getTransactionNum()) == TransactionStatus.EXECUTED){
//                    reSendExecutionReplyToClient(transaction);
//
//                }

                // Process the Transaction now.

                processIntraShardTransaction(transactionInput);

//                if(transaction.getIsCrossShard()){
//                    processCrossShardTransaction(transactionInput);
//                }
//                else {
//
//
//                }

                this.database.incomingTransactionsQueue.remove();

            } catch (InterruptedException e) {
                this.commandLogger.log("Line 143 ::: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public boolean processIntraShardTransaction(TransactionInputConfig transactionInput) {

        this.logger.log("Processing Transaction: " + transactionInput.getTransaction().getTransactionNum()
                + " " + Utils.toDataStoreString(transactionInput.getTransaction()) + " => "
                + Utils.CheckTransactionBelongToMyCluster(transactionInput.getTransaction(), this.serverNumber) );

        if(Utils.CheckTransactionBelongToMyCluster(transactionInput.getTransaction(), this.serverNumber)){
            this.logger.log("Processing Transaction: " + transactionInput.getTransaction().getTransactionNum());

            try {

                if(transactionInput.getTransaction().getIsCrossShard() &&
                        Utils.FindClusterOfDataItem(transactionInput.getTransaction().getSender()) == this.clusterNumber ) {

                    this.logger.log(("Cross Shard Started"));

                    if(transactionInput.getTransaction().getReceiver2() != 0){
                        CrossShardTnxProcessingThreadExtraReceiver crossShardTnxProcessingThread = new CrossShardTnxProcessingThreadExtraReceiver(this, transactionInput);
                        crossShardTnxProcessingThread.start();
                        Thread.sleep(100);
                    }
                    else{
                        CrossShardTnxProcessingThread crossShardTnxProcessingThread = new CrossShardTnxProcessingThread(this, transactionInput);
                        crossShardTnxProcessingThread.start();
                        Thread.sleep(100);
                    }


                }
//                else if(transactionInput.getTransaction().getIsCrossShard() &&
//                        Utils.FindClusterOfDataItem(transactionInput.getTransaction().getSender()) == this.clusterNumber ){
//                    this.logger.log(("Cross Thread Started"));
//                    CrossShardTnxProcessingThread crossShardTnxProcessingThread = new CrossShardTnxProcessingThread(this, transactionInput);
//                    crossShardTnxProcessingThread.start();
//                    Thread.sleep(100);
//                    //crossShardTnxProcessingThread.join();
//                }
                else{
                    this.logger.log(("Intra Thread Started"));
                    IntraShardTnxProcessingThread intraShardTnxProcessingThread = new IntraShardTnxProcessingThread(this,
                            transactionInput, new AtomicBoolean(false));
                    intraShardTnxProcessingThread.start();
                    intraShardTnxProcessingThread.join();
                }




            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return true;
        }
        else {
            this.logger.log("Transaction does not belong to my cluster: " + transactionInput.getTransaction().getTransactionNum());
        }
        return false;
    }

    public CrossTxnResponse processCrossShardTransaction(TransactionInputConfig transactionInputConfig) {
        return null;
    }

//    public CrossTxnResponse processCrossShardTransaction(TransactionInputConfig transactionInputConfig) {
//
//        try {
//
//            this.logger.log("Processing Cross Shard Transaction: " + transactionInputConfig.getTransaction().getTransactionNum());
//            //Sync data from other servers in the cluster.
//
//            CrossTxnResponse.Builder crossTxnResponse = CrossTxnResponse.newBuilder();
//            crossTxnResponse.setClusterId(this.clusterNumber);
//            crossTxnResponse.setSuccess(false);
//            crossTxnResponse.setServerName(this.serverName);
//            crossTxnResponse.setSuccessPreparesCount(0);
//            crossTxnResponse.setFailureReason("");
//            crossTxnResponse.setBallotNumber(-1);
//
//            int sender = transactionInputConfig.getTransaction().getSender();
//            int receiver = transactionInputConfig.getTransaction().getReceiver();
//            int amount = transactionInputConfig.getTransaction().getAmount();
//
//            if ( this.database.isDataItemLocked(sender) ||  this.database.isDataItemLocked(receiver)) {
//                crossTxnResponse.setFailureReason("Data Item Locked");
//                this.logger.log("Data Item Locked.... so rejecting the transaction");
//                return crossTxnResponse.build();
//            }
//
//
//            boolean belongsToCluster = false;
//
//            int itemToLock = sender;
//
//            if (Utils.FindClusterOfDataItem(sender) == this.clusterNumber) {
//                this.logger.log("Processing Cross Shard Transaction - Sender : " + transactionInputConfig.getTransaction().getTransactionNum());
//                belongsToCluster = true;
//
//                if (!this.database.isValidTransaction(transactionInputConfig.getTransaction())) {
//                    crossTxnResponse.setFailureReason("Insufficient Balance");
//                    return crossTxnResponse.build();
//                }
//
//            }
//            else if (Utils.FindClusterOfDataItem(receiver) == this.clusterNumber) {
//                this.logger.log("Processing Cross Shard Transaction - Receiver : " + transactionInputConfig.getTransaction().getTransactionNum());
//                belongsToCluster = true;
//                itemToLock = receiver;
//
//            }
//            else {
//                this.logger.log("Transaction does not belong to my cluster: " + transactionInputConfig.getTransaction().getTransactionNum());
//
//                return crossTxnResponse.build();
//            }
//
//
//            // Lock the data item
//
//            this.database.lockDataItem(itemToLock, transactionInputConfig.getTransaction().getTransactionNum());
//            this.logger.log("Locked Data Item: " + itemToLock);
//
//            // Prepare Request to cluster servers and return Response
//
//            int ballotNumber = this.database.ballotNumber.incrementAndGet();
//            crossTxnResponse.setBallotNumber(ballotNumber);
//
//            this.logger.log("Ballot Number: " + ballotNumber);
//
//            IntraPrepareThread[] intraPrepareThreads = new IntraPrepareThread[GlobalConfigs.numServersPerCluster];
//
//            ConcurrentHashMap<Integer, PrepareResponse> prepareResponses = new ConcurrentHashMap<>();
//            AtomicInteger successPrepares = new AtomicInteger(1);
//
//            PrepareRequest.Builder prepareBuilder = PrepareRequest.newBuilder();
//
//            prepareBuilder.setBallotNumber(ballotNumber)
//                    .setProcessId(this.serverName)
//                    .setTransaction(transactionInputConfig.getTransaction());
//
//            if (this.database.lastCommittedTransaction != null) {
//                prepareBuilder.setLatestCommittedTransaction(this.database.lastCommittedTransaction);
//            }
//
//            prepareBuilder.setLatestCommittedBallotNumber(this.database.lastCommittedBallotNumber)
//                    .setClusterId(this.clusterNumber);
//
//
//            PrepareRequest prepareRequest = prepareBuilder.build();
//            this.logger.log("Prepare Request: " + prepareRequest.toString());
//            try {
//
//                for (int i = 0; i < GlobalConfigs.numServersPerCluster; i++) {
//                    if (Objects.equals(GlobalConfigs.clusterToServersMap.get(this.clusterNumber).get(i), this.serverNumber)) {
//                        continue;
//                    }
//                    intraPrepareThreads[i] = new IntraPrepareThread(this, prepareRequest, prepareResponses, GlobalConfigs.clusterToServersMap.get(this.clusterNumber).get(i), successPrepares);
//                    intraPrepareThreads[i].start();
//                }
//
//                this.database.addTransactionStatus(ballotNumber, TransactionStatus.PREPARED);
//
//                for (int i = 0; i < GlobalConfigs.numServersPerCluster; i++) {
//                    if (intraPrepareThreads[i] == null) continue;
//                    intraPrepareThreads[i].join();
//                }
//
//            } catch (Exception e) {
//                this.logger.log("Error " + e.getMessage());
//            }
//
//
//            if (successPrepares.get() < GlobalConfigs.ShardConsesusThreshold) {
//                this.logger.log("Consensus FAILED for Cross Shard Prepare Phase");
//
//                // Retry Prepare phase if Synced
//                PrepareResponse syncPrepareResponse = null;
//
//                int maxCommittedBallotNumber = -1;
//
//                for (PrepareResponse r : prepareResponses.values()) {
//                    if (r.getNeedToSync()) {
//                        if (r.getLastCommittedBallotNumber() > maxCommittedBallotNumber) {
//                            maxCommittedBallotNumber = r.getLastCommittedBallotNumber();
//                            syncPrepareResponse = r;
//                        }
//                    }
//                }
//
//                if (syncPrepareResponse != null) {
//                    // Sync the data
//
//                    this.logger.log("Sync Prepare Response: " + syncPrepareResponse);
//
//
//                    this.syncData(syncPrepareResponse);
//                    ballotNumber = this.database.ballotNumber.incrementAndGet();
//                    Thread.sleep(10);
//
//                    this.logger.log("Synced data ... Now Retrying Prepare phase");
//
//                    this.logger.log(syncPrepareResponse.toString());
//
//
//                    intraPrepareThreads = new IntraPrepareThread[GlobalConfigs.numServersPerCluster];
//
//                    prepareResponses = new ConcurrentHashMap<>();
//                    successPrepares = new AtomicInteger(1);
//
//                    prepareBuilder = PrepareRequest.newBuilder();
//
//                    prepareBuilder.setBallotNumber(ballotNumber)
//                            .setProcessId(this.serverName)
//                            .setTransaction(transactionInputConfig.getTransaction());
//
//                    if (this.database.lastCommittedTransaction != null) {
//                        prepareBuilder.setLatestCommittedTransaction(this.database.lastCommittedTransaction);
//                    }
//
//                    prepareBuilder.setLatestCommittedBallotNumber(this.database.lastCommittedBallotNumber)
//                            .setClusterId(this.clusterNumber);
//
//
//                    prepareRequest = prepareBuilder.build();
//
//                    for (int i = 0; i < GlobalConfigs.numServersPerCluster; i++) {
//                        if (Objects.equals(GlobalConfigs.clusterToServersMap.get(this.clusterNumber).get(i), this.serverNumber)) {
//                            continue;
//                        }
//                        intraPrepareThreads[i] = new IntraPrepareThread(this, prepareRequest, prepareResponses, GlobalConfigs.clusterToServersMap.get(this.clusterNumber).get(i), successPrepares);
//                        intraPrepareThreads[i].start();
//                    }
//
//                    this.database.addTransactionStatus(ballotNumber, TransactionStatus.PREPARED);
//
//                    for (int i = 0; i < GlobalConfigs.numServersPerCluster; i++) {
//                        if (intraPrepareThreads[i] == null) continue;
//                        intraPrepareThreads[i].join();
//                    }
//                }
//
//
//                crossTxnResponse.setSuccessPreparesCount(successPrepares.get());
//                if(successPrepares.get() >= GlobalConfigs.ShardConsesusThreshold ){
//                    crossTxnResponse.setSuccess(true);
//                }
//                else {
//                    crossTxnResponse.setSuccess(false);
//                    crossTxnResponse.setFailureReason("Consensus not reached");
//                }
//                return crossTxnResponse.build();
//            } else {
//                this.logger.log("Consensus Reached for Cross Shard Prepare Phase");
//                crossTxnResponse.setSuccess(true);
//                crossTxnResponse.setSuccessPreparesCount(successPrepares.get());
//
//                this.database.addToDataStore(prepareRequest);
//
//                // Write Log Ahead
//                this.database.writeToWAL(prepareRequest.getTransaction());
//
//            }
//            return crossTxnResponse.build();
//        }
//        catch (Exception e){
//            this.logger.log("Error in Cross Shard Transaction: " + e.getMessage());
//            return CrossTxnResponse.newBuilder().setSuccess(false).build();
//        }
//        finally {
//           // this.pauseTnxServiceDueToCrossShard = false;
//        }
//    }
//
//    public void addSyncDataToPrepareResponse( PrepareResponse.Builder prepareResponse, PrepareRequest request){
//        // add all Transactions & statuses after request's committed tnx
//        for (int i = request.getLatestCommittedBallotNumber()+1; i <= this.database.ballotNumber.get() ; i++) {
//
//            Transaction transaction = this.database.getTransaction(i);
//            if(transaction.getTransactionNum() != -1){
//                prepareResponse.putSyncTransactionsMap(i, transaction);
//            }
//            TransactionStatus status = this.database.getTransactionStatus(i);
//            if(status != TransactionStatus.PENDING){
//                prepareResponse.putSyncTransactionStatusMap(i, status);
//            }
//        }
//
//        // add total balances map after request's committed tnx
//        //prepareResponse.putAllSyncBalancesMap(this.database.getAllBalances());
//        prepareResponse.setLatestBallotNumber(this.database.ballotNumber.get());
//        prepareResponse.setLastCommittedBallotNumber(this.database.lastCommittedBallotNumber);
//        if(this.database.lastCommittedTransaction != null)
//            prepareResponse.setLastCommittedTransaction(this.database.lastCommittedTransaction);
//
//        prepareResponse.setNeedToSync(true);
//    }

//    public void syncData(PrepareResponse syncPrepareResponse) {
//        this.logger.log("Data Post Sync -------------");
//
//
//        this.database.ballotNumber.set( Math.max(syncPrepareResponse.getLatestBallotNumber() , this.database.ballotNumber.get()) );
//
//        // Sync Transactions
//        for (int i = syncPrepareResponse.getLastAcceptedUncommittedBallotNumber()+1; i <= syncPrepareResponse.getLatestBallotNumber() ; i++) {
//            if(syncPrepareResponse.getSyncTransactionsMapMap().containsKey(i)){
//                this.database.addTransaction(i, syncPrepareResponse.getSyncTransactionsMapMap().get(i));
//                this.logger.log("Synced Transaction: " + i);
//                this.database.executeTransaction(syncPrepareResponse.getSyncTransactionsMapMap().get(i));
//            }
//            if(syncPrepareResponse.getSyncTransactionStatusMapMap().containsKey(i)){
//                this.database.addTransactionStatus(i, syncPrepareResponse.getSyncTransactionStatusMapMap().get(i));
//                this.logger.log("Synced Transaction Status: " + i);
//            }
//        }
//
//        this.database.lastCommittedBallotNumber = syncPrepareResponse.getLastCommittedBallotNumber();
//        this.database.lastCommittedTransaction = syncPrepareResponse.getLastCommittedTransaction();
//
//        this.database.lastAcceptedUncommittedTransaction = syncPrepareResponse.getLastAcceptedUncommittedTransaction();
//        this.database.lastAcceptedUncommittedBallotNumber = syncPrepareResponse.getLastAcceptedUncommittedBallotNumber();
//
//        // RunUpdateBalancesWithTheseNewTransactions
//
//        // Sync Balances
//        //this.database.updateBalances(syncPrepareResponse.getSyncBalancesMapMap());
//    }



    public PrePrepareResponse handlePrePreparePhase(PrePrepareRequest request) {
        PrePrepareResponse.Builder prePrepareResponse = PrePrepareResponse.newBuilder();

        prePrepareResponse.setProcessId(this.serverName);
        prePrepareResponse.setSuccess(false);
        prePrepareResponse.setSequenceNumber(request.getSequenceNumber());
        prePrepareResponse.setClusterId(this.clusterNumber);

        this.logger.log("PrePrepare Request received from " + request.getProcessId() );


        this.logger.log("PrePrepare request received: " + request.getSequenceNumber() + " Transaction ID: "+ request.getTransaction().getTransactionNum()
                + " Digest: " + request.getDigest()
        );

        if(this.database.transactionMap.containsKey(request.getSequenceNumber())){

            if( this.database.transactionMap.get(request.getSequenceNumber()).getTransactionNum() == request.getTransaction().getTransactionNum() &&
                    this.database.transactionMap.get(request.getSequenceNumber()).getTransactionHash().equals(request.getTransaction().getTransactionHash())){

                this.database.transactionStatusMap.put(request.getSequenceNumber(), TransactionStatus.PrePREPARED);

                this.logger.log("Accepted ---- PrePrepare request accepted: " + request.getSequenceNumber() +
                        " Transaction ID: "+ request.getTransaction().getTransactionNum()
                        + " Digest: " + request.getDigest()
                );

                prePrepareResponse.setSuccess(true);
            }


            this.logger.log("Rejected ---- PrePrepare request rejected: " + request.getSequenceNumber() +
                    " Transaction ID: "+ request.getTransaction().getTransactionNum() + " Transaction ID from db map : " + this.database.transactionMap.get(request.getSequenceNumber()).getTransactionNum()
                    + " Tnx Hash matching : " + this.database.transactionMap.get(request.getSequenceNumber()).getTransactionHash().equals(request.getTransaction().getTransactionHash())
                    + "Transaction Hash: " + this.database.transactionMap.get(request.getSequenceNumber()).getTransactionHash() + " Request Hash: " + request.getTransaction().getTransactionHash()
                    + " Digest: " + request.getDigest()
            );


        }
        else {
            this.database.transactionMap.put(request.getSequenceNumber(), request.getTransaction());
            this.database.seqNumViewMap.put(request.getSequenceNumber(), request.getView());
            this.database.transactionStatusMap.put(request.getSequenceNumber(), TransactionStatus.PrePREPARED);

            prePrepareResponse.setSuccess(true);

            this.logger.log("Accepted ---- PrePrepare request accepted: " + request.getSequenceNumber() +
                    " Transaction ID: "+ request.getTransaction().getTransactionNum()
                    + " Digest: " + request.getDigest()
            );
        }

        this.database.setMaxAddedSeqNum(request.getSequenceNumber());

        this.database.prePrepareRequestMap.put(request.getSequenceNumber(), request);

        return prePrepareResponse.build();
    }


    public PrepareResponse handlePreparePhase(PrepareRequest request) {

        PrepareResponse.Builder prepareResponse = PrepareResponse.newBuilder();
        prepareResponse.setProcessId(this.serverName);
        prepareResponse.setSequenceNumber(request.getSequenceNumber());
        prepareResponse.setSuccess(false);

        if( this.database.transactionMap.containsKey(request.getSequenceNumber()) &&
                this.database.transactionStatusMap.containsKey(request.getSequenceNumber()) &&

                this.database.prePrepareRequestMap.get(request.getSequenceNumber()) != null &&

                PBFTSignUtils.verifySignature(this.database.prePrepareRequestMap.get(request.getSequenceNumber()).toString(), request.getDigest(),
                        GlobalConfigs.serversToSignKeys.get(request.getProcessId()).getPublic() ) &&

                (this.database.transactionStatusMap.get(request.getSequenceNumber()) == TransactionStatus.PrePREPARED ||
                        this.database.transactionStatusMap.get(request.getSequenceNumber()) == TransactionStatus.PREPARED)){

            this.database.transactionStatusMap.put(request.getSequenceNumber(), TransactionStatus.PREPARED);

            this.logger.log("Accepted ---- Prepare request accepted: " + request.getSequenceNumber() + " Digest: " + request.getDigest());
            prepareResponse.setSuccess(true);
        }

        this.database.setMaxAddedSeqNum(request.getSequenceNumber());

        this.database.prepareRequestMap.put(request.getSequenceNumber(), request);

        return prepareResponse.build();
    }


    public CommitResponse handleCommit(CommitRequest request) {
        CommitResponse.Builder commitResponse = CommitResponse.newBuilder();
        commitResponse.setProcessId(this.serverName);
        commitResponse.setSequenceNumber(request.getSequenceNumber());
        commitResponse.setSuccess(false);

        this.database.transactionStatusMap.put(request.getSequenceNumber(), TransactionStatus.COMMITTED);

        this.database.initiateExecutions();


        if(
                request.getSequenceNumber() <= this.database.maxAddedSeqNum.get() &&

                ( this.database.transactionStatusMap.get(request.getSequenceNumber()) == TransactionStatus.PREPARED ||
                        this.database.transactionStatusMap.get(request.getSequenceNumber()) == TransactionStatus.COMMITTED
                        ) &&

                this.database.prepareRequestMap.get(request.getSequenceNumber()) != null &&

                        PBFTSignUtils.verifySignature(this.database.prepareRequestMap.get(request.getSequenceNumber()).toString(), request.getDigest(),
                                GlobalConfigs.serversToSignKeys.get(request.getProcessId()).getPublic() )

        )
        {
            if(!request.hasTransaction()){
                request = request.toBuilder().setTransaction(this.database.transactionMap.get(request.getSequenceNumber())).build();
            }

            if(request.getTransaction().getIsCrossShard()){
                this.database.addCrossShardPrepareToDataStore(request);
            }
            else{
                this.database.addToDataStore(request);
            }

            commitResponse.setSuccess(true);

        }

        return commitResponse.build();
    }

    public CommitResponse handleCrossShardCommit(CommitRequest request) {
        CommitResponse.Builder commitResponse = CommitResponse.newBuilder();
        commitResponse.setProcessId(this.serverName);
        commitResponse.setSequenceNumber(request.getSequenceNumber());
        commitResponse.setSuccess(false);

        if(request.getAbort()){
            this.logger.log("Abort Request Received from " + request.getProcessId() );
            this.database.addTransactionStatus(request.getSequenceNumber(), TransactionStatus.ABORTED);
            this.database.transactionStatusMap.put(request.getSequenceNumber(), TransactionStatus.ABORTED);

            this.database.rollbackWAL(request.getTransaction().getTransactionNum());
            commitResponse.setSuccess(true);
        }
        else {
            this.logger.log("Commit Request Received from " + request.getProcessId());
            commitResponse.setSuccess(true);
            this.database.addTransactionStatus(request.getSequenceNumber(), TransactionStatus.COMMITTED);
            this.database.transactionStatusMap.put(request.getSequenceNumber(), TransactionStatus.COMMITTED);

            this.logger.log("Commit Request Accepted from " + request.getProcessId());
            this.database.commitWAL(request.getTransaction().getTransactionNum());
        }

        this.database.addCrossShardCommitToDataStore(request);

        this.database.unlockDataItem(request.getTransaction().getSender(), request.getTransaction().getTransactionNum());
        this.database.unlockDataItem(request.getTransaction().getReceiver(), request.getTransaction().getTransactionNum());

        return commitResponse.build();
    }


    public void reSendExecutionReplyToClient(Transaction transaction) {
        // Send reply to Client

        if(transaction == null){
            return;
        }

        int checkSeqNum = -1;

        if(this.database.transactionNumSeqNumMap.containsKey(transaction.getTransactionNum())){
            checkSeqNum = this.database.transactionNumSeqNumMap.get(transaction.getTransactionNum());
        }

        if(checkSeqNum == -1){
            return;
        }

        //this.database.initiateExecutions();


       if(this.database.transactionStatusMap.get(checkSeqNum) == TransactionStatus.COMMITTED){
           this.logger.log("Sending Execution Reply to Client: COMMITTED" + transaction.getTransactionNum());
            sendExecutionReplyToClient(transaction, true, "", "COMMITTED");
        }
        else if(this.database.transactionStatusMap.get(checkSeqNum) == TransactionStatus.ABORTED){
            this.logger.log("Sending Execution Reply to Client: ABORTED" + transaction.getTransactionNum());
            sendExecutionReplyToClient(transaction, false, "Transaction Failed", "ABORTED");
        }
        else if(this.database.transactionStatusMap.get(checkSeqNum) == TransactionStatus.EXECUTED){
            this.logger.log("Sending Execution Reply to Client: EXECUTED" + transaction.getTransactionNum());
            sendExecutionReplyToClient(transaction, true, "", "EXECUTED");
        }
        this.logger.log("Sent Execution Reply to Client: " + transaction.getTransactionNum());
    }



    public void sendExecutionReplyToClient(Transaction transaction, boolean success, String failureReason, String status) {
        // Send reply to Client
        ExecutionReply.Builder executionReply = ExecutionReply.newBuilder();
        executionReply.setTransactionId(transaction.getTransactionNum());
        executionReply.setStatus(status);
        executionReply.setSuccess(success);
        executionReply.setProcessId(this.serverName);
        executionReply.setClusterId(this.clusterNumber);
        executionReply.setFailureReason(failureReason);
        this.serversToPaxosStub.get(0).execReply(executionReply.build());
        this.logger.log("Sent Execution Reply to Client: " + transaction.getTransactionNum() + " : " + executionReply.build().toString());
    }

    public String PrintDataStore(){
        return this.serverName + " : " + String.join(" -> ", this.database.dataStore.get());
    }



}
