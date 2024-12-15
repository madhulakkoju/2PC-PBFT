package org.cse535.threadimpls;

import org.cse535.configs.GlobalConfigs;
import org.cse535.configs.Utils;
import org.cse535.node.Node;
import org.cse535.proto.*;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CrossShardTnxProcessingThread extends Thread {

    public Node node;
    public TransactionInputConfig transactionInputConfig;
    public String senderServerId;
    public String receiverServerId;

    public CrossShardTnxProcessingThread(Node viewServer, TransactionInputConfig transactionInputConfig,
                                         String senderServerId, String receiverServerId) {
        this.node = viewServer;
        this.transactionInputConfig = transactionInputConfig;
        this.senderServerId = senderServerId;
        this.receiverServerId = receiverServerId;
    }

    public void run(){

        try{

        Transaction transaction = this.transactionInputConfig.getTransaction();

        boolean isCoordinator = Utils.FindClusterOfDataItem(transaction.getSender()) == this.node.clusterNumber;

            if(isCoordinator) {

                this.node.database.crossShardPrepareResponses.put(transaction.getTransactionNum(), new HashMap<>());

                // Coordinate between shards

                AtomicBoolean isCrossShardPrepareSuccess = new AtomicBoolean(false);


                IntraShardTnxProcessingThread intraShardTnxProcessingThread = new IntraShardTnxProcessingThread(this.node,
                        this.transactionInputConfig, isCrossShardPrepareSuccess);

                intraShardTnxProcessingThread.start();

                intraShardTnxProcessingThread.join();

                int currentClusterSeqNum = this.node.database.transactionNumSeqNumMap.get(transaction.getTransactionNum());

                //Coordinator Prepare Phase completed
                if(isCrossShardPrepareSuccess.get()){
                    // Send to other shard for Prepare

                    int receiverCluster = Utils.FindClusterOfDataItem(transaction.getReceiver());


                    //Wait for response from other shard
                    Thread.sleep(GlobalConfigs.TransactionTimeout);

                    boolean recieverClusterPrepSuccess = false;

                    if(this.node.database.crossShardPrepareResponses.containsKey(transaction.getTransactionNum())
                        && this.node.database.crossShardPrepareResponses.get(transaction.getTransactionNum()).containsKey(receiverCluster) ){

                        if(!this.node.database.crossShardPrepareResponses.get(transaction.getTransactionNum()).get(receiverCluster).getAbort()){
                            recieverClusterPrepSuccess = true;
                        }
                    }

                    if(!recieverClusterPrepSuccess){
                        this.node.logger.log(Utils.toDataStoreString(transaction) + " "+ "Cross Shard Prepare Success");

                        //Check if response received
                        // if received, Commit in this shard

                        CommitRequest commitRequest = CommitRequest.newBuilder()
                                .setTransaction(transaction)
                                .setAbort(false)
                                .setProcessId(this.node.serverName)
                                .setClusterId(this.node.clusterNumber)
                                .setSequenceNumber(currentClusterSeqNum)
                                .build();


                        sendCommit(commitRequest, this.node.clusterNumber);

                        Thread.sleep(GlobalConfigs.TransactionTimeout);

                        // Commit in Other shard
                        int receiverClusterSeqNum = this.node.database.crossShardPrepareResponses.get(transaction.getTransactionNum()).get(receiverCluster).getSequenceNumber();

                        CommitRequest commitRequestReceiver = CommitRequest.newBuilder()
                                .setTransaction(transaction)
                                .setAbort(false)
                                .setProcessId(this.node.serverName)
                                .setClusterId(receiverCluster)
                                .setSequenceNumber(receiverClusterSeqNum)
                                .build();

                        sendCommit(commitRequestReceiver, this.node.clusterNumber);


                    }
                    else{
                        this.node.logger.log(Utils.toDataStoreString(transaction) + " "+ "Cross Shard Prepare Failed");
                        //ABORT now

                        CommitRequest commitRequest = CommitRequest.newBuilder()
                                .setTransaction(transaction)
                                .setAbort(true)
                                .setProcessId(this.node.serverName)
                                .setClusterId(this.node.clusterNumber)
                                .setSequenceNumber(currentClusterSeqNum)
                                .build();

                        // If not received, ABORT and WAL Rollback
                        sendCommit(commitRequest, this.node.clusterNumber);

                        //rollback here


                        return;
                    }







                }




            }

        }
        catch (Exception e){
            System.err.println("Error occurred while processing cross shard transaction: " + e.getMessage());
            this.node.logger.log( Utils.toDataStoreString(this.transactionInputConfig.getTransaction()) + " "+  "Error occurred while processing cross shard transaction: " + e.getMessage());
        }

    }




    public void sendCommit(CommitRequest request, int cluster){

        for(int serverId : GlobalConfigs.clusterToServersMap.get(cluster)){
            if(serverId == this.node.serverNumber){
                continue;
            }

            Thread thread = new Thread(() -> {
                try {
                    this.node.serversToPaxosStub.get(cluster).crossShardCommit(request);
                } catch (Exception e) {
                    System.err.println("Error occurred while processing receiver response: " + e.getMessage());
                    this.node.logger.log( Utils.toDataStoreString(this.transactionInputConfig.getTransaction()) + " "+  "Error occurred while processing receiver response: " + e.getMessage());
                }
            });
            thread.start();
        }
    }


}
