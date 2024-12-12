package org.cse535.node;

import org.cse535.configs.GlobalConfigs;
import org.cse535.configs.Utils;
import org.cse535.proto.*;
import org.cse535.threadimpls.IntraPrepareThread;
import org.cse535.threadimpls.IntraShardTnxProcessingThread;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
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

                // TransactionInputConfig transactionInput = this.database.incomingTransactionsQueue.take();

                TransactionInputConfig transactionInput = this.database.incomingTransactionsQueue.peek();
                if(transactionInput == null){
                    Thread.sleep(10);
                    continue;
                }

                Transaction transaction = transactionInput.getTransaction();

                if(this.database.processedTransactionsSet.contains(transaction.getTransactionNum())){
                    this.database.incomingTransactionsQueue.remove();
                    continue;
                }

                // Process the Transaction now.

                if(transaction.getIsCrossShard()){
                    processCrossShardTransaction(transactionInput);
                }
                else {
                    processIntraShardTransaction(transaction, this.database.ballotNumber.incrementAndGet());
                }

                this.database.incomingTransactionsQueue.remove();

            } catch (InterruptedException e) {
                this.commandLogger.log("Line 143 ::: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public boolean processIntraShardTransaction(Transaction transaction, int ballotNumber) {

        if(Utils.CheckTransactionBelongToMyCluster(transaction, this.serverNumber)){
            this.logger.log("Processing Transaction: " + transaction.getTransactionNum());

            try {

                IntraShardTnxProcessingThread intraShardTnxProcessingThread = new IntraShardTnxProcessingThread(this, transaction, ballotNumber);
                intraShardTnxProcessingThread.start();
                intraShardTnxProcessingThread.join();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return true;
        }
        else {
            this.logger.log("Transaction does not belong to my cluster: " + transaction.getTransactionNum());
        }
        return false;
    }

    public CrossTxnResponse processCrossShardTransaction(TransactionInputConfig transactionInputConfig) {

        try {

            this.logger.log("Processing Cross Shard Transaction: " + transactionInputConfig.getTransaction().getTransactionNum());
            //Sync data from other servers in the cluster.

            CrossTxnResponse.Builder crossTxnResponse = CrossTxnResponse.newBuilder();
            crossTxnResponse.setClusterId(this.clusterNumber);
            crossTxnResponse.setSuccess(false);
            crossTxnResponse.setServerName(this.serverName);
            crossTxnResponse.setSuccessPreparesCount(0);
            crossTxnResponse.setFailureReason("");
            crossTxnResponse.setBallotNumber(-1);

            int sender = transactionInputConfig.getTransaction().getSender();
            int receiver = transactionInputConfig.getTransaction().getReceiver();
            int amount = transactionInputConfig.getTransaction().getAmount();

            if ( this.database.isDataItemLocked(sender) ||  this.database.isDataItemLocked(receiver)) {
                crossTxnResponse.setFailureReason("Data Item Locked");
                this.logger.log("Data Item Locked.... so rejecting the transaction");
                return crossTxnResponse.build();
            }


            boolean belongsToCluster = false;

            int itemToLock = sender;

            if (Utils.FindClusterOfDataItem(sender) == this.clusterNumber) {
                this.logger.log("Processing Cross Shard Transaction - Sender : " + transactionInputConfig.getTransaction().getTransactionNum());
                belongsToCluster = true;

                if (!this.database.isValidTransaction(transactionInputConfig.getTransaction())) {
                    crossTxnResponse.setFailureReason("Insufficient Balance");
                    return crossTxnResponse.build();
                }

            }
            else if (Utils.FindClusterOfDataItem(receiver) == this.clusterNumber) {
                this.logger.log("Processing Cross Shard Transaction - Receiver : " + transactionInputConfig.getTransaction().getTransactionNum());
                belongsToCluster = true;
                itemToLock = receiver;

            }
            else {
                this.logger.log("Transaction does not belong to my cluster: " + transactionInputConfig.getTransaction().getTransactionNum());

                return crossTxnResponse.build();
            }


            // Lock the data item

            this.database.lockDataItem(itemToLock, transactionInputConfig.getTransaction().getTransactionNum());
            this.logger.log("Locked Data Item: " + itemToLock);

            // Prepare Request to cluster servers and return Response

            int ballotNumber = this.database.ballotNumber.incrementAndGet();
            crossTxnResponse.setBallotNumber(ballotNumber);

            this.logger.log("Ballot Number: " + ballotNumber);

            IntraPrepareThread[] intraPrepareThreads = new IntraPrepareThread[GlobalConfigs.numServersPerCluster];

            ConcurrentHashMap<Integer, PrepareResponse> prepareResponses = new ConcurrentHashMap<>();
            AtomicInteger successPrepares = new AtomicInteger(1);

            PrepareRequest.Builder prepareBuilder = PrepareRequest.newBuilder();

            prepareBuilder.setBallotNumber(ballotNumber)
                    .setProcessId(this.serverName)
                    .setTransaction(transactionInputConfig.getTransaction());

            if (this.database.lastCommittedTransaction != null) {
                prepareBuilder.setLatestCommittedTransaction(this.database.lastCommittedTransaction);
            }

            prepareBuilder.setLatestCommittedBallotNumber(this.database.lastCommittedBallotNumber)
                    .setClusterId(this.clusterNumber);


            PrepareRequest prepareRequest = prepareBuilder.build();
            this.logger.log("Prepare Request: " + prepareRequest.toString());
            try {

                for (int i = 0; i < GlobalConfigs.numServersPerCluster; i++) {
                    if (Objects.equals(GlobalConfigs.clusterToServersMap.get(this.clusterNumber).get(i), this.serverNumber)) {
                        continue;
                    }
                    intraPrepareThreads[i] = new IntraPrepareThread(this, prepareRequest, prepareResponses, GlobalConfigs.clusterToServersMap.get(this.clusterNumber).get(i), successPrepares);
                    intraPrepareThreads[i].start();
                }

                this.database.addTransactionStatus(ballotNumber, TransactionStatus.PREPARED);

                for (int i = 0; i < GlobalConfigs.numServersPerCluster; i++) {
                    if (intraPrepareThreads[i] == null) continue;
                    intraPrepareThreads[i].join();
                }

            } catch (Exception e) {
                this.logger.log("Error " + e.getMessage());
            }


            if (successPrepares.get() < GlobalConfigs.ShardConsesusThreshold) {
                this.logger.log("Consensus FAILED for Cross Shard Prepare Phase");

                // Retry Prepare phase if Synced
                PrepareResponse syncPrepareResponse = null;

                int maxCommittedBallotNumber = -1;

                for (PrepareResponse r : prepareResponses.values()) {
                    if (r.getNeedToSync()) {
                        if (r.getLastCommittedBallotNumber() > maxCommittedBallotNumber) {
                            maxCommittedBallotNumber = r.getLastCommittedBallotNumber();
                            syncPrepareResponse = r;
                        }
                    }
                }

                if (syncPrepareResponse != null) {
                    // Sync the data

                    this.logger.log("Sync Prepare Response: " + syncPrepareResponse);


                    this.syncData(syncPrepareResponse);
                    ballotNumber = this.database.ballotNumber.incrementAndGet();
                    Thread.sleep(10);

                    this.logger.log("Synced data ... Now Retrying Prepare phase");

                    this.logger.log(syncPrepareResponse.toString());


                    intraPrepareThreads = new IntraPrepareThread[GlobalConfigs.numServersPerCluster];

                    prepareResponses = new ConcurrentHashMap<>();
                    successPrepares = new AtomicInteger(1);

                    prepareBuilder = PrepareRequest.newBuilder();

                    prepareBuilder.setBallotNumber(ballotNumber)
                            .setProcessId(this.serverName)
                            .setTransaction(transactionInputConfig.getTransaction());

                    if (this.database.lastCommittedTransaction != null) {
                        prepareBuilder.setLatestCommittedTransaction(this.database.lastCommittedTransaction);
                    }

                    prepareBuilder.setLatestCommittedBallotNumber(this.database.lastCommittedBallotNumber)
                            .setClusterId(this.clusterNumber);


                    prepareRequest = prepareBuilder.build();

                    for (int i = 0; i < GlobalConfigs.numServersPerCluster; i++) {
                        if (Objects.equals(GlobalConfigs.clusterToServersMap.get(this.clusterNumber).get(i), this.serverNumber)) {
                            continue;
                        }
                        intraPrepareThreads[i] = new IntraPrepareThread(this, prepareRequest, prepareResponses, GlobalConfigs.clusterToServersMap.get(this.clusterNumber).get(i), successPrepares);
                        intraPrepareThreads[i].start();
                    }

                    this.database.addTransactionStatus(ballotNumber, TransactionStatus.PREPARED);

                    for (int i = 0; i < GlobalConfigs.numServersPerCluster; i++) {
                        if (intraPrepareThreads[i] == null) continue;
                        intraPrepareThreads[i].join();
                    }
                }


                crossTxnResponse.setSuccessPreparesCount(successPrepares.get());
                if(successPrepares.get() >= GlobalConfigs.ShardConsesusThreshold ){
                    crossTxnResponse.setSuccess(true);
                }
                else {
                    crossTxnResponse.setSuccess(false);
                    crossTxnResponse.setFailureReason("Consensus not reached");
                }
                return crossTxnResponse.build();
            } else {
                this.logger.log("Consensus Reached for Cross Shard Prepare Phase");
                crossTxnResponse.setSuccess(true);
                crossTxnResponse.setSuccessPreparesCount(successPrepares.get());

                this.database.addToDataStore(prepareRequest);

                // Write Log Ahead
                this.database.writeToWAL(prepareRequest.getTransaction());

            }
            return crossTxnResponse.build();
        }
        catch (Exception e){
            this.logger.log("Error in Cross Shard Transaction: " + e.getMessage());
            return CrossTxnResponse.newBuilder().setSuccess(false).build();
        }
        finally {
           // this.pauseTnxServiceDueToCrossShard = false;
        }
    }

    public void addSyncDataToPrepareResponse( PrepareResponse.Builder prepareResponse, PrepareRequest request){
        // add all Transactions & statuses after request's committed tnx
        for (int i = request.getLatestCommittedBallotNumber()+1; i <= this.database.ballotNumber.get() ; i++) {

            Transaction transaction = this.database.getTransaction(i);
            if(transaction.getTransactionNum() != -1){
                prepareResponse.putSyncTransactionsMap(i, transaction);
            }
            TransactionStatus status = this.database.getTransactionStatus(i);
            if(status != TransactionStatus.PENDING){
                prepareResponse.putSyncTransactionStatusMap(i, status);
            }
        }

        // add total balances map after request's committed tnx
        //prepareResponse.putAllSyncBalancesMap(this.database.getAllBalances());
        prepareResponse.setLatestBallotNumber(this.database.ballotNumber.get());
        prepareResponse.setLastCommittedBallotNumber(this.database.lastCommittedBallotNumber);
        if(this.database.lastCommittedTransaction != null)
            prepareResponse.setLastCommittedTransaction(this.database.lastCommittedTransaction);

        prepareResponse.setNeedToSync(true);
    }

    public void syncData(PrepareResponse syncPrepareResponse) {
        this.logger.log("Data Post Sync -------------");


        this.database.ballotNumber.set( Math.max(syncPrepareResponse.getLatestBallotNumber() , this.database.ballotNumber.get()) );

        // Sync Transactions
        for (int i = syncPrepareResponse.getLastAcceptedUncommittedBallotNumber()+1; i <= syncPrepareResponse.getLatestBallotNumber() ; i++) {
            if(syncPrepareResponse.getSyncTransactionsMapMap().containsKey(i)){
                this.database.addTransaction(i, syncPrepareResponse.getSyncTransactionsMapMap().get(i));
                this.logger.log("Synced Transaction: " + i);
                this.database.executeTransaction(syncPrepareResponse.getSyncTransactionsMapMap().get(i));
            }
            if(syncPrepareResponse.getSyncTransactionStatusMapMap().containsKey(i)){
                this.database.addTransactionStatus(i, syncPrepareResponse.getSyncTransactionStatusMapMap().get(i));
                this.logger.log("Synced Transaction Status: " + i);
            }
        }

        this.database.lastCommittedBallotNumber = syncPrepareResponse.getLastCommittedBallotNumber();
        this.database.lastCommittedTransaction = syncPrepareResponse.getLastCommittedTransaction();

        this.database.lastAcceptedUncommittedTransaction = syncPrepareResponse.getLastAcceptedUncommittedTransaction();
        this.database.lastAcceptedUncommittedBallotNumber = syncPrepareResponse.getLastAcceptedUncommittedBallotNumber();

        // RunUpdateBalancesWithTheseNewTransactions

        // Sync Balances
        //this.database.updateBalances(syncPrepareResponse.getSyncBalancesMapMap());
    }

    public PrepareResponse handlePreparePhase(PrepareRequest request) {
        PrepareResponse.Builder prepareResponse = PrepareResponse.newBuilder();
        prepareResponse.setProcessId(this.serverName);
        prepareResponse.setBallotNumber(request.getBallotNumber());
        prepareResponse.setSuccess(false);
        addSyncDataToPrepareResponse(prepareResponse, request);
        prepareResponse.setLastAcceptedUncommittedBallotNumber(this.database.lastAcceptedUncommittedBallotNumber);
        if(this.database.lastAcceptedUncommittedTransaction != null)
            prepareResponse.setLastAcceptedUncommittedTransaction(this.database.lastAcceptedUncommittedTransaction);

        this.logger.log("Prepare Request Received from " + request.getProcessId() );

        this.logger.log(request.toString());

        this.logger.log("-------------------");

        this.logger.log("DB -> Last Committed Ballot Number: " + this.database.lastCommittedBallotNumber);
        this.logger.log("Req -> getLatestCommittedBallotNumber" + request.getLatestCommittedBallotNumber());
        this.logger.log("Ahead check");

        this.logger.log("Last Accepted Uncommitted Ballot Number: " + this.database.lastAcceptedUncommittedBallotNumber);
        this.logger.log("Req Ballot Number " + request.getBallotNumber());
        this.logger.log("Success condition");

        this.logger.log("Last Accepted Uncommitted Transaction: " + Utils.toString(this.database.lastAcceptedUncommittedTransaction));
        this.logger.log("Last Committed Transaction: " + Utils.toString(this.database.lastCommittedTransaction));

        if(request.getLatestCommittedBallotNumber() < this.database.lastCommittedBallotNumber){

            this.logger.log("Prepare Request Rejected from " + request.getProcessId() + " Because Current Server is ahead of Leader " );
            return prepareResponse.build();
        }

        if(request.getBallotNumber() >= this.database.lastAcceptedUncommittedBallotNumber){
            this.database.lastAcceptedUncommittedBallotNumber = request.getBallotNumber();
            this.database.lastAcceptedUncommittedTransaction = request.getTransaction();
            this.database.ballotNumber.set( Math.max(request.getBallotNumber() , this.database.ballotNumber.get()) );

            this.database.addTransaction(request.getBallotNumber(), request.getTransaction());
            this.database.addTransactionStatus(request.getBallotNumber(), TransactionStatus.PREPARED);

            prepareResponse.setSuccess(true);
            prepareResponse.setNeedToSync(false);


            if(request.getTransaction().getIsCrossShard())
                this.database.addToDataStore(request);
        }

        this.logger.log("Prepare Request Accepted from " + request.getProcessId() );



        return prepareResponse.build();
    }

    public CommitResponse handleCommit(CommitRequest request) {
        CommitResponse.Builder commitResponse = CommitResponse.newBuilder();
        commitResponse.setProcessId(this.serverName);
        commitResponse.setBallotNumber(request.getBallotNumber());
        commitResponse.setSuccess(false);


        if(request.getTransaction().getIsCrossShard()){
            //Cross Shard Type

            if(request.getAbort()){

                this.logger.log("Abort Request Received from " + request.getProcessId() );
                this.database.addTransactionStatus(request.getBallotNumber(), TransactionStatus.ABORTED);

                this.database.rollbackWAL(request.getTransaction().getTransactionNum());

                this.database.unlockDataItem(request.getTransaction().getSender(), request.getTransaction().getTransactionNum());
                this.database.unlockDataItem(request.getTransaction().getReceiver(), request.getTransaction().getTransactionNum());

                commitResponse.setSuccess(true);
            }
            else {

                this.logger.log("Commit Request Received from " + request.getProcessId());

                    this.database.lastCommittedBallotNumber = request.getBallotNumber();
                    this.database.lastCommittedTransaction = request.getTransaction();
//                    this.database.lastAcceptedUncommittedBallotNumber = -1;
//                    this.database.lastAcceptedUncommittedTransaction = null;
                    commitResponse.setSuccess(true);
                    this.database.addTransactionStatus(request.getBallotNumber(), TransactionStatus.COMMITTED);
                    this.logger.log("Commit Request Accepted from " + request.getProcessId());

//                    this.database.executeTransaction(request.getTransaction());
                    this.database.commitbackWAL(request.getTransaction().getTransactionNum());
            }

        }
        else{

            if(request.getAbort()){

                this.logger.log("Abort Request Received from " + request.getProcessId() );
                this.database.addTransactionStatus(request.getBallotNumber(), TransactionStatus.ABORTED);

                this.database.unlockDataItem(request.getTransaction().getSender(), request.getTransaction().getTransactionNum());
                this.database.unlockDataItem(request.getTransaction().getReceiver(), request.getTransaction().getTransactionNum());

                commitResponse.setSuccess(true);
            }
            else {

                this.logger.log("Commit Request Received from " + request.getProcessId());

                if (request.getBallotNumber() == this.database.lastAcceptedUncommittedBallotNumber) {
                    this.database.lastCommittedBallotNumber = request.getBallotNumber();
                    this.database.lastCommittedTransaction = request.getTransaction();
                    this.database.lastAcceptedUncommittedBallotNumber = -1;
                    this.database.lastAcceptedUncommittedTransaction = null;
                    commitResponse.setSuccess(true);
                    this.database.addTransactionStatus(request.getBallotNumber(), TransactionStatus.COMMITTED);
                    this.logger.log("Commit Request Accepted from " + request.getProcessId());

                    this.database.executeTransaction(request.getTransaction());
                }

            }


        }


        this.database.addToDataStore(request);

        //Finally remove locks

        this.database.unlockDataItem(request.getTransaction().getSender(), request.getTransaction().getTransactionNum());
        this.database.unlockDataItem(request.getTransaction().getReceiver(), request.getTransaction().getTransactionNum());

        return commitResponse.build();
    }

    public void sendExecutionReplyToClient(Transaction transaction, boolean success, String failureReason) {
        // Send reply to Client
        ExecutionReply.Builder executionReply = ExecutionReply.newBuilder();
        executionReply.setTransactionNum(transaction.getTransactionNum());
        executionReply.setSuccess(success);
        executionReply.setServerName(this.serverName);
        executionReply.setClusterId(this.clusterNumber);
        executionReply.setFailureReason(failureReason);
        this.serversToPaxosStub.get(0).execReply(executionReply.build());
    }

    public String PrintDataStore(){
        return this.serverName + " : " + String.join(" -> ", this.database.dataStore.get());
    }

}
