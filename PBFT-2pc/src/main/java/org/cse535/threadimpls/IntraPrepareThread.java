package org.cse535.threadimpls;

import org.cse535.configs.GlobalConfigs;
import org.cse535.node.Node;
import org.cse535.proto.PrepareRequest;
import org.cse535.proto.PrepareResponse;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class IntraPrepareThread extends Thread{

    public PrepareRequest request;

    public int targetServer;
    public AtomicInteger successPrepares;
    public Node node;


    public IntraPrepareThread(Node node, PrepareRequest request, int targetServer, AtomicInteger successPrepares) {
        this.request = request;
        this.node = node;
        this.targetServer = targetServer;
        this.successPrepares = successPrepares;
    }


    public void run() {

        if(GlobalConfigs.ServerToPortMap.get(this.targetServer) == this.node.port){
            return;
        }

        PrepareResponse response = this.node.serversToPaxosStub.get(targetServer).prepare(this.request);

        if(response.getSuccess()){
            this.successPrepares.incrementAndGet();
        }

        if(!this.node.database.prepareResponseMap.containsKey(this.request.getSequenceNumber())){
            this.node.database.prepareResponseMap.put(this.request.getSequenceNumber(), new ArrayList<>());
        }

        this.node.database.prepareResponseMap.get(this.request.getSequenceNumber()).add(response);

        this.node.logger.log("Prepare response from " + targetServer + " : " + response.getSuccess());


    }

}
