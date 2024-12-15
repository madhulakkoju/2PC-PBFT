package org.cse535.node;

import com.google.protobuf.Empty;
import org.cse535.Main;
import org.cse535.configs.GlobalConfigs;
import org.cse535.configs.Utils;

import org.cse535.loggers.LogUtils;
import org.cse535.proto.*;
import org.cse535.reshard.Resharding;
import org.cse535.threadimpls.CrossShardTnxProcessingThread;
import org.cse535.threadimpls.ReShardThread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class ViewServer extends NodeServer {


    public int TestSetNumber;

    public static class TnxLine{
        public TransactionInputConfig transactionInputConfig;
        public HashMap<Integer, String> clusterContactServermapping;

        public List<String> maliciousServers;

        public TnxLine(TransactionInputConfig transactionInputConfig, List<String> contactServers, List<String> maliciousServers) {
            this.transactionInputConfig = transactionInputConfig;
            clusterContactServermapping = new HashMap<>();

            for(String server : contactServers){
                clusterContactServermapping.put( Utils.FindMyCluster( server ), server);
            }
            this.maliciousServers = maliciousServers;
        }

    }




    public enum Command {
        PrintDB,
        PrintLog,
        Performance,
        PrintDataStore,
        PrintBalance
    }


    public long startTime = System.currentTimeMillis();
    public long endTime = System.currentTimeMillis();
    public long transactionsCount = 0;




    HashMap<Integer, Boolean> activeServersStatusMap = new HashMap<>();


    public HashMap<Integer, HashMap<Integer, String>> transactionStatuses = new HashMap<>();

    public TreeSet<Integer> participatingDataItems = new TreeSet<>();

    public Timer timer = new Timer();


    public ViewServer(String serverName, int port) {
        super(0, port);
        try {
            this.server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TnxLine lastexecTnxLine;

    public static TnxLine parseTnxConfig(String line, int tnxCount) {

        if( line.trim().length() == 0){
            return null;
        }


// Split by commas, respecting quoted commas.
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

// Parse the test case count (assuming the first part is a simple number)
        int testCaseCount = Integer.parseInt(parts[0].trim()); // No need to use replaceAll for numbers

        viewServer.TestSetNumber = testCaseCount;

// Parse the transaction details (sender, receiver, amount)
        String[] tnx = parts[1].replaceAll("[()\"]", "").trim().split(","); // Clean up and split
        String sender = tnx[0].trim();
        String receiver = tnx[1].trim();
        int amount = Integer.parseInt(tnx[2].trim());  // Parse amount as an integer

// Parse the list of active servers
        String[] activeServers = parts[2].replaceAll("[\\[\\]\"]", "").trim().split(",");

        List<String> activeServersList = new ArrayList<>();


        for(int i =0;i<activeServers.length;i++){
            if(activeServers[i].trim().length() > 0)
                activeServersList.add( activeServers[i].trim());
        }

// Parse the list of malicious servers
        String[] maliciousServers = parts[3].replaceAll("[\\[\\]\"]", "").trim().split(",");

        List<String> contactServersList = new ArrayList<>();

        for(int i =0;i<maliciousServers.length;i++){
            if(!maliciousServers[i].trim().isEmpty())
                contactServersList.add( maliciousServers[i].trim());
        }

        List<String> maliciousServersList = new ArrayList<>();
        maliciousServers = parts[4].replaceAll("[\\[\\]\"]", "").trim().split(",");

        for(int i =0;i<maliciousServers.length;i++){
            if(!maliciousServers[i].trim().isEmpty())
                maliciousServersList.add( maliciousServers[i].trim());
        }

        // Now you can create your transaction
        Transaction transaction = Transaction.newBuilder()
                .setSender(Integer.parseInt(sender))
                .setReceiver(Integer.parseInt(receiver))
                .setAmount(amount)
                .setTransactionNum(tnxCount)
                .setIsCrossShard( Utils.IsTransactionCrossCluster(Integer.parseInt(sender), Integer.parseInt(receiver)) )
                .build();

        return new TnxLine(TransactionInputConfig.newBuilder()
                .setSetNumber(testCaseCount)
                .setTransaction(transaction)
                .addAllServerNames(activeServersList)
                .addAllPrimaryServers(contactServersList)
                .build(), contactServersList,
                maliciousServersList);
    }


    public void PrintDataStore(){
        this.logger.log("Printing Data Store");

        this.commandLogger.log("---------------------------------------- Print Data Store -----------------------------------\n");
        CommandInput commandInput = CommandInput.newBuilder().build();
        HashMap<Integer, String> dataStore = new HashMap<>();

        activeServersStatusMap.forEach((server, isActive) -> {
            if(server == 0) return;
            CommandsGrpc.CommandsBlockingStub stub = this.serversToCommandsStub.get(server);
            CommandOutput op  = CommandOutput.newBuilder().setOutput("No Output").build() ;
            op = stub.printDatastore(commandInput);
            dataStore.put(server, op.getOutput());
        });

        this.commandLogger.log("Server \\ Data Store");
        dataStore.forEach((server, data) -> {
            this.commandLogger.log( data);
        });

//        this.commandLogger.log("\n\n-----------------------------------Transaction Statuses:-----------------------------------\n");
//
//        this.transactionStatuses.forEach((tnxNum, statuses) -> {
//
//            StringBuilder status = new StringBuilder();
//            for(Map.Entry<Integer, String> entry : statuses.entrySet()){
//                status.append("S").append(entry.getKey()).append(" : ").append(entry.getValue()).append("; ");
//            }
//
//            this.commandLogger.log(" " + String.format("%3d", tnxNum) + " :: Transaction " + Utils.toDataStoreString(transactions.get(tnxNum)) + " : " + status);
//        });
    }

    public void PrintBalance(){
        this.commandLogger.log("------------------------------PrintBalance---------------------------------\n");

        this.participatingDataItems.forEach( dataItem -> {
            CommandInput commandInput = CommandInput.newBuilder().setInput( String.valueOf(dataItem) ).build();
            StringBuilder balances = new StringBuilder();

            int cluster = Utils.FindClusterOfDataItem(dataItem);

            GlobalConfigs.clusterToServersMap.get(cluster).forEach( server -> {
                if(server == 0) return;
                CommandsGrpc.CommandsBlockingStub stub = this.serversToCommandsStub.get(server);
                CommandOutput op = stub.printBalance(commandInput);
                balances.append(op.getOutput()).append("; ");
            });

            this.commandLogger.log( String.format("%4d", dataItem) + " Balances: " + balances.toString());
        });

        this.commandLogger.log("----------------------------------------------------------------------------\n");

    }

    public void PrintPerformance(){
        double latency = endTime - startTime;
        double throughput = 0.0;
        if( latency != 0.0){
            throughput = (transactionsCount / latency)*1000;
        }

        this.commandLogger.log("-------------------------Performance---------------------------------\n"
                + "Total Transactions: " + transactionsCount + "\n"
                + "Latency: " + String.format("%.8f",latency) + " ms\n"
                + "Throughput: " + String.format("%.8f", throughput) + " tps\n" +
                "----------------------------------------------------------------------------\n");
    }

    public void sendCommandToServers(Command commandType) throws InterruptedException {

        if(commandType == Command.PrintDataStore){
            PrintDataStore();
            return;
        }

        if(commandType == Command.PrintBalance){
            PrintBalance();
            return;
        }

        if(commandType == Command.Performance){
            PrintPerformance();
            return;
        }

        CommandInput commandInput = CommandInput.newBuilder().build();
        Thread.sleep(10);

        activeServersStatusMap.forEach((server, isActive) -> {

            CommandsGrpc.CommandsBlockingStub stub = this.serversToCommandsStub.get(server);
            CommandOutput op  = CommandOutput.newBuilder().setOutput("No Output").build() ;

            switch (commandType) {
                case PrintDB:
                    op = stub.printDB(commandInput);
                    break;
                case PrintLog:
                    op = stub.printLog(commandInput);
                    break;
            }

            //this.logger.log("Command: " + commandType + "\n server: " + server + "\n output: \n"+ op.getOutput());

        });

    }


    public void sendTransactionToServer(TransactionInputConfig transactionInputConfig, String server){


        if(!transactionStatuses.containsKey(transactionInputConfig.getTransaction().getTransactionNum())){
            // First Try
            transactionStatuses.put(transactionInputConfig.getTransaction().getTransactionNum(), new HashMap<>());
        }

        participatingDataItems.add(transactionInputConfig.getTransaction().getSender());
        participatingDataItems.add(transactionInputConfig.getTransaction().getReceiver());
        serversToPaxosStub.get( Integer.parseInt(server.replaceAll("S","")) ).request(transactionInputConfig);


        TimerTask retry = new TimerTask() {
            @Override
            public void run() {
                if(viewServer.transactionStatuses.get(transactionInputConfig.getTransaction().getTransactionNum()).size() >= GlobalConfigs.f+1){
                    int successCount = 0;
                    for(Map.Entry<Integer, String> entry : viewServer.transactionStatuses.get(transactionInputConfig.getTransaction().getTransactionNum()).entrySet()){
                        if(entry.getValue().toLowerCase().contains("commit") || entry.getValue().toLowerCase().contains("exec")){
                            successCount++;
                        }
                    }

                    if(successCount >= GlobalConfigs.f+1){
                        viewServer.logger.log("Transaction: " + transactionInputConfig.getTransaction().getTransactionNum() + " Completed - Received responses from f+1 servers");
                        return;
                    }
                }
                else if (viewServer.transactionStatuses.get(transactionInputConfig.getTransaction().getTransactionNum()).size() == 1 &&
                        viewServer.transactionStatuses.get(transactionInputConfig.getTransaction().getTransactionNum()).containsKey(Integer.parseInt(server.replaceAll("S",""))) &&
                        viewServer.transactionStatuses.get(transactionInputConfig.getTransaction().getTransactionNum()).get(Integer.parseInt(server.replaceAll("S","")))
                                .toLowerCase().contains("balance") ){
                    return;
                }
                
                viewServer.logger.log("Transaction: " + transactionInputConfig.getTransaction().getTransactionNum() + " Timed Out -> RETRY");
                viewServer.sendTransactionToServer(transactionInputConfig, server);
            }
        };

        timer.schedule(retry, transactionInputConfig.getTransaction().getIsCrossShard() ? 3*GlobalConfigs.TransactionTimeout: GlobalConfigs.TransactionTimeout);
    }

//    public void sendCrossShardTransaction(TransactionInputConfig transactionInputConfig, String senderServer){
//        participatingDataItems.add(transactionInputConfig.getTransaction().getSender());
//        participatingDataItems.add(transactionInputConfig.getTransaction().getReceiver());
//        try {
////            CrossShardTnxProcessingThread thread = new CrossShardTnxProcessingThread(this, transactionInputConfig, senderServer, receiverServer);
////            thread.start();
//
//            //need not wait for the thread to finish
//            //thread.join();
//
//        }
//        catch (Exception e){
//            this.logger.log("Error in Cross Shard Transaction Processing: "+ e.getMessage());
//        }
//    }




    public HashMap<Integer, Transaction> transactions = new HashMap<>();


    public static ViewServer viewServer;

    public static void main(String[] args) throws InterruptedException, IOException {

        int viewServerNum = Integer.parseInt(args[0]);
        GlobalConfigs.TotalServers = Integer.parseInt(args[1]);
        GlobalConfigs.numServersPerCluster = Integer.parseInt(args[2]);
        GlobalConfigs.TotalDataItems = Integer.parseInt(args[3]);

        GlobalConfigs.LoadConfigs();

        viewServer = new ViewServer( GlobalConfigs.ViewServerName, GlobalConfigs.ViewServerPort );

        for (Integer serverNum : GlobalConfigs.ServerToPortMap.keySet()) {
            viewServer.activeServersStatusMap.put(serverNum, true);
        }

        //String path = "src/main/resources/Lab4_Testset_1.csv"; // Intra Shard Transactions
        String path = "src/main/resources/Lab4_Testset_2.csv"; // Cross Shard Transactions

        File file = new File(path);
        String line;


        int tnxCount = 1;
        int lineNum = 0;



        if (file.exists()) {
            System.out.println("File exists");

            // Read the file
            BufferedReader br = new BufferedReader(new FileReader(path));

            int prevSetNumber = 0;

            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                lineNum++;

                //Thread.sleep(5);

                // System.out.println("Line: " + line);
                viewServer.logger.log("-------------------------------------------------------------\nLine: "+ lineNum +" : "+ line);


                TnxLine tnxLine = parseTnxConfig(line, tnxCount++);
                viewServer.lastexecTnxLine = tnxLine;
                if(tnxLine == null) {
                    continue;
                }

                TransactionInputConfig transactionInputConfig = tnxLine.transactionInputConfig;

                if (transactionInputConfig == null) {
                    //System.out.println("Invalid transaction");
                    tnxCount -- ;

                    continue;
                }

                // Trigger Inactive servers to stop accepting transactions
                if (transactionInputConfig.getServerNamesList().isEmpty()) {
                    System.out.println("No servers to send the transaction to");
                    continue;
                }

                //Activate or deactivate Servers
                if(prevSetNumber != transactionInputConfig.getSetNumber()) {
                    prevSetNumber = transactionInputConfig.getSetNumber();

                    // If the Test Set Number changes, then trigger the inactive servers to stop accepting transactions

                    // Set all servers inactive
                    for (Integer server : GlobalConfigs.ServerToPortMap.keySet()) {
                        viewServer.activeServersStatusMap.put(server, false);
                    }
                    // Set the active servers
                    for (String server : transactionInputConfig.getServerNamesList()) {
                        viewServer.activeServersStatusMap.put( Integer.parseInt( server.replaceAll("S","") ) , true);
                    }

                    Thread.sleep(20);
                    System.out.print("Press Enter to run Commands ");
                    System.console().readLine();

                    viewServer.sendCommandToServers( Command.PrintDB );
                    viewServer.sendCommandToServers( Command.PrintLog );
                    viewServer.sendCommandToServers( Command.PrintDataStore );
                    viewServer.sendCommandToServers( Command.PrintBalance );
                    viewServer.sendCommandToServers( Command.Performance );

                    System.out.print("Press Enter to continue to next Test set "+transactionInputConfig.getSetNumber() + " ");
                    System.console().readLine();

                    //viewServer.participatingDataItems.clear();
                    viewServer.transactionsCount = 0;

                    for( Integer server : GlobalConfigs.ServerToPortMap.keySet()) {
                        if(server == viewServerNum) continue;
                        if( viewServer.activeServersStatusMap.get(server)) {
                            ActivateServerRequest request = ActivateServerRequest.newBuilder().setServerName("S"+ server).setTestCase(transactionInputConfig.getSetNumber()).build();
                            viewServer.serversToActivateServersStub.get(server).activateServer(request);
                        }
                        else {
                            DeactivateServerRequest request = DeactivateServerRequest.newBuilder().setServerName("S"+ server).setTestCase(transactionInputConfig.getSetNumber()).build();
                            viewServer.serversToActivateServersStub.get(server).deactivateServer(request);
                        }

                        if(tnxLine.maliciousServers.contains("S"+server)){
                            viewServer.serversToActivateServersStub.get(server).makeByzantine( CommandInput.newBuilder().build());
                        }
                        else{
                            viewServer.serversToActivateServersStub.get(server).makeHonest( CommandInput.newBuilder().build());
                        }

                    }


                    viewServer.commandLogger.log("---------------------------------------------------------------------------------");
                    viewServer.commandLogger.log("                               Test Case: " + transactionInputConfig.getSetNumber());
                    viewServer.commandLogger.log("---------------------------------------------------------------------------------");

                    viewServer.startTime = System.currentTimeMillis();
                    viewServer.endTime = System.currentTimeMillis();
                }



                System.out.println(Utils.toString(transactionInputConfig.getTransaction()) + " ContactServers: "
                        + tnxLine.clusterContactServermapping.values() + " Active Servers: " + transactionInputConfig.getServerNamesList()
                        + " Is Cross Shard: " + transactionInputConfig.getTransaction().getIsCrossShard());


                viewServer.transactions.put(transactionInputConfig.getTransaction().getTransactionNum(), transactionInputConfig.getTransaction());

                // Check whether transaction is IntraShard or Cross Shard
                viewServer.transactionsCount++;

                int cluster = Utils.FindClusterOfDataItem(transactionInputConfig.getTransaction().getSender());
                Thread.sleep(2*GlobalConfigs.TransactionTimeout);
                viewServer.sendTransactionToServer(transactionInputConfig, tnxLine.clusterContactServermapping.get(cluster));



//                if( !transactionInputConfig.getTransaction().getIsCrossShard() ){
//                    //Intra Shard.. can send to contact server from the shard.
//
//                }
//                else{
//                    //Cross Shard.. need to send to both servers && wait for the Prepare Responses from both leaders
//
//                    viewServer.sendCrossShardTransaction(transactionInputConfig,
//                            tnxLine.clusterContactServermapping.get(Utils.FindClusterOfDataItem(transactionInputConfig.getTransaction().getSender())),
//                            tnxLine.clusterContactServermapping.get(Utils.FindClusterOfDataItem(transactionInputConfig.getTransaction().getReceiver())));
//
//                    //Thread.sleep(100);
//                }
            }

            System.out.println("All Transactions Sent for final Test set. ");
            while(true) {
                System.out.println("Press Enter to run all commands ");
                String inp= System.console().readLine();
                if(inp.contains("stop")) break;
                Thread.sleep(30);
                viewServer.sendCommandToServers(Command.PrintDB);
                viewServer.sendCommandToServers(Command.PrintLog);
                viewServer.sendCommandToServers(Command.PrintDataStore);
                viewServer.sendCommandToServers(Command.PrintBalance);
                viewServer.sendCommandToServers(Command.Performance);
            }

System.out.println("Press Enter to Initiate ReSharding");
            System.console().readLine();

            viewServer.InitiateReSharding();

        }
        else {
            System.out.println("File does not exist");
        }

    }


    public void InitiateReSharding() throws InterruptedException {

        HashMap<Integer, ReShardingInitData> reshardingInitData = new HashMap<>();

        this.database.deleteAllBalances();

        for (String servers: this.lastexecTnxLine.clusterContactServermapping.values()) {
            int server = Integer.parseInt(servers.replaceAll("S",""));
            if(server == 0) continue;

            ReShardingInitData initData = this.serversToPaxosStub.get(server).reShardingInitiation( Empty.newBuilder().build() );
            reshardingInitData.put(initData.getClusterId(), initData);

            this.database.insertBalances(initData.getAccountBalancesMap());
        }


        HashMap<Integer, Integer> allBalances = this.database.getAllBalances();





        LogUtils reshardingLogger = new LogUtils("ReSharding",0);

        System.out.println("Sending for Resharding... New Updated Reshard Config can be found in Logs/0-ReSharding.txt\n1. Resharding with HotKey and Round-Robin\n2. Resharding with Hotkey and swaps.. to reduce reshuffle of data across clusters");

        List<Transaction> transactionList = new ArrayList<>(viewServer.transactions.values());

        //Resharding.reshardUsingHotKeyAndRoundRobin( transactionList , GlobalConfigs.DataItemToClusterMap, GlobalConfigs.numClusters, reshardingLogger);

        HashMap<Integer, Integer> newConfig = Resharding.reshardWithSwapHotKey(transactionList, GlobalConfigs.DataItemToClusterMap, GlobalConfigs.numClusters, reshardingLogger);

        GlobalConfigs.DataItemToClusterMap = newConfig;

        System.out.println("Re-Sharding Completed. Initiating Re-Sharding Process between Servers");


        HashMap<Integer, HashMap<Integer, Integer>> clusterBalancesMap = new HashMap<>();

        for(int cluster = 1; cluster <= GlobalConfigs.numClusters; cluster++){
            clusterBalancesMap.put(cluster, new HashMap<>());
        }

        for(Map.Entry<Integer, Integer> entry : allBalances.entrySet()){
            int cluster = Utils.FindClusterOfDataItem(entry.getKey());
            clusterBalancesMap.get(cluster).put(entry.getKey(), entry.getValue());
        }


        ReShardThread[] threads = new ReShardThread[GlobalConfigs.TotalServers+1];

        for (int server : GlobalConfigs.ServerToPortMap.keySet()){
            if(server == 0) continue;

            ReShardingData data = ReShardingData.newBuilder()
                    .setClusterId(Utils.FindMyCluster(server))
                    .putAllAccountBalances(clusterBalancesMap.get(Utils.FindMyCluster(server)))
                    .putAllNewDataItemClusterConfig(GlobalConfigs.DataItemToClusterMap)
                    .build();

            threads[server] = new ReShardThread(this, server, data);
        }

        for (int server : GlobalConfigs.ServerToPortMap.keySet()){
            if(threads[server] != null)
                threads[server].start();
        }

        for (int server : GlobalConfigs.ServerToPortMap.keySet()){
            if(threads[server] != null)
                threads[server].join();
        }

        System.out.println("ReSharding Process Completed in all servers !!!");
    }

}
