package org.cse535.service;

import io.grpc.stub.StreamObserver;
import org.cse535.Main;
import org.cse535.proto.*;

public class ActivateServersService extends ActivateServersGrpc.ActivateServersImplBase {

    @Override
    public void activateServer(ActivateServerRequest request, StreamObserver<ActivateServerResponse> responseObserver) {
        Main.node.isServerActive.set(true);
        responseObserver.onNext(ActivateServerResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
        System.out.println("Server activated");
        Main.node.commandLogger.log("---------------------------------------------------------------------------------");
        Main.node.commandLogger.log("                               Test Case: " + request.getTestCase());
        Main.node.commandLogger.log("---------------------------------------------------------------------------------");
    }


    @Override
    public void deactivateServer(DeactivateServerRequest request, StreamObserver<DeactivateServerResponse> responseObserver) {
        Main.node.isServerActive.set(false);
        responseObserver.onNext(DeactivateServerResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
        System.out.println("Server deactivated");
        Main.node.commandLogger.log("---------------------------------------------------------------------------------");

        Main.node.commandLogger.log("---------------------------------------------------------------------------------");
    }
}
