package org.cse535.threadimpls;

import org.cse535.configs.GlobalConfigs;
import org.cse535.node.Node;
import org.cse535.proto.*;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class IntraShardTnxProcessingThread extends Thread {

    public Transaction tnx;
    public Node node;
    public int ballotNumber;

    public IntraShardTnxProcessingThread(Node node, Transaction tnx, int ballotNumber) {
        this.tnx = tnx;
        this.node = node;
        this.ballotNumber = ballotNumber;
        //this.node.database.addTransaction(ballotNumber, tnx);
    }



    public void run() {

    }

}
