package org.cse535.service;

import io.grpc.stub.StreamObserver;
import org.cse535.Main;
import org.cse535.proto.*;

public class CommandsService extends CommandsGrpc.CommandsImplBase {

    @Override
    public void performance(CommandInput request, StreamObserver<CommandOutput> responseObserver) {


        responseObserver.onNext(CommandOutput.newBuilder().setOutput("Performance").build());
        responseObserver.onCompleted();
    }

    @Override
    public void printLog(CommandInput request, StreamObserver<CommandOutput> responseObserver) {


            responseObserver.onNext(CommandOutput.newBuilder().setOutput("Log").build());
            responseObserver.onCompleted();
    }

    @Override
    public void printDB(CommandInput request, StreamObserver<CommandOutput> responseObserver) {


            responseObserver.onNext(CommandOutput.newBuilder().setOutput("DB").build());
            responseObserver.onCompleted();
    }

    @Override
    public void printBalance(CommandInput request, StreamObserver<CommandOutput> responseObserver) {

        responseObserver.onNext(CommandOutput.newBuilder().setOutput(
                Main.node.serverName + " : " + String.format( "%2d",
                Main.node.database.getBalance( Integer.parseInt( request.getInput() )))).build());
        responseObserver.onCompleted();
    }

    @Override
    public void printDatastore(CommandInput request, StreamObserver<CommandOutput> responseObserver) {

        responseObserver.onNext(CommandOutput.newBuilder().setOutput(Main.node.PrintDataStore()).build());
        responseObserver.onCompleted();
    }
}
