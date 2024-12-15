package org.cse535.threadimpls;

import org.cse535.configs.GlobalConfigs;
import org.cse535.node.Node;
import org.cse535.proto.CommitRequest;
import org.cse535.proto.CommitResponse;
import org.cse535.proto.PrepareRequest;
import org.cse535.proto.PrepareResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class IntraCommitThread extends Thread{

    public AtomicInteger successCommits;
    public CommitRequest request;

    public int targetServer;
    public Node node;

    public IntraCommitThread(Node node, CommitRequest request, int targetServer, AtomicInteger successCommits) {
        this.node = node;
        this.request = request;
        this.targetServer = targetServer;
        this.successCommits = successCommits;
    }

    public void run() {

        if(GlobalConfigs.ServerToPortMap.get(this.targetServer) == this.node.port){
            return;
        }

        if(request.getTransaction() == null){
            this.node.logger.log("Transaction is null in commit request");
            return;
        }

        CommitResponse response = this.node.serversToPaxosStub.get(targetServer).commit(this.request);

        if(response.getSuccess()){
            this.successCommits.incrementAndGet();
        }

        this.node.logger.log("Commit response from " + targetServer + " : " + response.getSuccess());

    }


}
