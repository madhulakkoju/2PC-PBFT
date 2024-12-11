package org.cse535.node;

import org.cse535.configs.GlobalConfigs;
import org.cse535.configs.Utils;
import org.cse535.proto.*;
import org.cse535.threadimpls.IntraPrepareThread;
import org.cse535.threadimpls.IntraShardTnxProcessingThread;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
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


            } catch (InterruptedException e) {
                this.commandLogger.log("Line 143 ::: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }



}
