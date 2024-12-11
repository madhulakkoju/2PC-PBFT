package org.cse535.threadimpls;

import org.cse535.configs.GlobalConfigs;
import org.cse535.configs.Utils;
import org.cse535.node.ViewServer;
import org.cse535.proto.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CrossShardTnxProcessingThread extends Thread {

    public ViewServer viewServer;
    public TransactionInputConfig transactionInputConfig;
    public String senderServerId;
    public String receiverServerId;

    public CrossShardTnxProcessingThread(ViewServer viewServer, TransactionInputConfig transactionInputConfig,
                                         String senderServerId, String receiverServerId) {
        this.viewServer = viewServer;
        this.transactionInputConfig = transactionInputConfig;
        this.senderServerId = senderServerId;
        this.receiverServerId = receiverServerId;
    }

    public void run(){


    }

}
