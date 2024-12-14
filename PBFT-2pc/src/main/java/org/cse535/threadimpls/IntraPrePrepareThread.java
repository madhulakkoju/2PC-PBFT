package org.cse535.threadimpls;

import org.cse535.configs.GlobalConfigs;
import org.cse535.node.Node;
import org.cse535.proto.PrePrepareRequest;
import org.cse535.proto.PrePrepareResponse;
import org.cse535.proto.PrepareRequest;
import org.cse535.proto.PrepareResponse;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class IntraPrePrepareThread extends Thread{

    public PrePrepareRequest request;
    public int targetServer;
    public AtomicInteger successPrepares;
    public Node node;


    public IntraPrePrepareThread(Node node, PrePrepareRequest request,
                                 int targetServer, AtomicInteger successPrepares) {
        this.request = request;
        this.node = node;
        this.targetServer = targetServer;
        this.successPrepares = successPrepares;
    }


    public void run() {

        if(GlobalConfigs.ServerToPortMap.get(this.targetServer) == this.node.port){
            return;
        }

        PrePrepareResponse response = this.node.serversToPaxosStub.get(targetServer).prePrepare(this.request);

        if(response.getSuccess()){
            this.successPrepares.incrementAndGet();
        }

        if(this.node.database.prePrepareResponseMap.get(this.request.getSequenceNumber()) == null){
            this.node.database.prePrepareResponseMap.put(this.request.getSequenceNumber(), new ArrayList<>());
        }

        this.node.database.prePrepareResponseMap.get(this.request.getSequenceNumber()).add(response);

        this.node.logger.log("Prepare response from " + targetServer + " : " + response.getSuccess());
    }

}
