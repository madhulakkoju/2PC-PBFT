package org.cse535.service;

import io.grpc.stub.StreamObserver;
import org.cse535.Main;
import org.cse535.node.ViewServer;
import org.cse535.proto.LinearPBFTGrpc;
import org.cse535.proto.TransactionInputConfig;
import org.cse535.proto.TxnResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class LinearPBFTService extends LinearPBFTGrpc.LinearPBFTImplBase {


    @Override
    public void request(TransactionInputConfig request, StreamObserver<TxnResponse> responseObserver) {

        Main.node.logger.log("Request received: " + request.getTransaction().getTransactionNum());

        if(!Main.node.isServerActive.get()){
            Main.node.logger.log("Server is not active -> left request");

            responseObserver.onNext(null);
            responseObserver.onCompleted();
            return;
        }
//
//        if( Main.node.isLeader() ){
//
//            Main.node.logger.log("Server is leader -> processing request -- added to queue");
//            Main.node.database.incomingTnxQueue.add(request);
//
//            TxnResponse response = TxnResponse.newBuilder().setSuccess(true).setServerName(Main.node.serverName).build();
//            responseObserver.onNext(response);
//        }
//        else{
//            Main.node.logger.log("Server is not leader -> left request");
//            TxnResponse response = TxnResponse.newBuilder().setSuccess(false).setServerName(Main.node.serverName).build();
//            responseObserver.onNext(response);
//        }

        responseObserver.onCompleted();
    }




}
