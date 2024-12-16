package org.cse535.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.cse535.Main;
import org.cse535.configs.GlobalConfigs;
import org.cse535.configs.Utils;
import org.cse535.node.ViewServer;
import org.cse535.proto.*;

import java.util.HashMap;

public class LinearPBFTService extends LinearPBFTGrpc.LinearPBFTImplBase {

    @Override
    public void request(TransactionInputConfig request, StreamObserver<TxnResponse> responseObserver) {

        if(!Main.node.isServerActive.get()){
            //Inactive server
            TxnResponse response = TxnResponse.newBuilder().setSuccess(false).setServerName(Main.node.serverName).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        Main.node.database.incomingTransactionsQueue.add(request);
        TxnResponse response = TxnResponse.newBuilder().setSuccess(true).setServerName(Main.node.serverName).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void crossShardRequest(TransactionInputConfig request, StreamObserver<CrossTxnResponse> responseObserver) {

        if(!Main.node.isServerActive.get()){
            //Inactive server
            CrossTxnResponse response = CrossTxnResponse.newBuilder()
                    .setSuccess(false)
                    .setServerName(Main.node.serverName)
                    .setBallotNumber(-1)
                    .setClusterId(Main.node.clusterNumber)
                    .setSuccessPreparesCount(0)
                    .setFailureReason("Server Inactive")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        Main.node.logger.log("Received cross shard request " + Utils.toString(request.getTransaction()) );

        CrossTxnResponse response = Main.node.processCrossShardTransaction(request);

        Main.node.logger.log("Sending cross shard response \n**************\n" + response.toString() + "\n**************" );

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void prePrepare(PrePrepareRequest request, StreamObserver<PrePrepareResponse> responseObserver) {
        Main.node.logger.log("Received pre-prepare request from " + request.getProcessId() + " with ballot number " + request.getSequenceNumber());
        if( ! Main.node.isServerActive.get() ){
            //Inactive server
            PrePrepareResponse response = PrePrepareResponse.newBuilder()
                    .setSuccess(false)
                    .setSequenceNumber(request.getSequenceNumber())
                    .setView(request.getView())
                    .setProcessId(Main.node.serverName)
                    .setClusterId(Main.node.clusterNumber)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        PrePrepareResponse resp = Main.node.handlePrePreparePhase(request);
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    @Override
    public void prepare(PrepareRequest request, StreamObserver<PrepareResponse> responseObserver) {
        Main.node.logger.log("Received prepare request from " + request.getProcessId() + " with Sequence number " + request.getSequenceNumber());
        if( ! Main.node.isServerActive.get() ){
            //Inactive server
            PrepareResponse response = PrepareResponse.newBuilder()
                    .setSuccess(false)
                    .setSequenceNumber(request.getSequenceNumber())
                    .setView(request.getView())
                    .setClusterId(Main.node.clusterNumber)
                    .setProcessId(Main.node.serverName)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        PrepareResponse resp = Main.node.handlePreparePhase(request);
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }


    @Override
    public void commit(CommitRequest request, StreamObserver<CommitResponse> responseObserver) {
        Main.node.logger.log("Received Commit request from " + request.getProcessId() + " with Seq number " + request.getSequenceNumber());

        if( ! Main.node.isServerActive.get() ){
            //Inactive server
            CommitResponse response = CommitResponse.newBuilder()
                    .setSuccess(false)
                    .setSequenceNumber(request.getSequenceNumber())
                    .setView(request.getView())
                    .setProcessId(Main.node.serverName)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        CommitResponse resp = Main.node.handleCommit(request);
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }


    @Override
    public void execReply(ExecutionReply request, StreamObserver<Empty> responseObserver) {
        if(ViewServer.viewServer != null){
            ViewServer.viewServer.transactionStatuses.get(request.getTransactionId())
                    .put(
                            Integer.valueOf(request.getProcessId().replace("S","")),
                    request.getSuccess() ? request.getStatus() : "ABORTED-"+request.getFailureReason());
            ViewServer.viewServer.endTime = System.currentTimeMillis();
        }

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }


    @Override
    public void crossShardPrepare(CommitRequest request, StreamObserver<Empty> responseObserver) {
        if( ! Main.node.isServerActive.get() ){
            //Inactive server
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
            return;
        }

        if(request.getSequenceNumber() != -1){
            if(!Main.node.database.crossShardPrepareResponses.containsKey(request.getTransaction().getTransactionNum())) {
                Main.node.database.crossShardPrepareResponses.put(request.getTransaction().getTransactionNum(), new HashMap<>());
            }

            Main.node.database.crossShardPrepareResponses.get(request.getTransaction().getTransactionNum()).put(request.getClusterId(), request);
        }

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void crossShardCommit(CommitRequest request, StreamObserver<CommitResponse> responseObserver) {

        Main.node.logger.log("Received Cross Shard Commit request from " + request.getProcessId() + " with Seq number " + request.getSequenceNumber());

        if( ! Main.node.isServerActive.get() || request.getSequenceNumber() == -1){
            //Inactive server
            CommitResponse response = CommitResponse.newBuilder()
                    .setSuccess(false)
                    .setSequenceNumber(request.getSequenceNumber())
                    .setView(request.getView())
                    .setProcessId(Main.node.serverName)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        CommitResponse resp = Main.node.handleCrossShardCommit(request);

        Main.node.logger.log("Sending Cross Shard Commit response to " + request.getProcessId() + " with Seq number " + request.getSequenceNumber());
        Main.node.logger.log("Response: " + resp.toString());

        responseObserver.onNext(resp);
        responseObserver.onCompleted();

        Main.node.logger.log("Resending Reply to Client  " );
        Main.node.reSendExecutionReplyToClient(request.getTransaction());

    }

    @Override
    public void reShardingInitiation(Empty request, StreamObserver<ReShardingInitData> responseObserver) {
        if(ViewServer.viewServer != null){
            return;
        }
        Main.node.isServerActive.set(false);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        responseObserver.onNext( Main.node.database.getReshardingInitData() );
        responseObserver.onCompleted();
    }

    @Override
    public void reShardingProcess(ReShardingData request, StreamObserver<CommandOutput> responseObserver) {
        if(ViewServer.viewServer != null){
            return;
        }


        Main.node.database.processReshardingData(request);

        responseObserver.onNext( CommandOutput.newBuilder().setOutput("true").build() );
        responseObserver.onCompleted();

        System.out.println("ReSharding Process Completed");

        Main.node.commandLogger.log("ReSharding Process Completed\nNew Config:");

        int clusterNum = Main.node.clusterNumber;

        GlobalConfigs.DataItemToClusterMap.forEach((k, v) -> {
            if(v == clusterNum)
                Main.node.commandLogger.log("DataItem: " + k + " Cluster: " + v);
        });

        Main.node.isServerActive.set(true);
        return;
    }


    @Override
    public void relayRequest(Transaction request, StreamObserver<Empty> responseObserver) {

        if( ! Main.node.isServerActive.get() ){
            //Inactive server
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
            return;
        }

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();

        Main.node.reSendExecutionReplyToClient(request);

    }
}
