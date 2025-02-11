package org.cse535.database;

import org.cse535.Main;
import org.cse535.configs.GlobalConfigs;
import org.cse535.configs.Utils;
import org.cse535.node.Node;
import org.cse535.node.NodeServer;
import org.cse535.proto.*;
import org.cse535.threadimpls.IntraShardTnxProcessingThread;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

public class DatabaseService {


    public Integer serverNumber;

    public PriorityBlockingQueue<TransactionInputConfig> incomingTransactionsQueue;

    public HashSet<Integer> processedTransactionsSet;

    public Connection connection;
    public Statement statement;

    public Lock lock;



    public int lastAcceptedUncommittedBallotNumber;
    public Transaction lastAcceptedUncommittedTransaction;

    public int lastCommittedBallotNumber;
    public Transaction lastCommittedTransaction;

    public AtomicReference<ArrayList<String>> dataStore;

    HashSet<String> dataStoreBackup = new HashSet<>();

    public NodeServer node;






    //TransactionNum : SeqNum
    public HashMap<Integer, Integer> transactionNumSeqNumMap = new HashMap<>();

    // SeqNum : TransactionStatus
    public HashMap<Integer, TransactionStatus> transactionStatusMap = new HashMap<>();

    // SeqNum : Transaction
    public HashMap<Integer, Transaction> transactionMap = new HashMap<>();

    // SeqNum : ViewNum
    public HashMap<Integer, Integer> seqNumViewMap = new HashMap<>();

    // SeqNum : PrePrepareRequest
    public HashMap<Integer, PrePrepareRequest> prePrepareRequestMap = new HashMap<>();
    // SeqNum : PrePrepareResponse
    public HashMap<Integer, List<PrePrepareResponse>> prePrepareResponseMap = new HashMap<>();

    // SeqNum : PrepareRequest
    public HashMap<Integer, PrepareRequest> prepareRequestMap = new HashMap<>();
    // SeqNum : PrepareResponse
    public HashMap<Integer, List<PrepareResponse>> prepareResponseMap = new HashMap<>();

    //SeqNum : CommitRequest
    public HashMap<Integer, CommitRequest> commitRequestMap = new HashMap<>();
    //SeqNum : CommitResponse
    public HashMap<Integer, List<CommitResponse>> commitResponseMap = new HashMap<>();

    public ConcurrentHashMap<Integer, Boolean> crossShardCommitsReceived = new ConcurrentHashMap<>();

    // SeqNum : ExecutionReply
    public HashMap<Integer, ExecutionReply> executionReplyMap = new HashMap<>();


//    // Client: Balance
//    public ConcurrentHashMap<String, Integer> accountsMap = new ConcurrentHashMap<>();


    //Already triggered Views - to restrict multiple triggers for same view
    public HashSet<Integer> viewTriggers = new HashSet<>();
    public ConcurrentHashMap<Integer, Boolean> viewsTriggered = new ConcurrentHashMap<>();




    public AtomicInteger currentSeqNum = new AtomicInteger(0);
    public AtomicInteger currentViewNum = new AtomicInteger(0);


    public PriorityBlockingQueue<TransactionInputConfig> incomingTnxQueue = new PriorityBlockingQueue<>(100, new Comparator<TransactionInputConfig>() {
        @Override
        public int compare(TransactionInputConfig o1, TransactionInputConfig o2) {
            return o1.getTransaction().getTransactionNum() - o2.getTransaction().getTransactionNum();
        }
    });

    public AtomicBoolean isLeader = new AtomicBoolean(false);
    public AtomicBoolean isServerActive = new AtomicBoolean(true);
    public AtomicBoolean isServerByzantine = new AtomicBoolean(false);



    public AtomicInteger lastExecutedSeqNum = new AtomicInteger(0);
    public AtomicInteger maxAddedSeqNum = new AtomicInteger(0);



    //TransactionNum : Map< cluster , seqnum >
    public ConcurrentHashMap<Integer, HashMap<Integer, CommitRequest>>  crossShardPrepareResponses = new ConcurrentHashMap<>();







    public DatabaseService( Integer serverNum, NodeServer node) {
        this.lastAcceptedUncommittedBallotNumber = -1;
        this.lastAcceptedUncommittedTransaction = null;
        this.lastCommittedTransaction = null;
        this.lastCommittedBallotNumber = -1;

        this.serverNumber = serverNum;

        this.incomingTransactionsQueue = new PriorityBlockingQueue<>(100, new Comparator<TransactionInputConfig>() {
            @Override
            public int compare(TransactionInputConfig o1, TransactionInputConfig o2) {
                return o1.getTransaction().getTransactionNum() - o2.getTransaction().getTransactionNum();
            }
        });

        this.transactionMap = new HashMap<>();
        this.processedTransactionsSet = new HashSet<>();


        this.dataStore = new AtomicReference<>(new ArrayList<>());
        this.node = node;

        this.lockedDataItemsWithTransactionNum = new ConcurrentHashMap<>();
        this.writeAheadLog = new ConcurrentHashMap<>();

        this.transactionStatusMap = new HashMap<>();

        initializeSQLiteDatabase();
    }


    public void initializeSQLiteDatabase() {


        try {
            Class.forName("org.sqlite.JDBC");


            connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\mlakkoju\\2pcbyz-madhulakkoju\\PBFT-2pc\\Databases\\Database-" + this.serverNumber + ".db");
            statement = connection.createStatement();

            //Account Table - to store balances

            statement.executeUpdate("DELETE FROM accounts;");

            String createTableSQL = "CREATE TABLE IF NOT EXISTS accounts (" +
                    "id INTEGER PRIMARY KEY, " +
                    "amount INTEGER NOT NULL" +
                    ");";

            statement.executeUpdate(createTableSQL);

            int cluster = Utils.FindMyCluster(this.serverNumber);

            for (Integer i : Utils.GetAllDataItemsInCluster(cluster)) {
                String insertSQL = "INSERT INTO accounts (id, amount) VALUES ("
                        + i + ", " + GlobalConfigs.InitialBalance + ");";
                statement.executeUpdate(insertSQL);
            }


            //Transactions Table - to store transactions and ballot numbers

            statement.executeUpdate("DELETE FROM transactions;");

            createTableSQL = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "ballot INTEGER PRIMARY KEY, " +
                    "transactionNum INTEGER NOT NULL, " +
                    "sender INTEGER NOT NULL, " +
                    "receiver INTEGER NOT NULL, " +
                    "amount INTEGER NOT NULL, " +
                    "isCrossShard BOOLEAN NOT NULL" +
                    ");";


            statement.executeUpdate(createTableSQL);


            //Transaction Status Table - to store status of transactions

            statement.executeUpdate("DELETE FROM transactionstatus;");

            createTableSQL = "CREATE TABLE IF NOT EXISTS transactionstatus (" +
                    "ballot INTEGER PRIMARY KEY, " +
                    "status INTEGER NOT NULL " +
                    ");";

            statement.executeUpdate(createTableSQL);

            initWALAndLockTables();


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }


    public synchronized boolean isValidTransaction(Transaction transaction) {
        int sender = transaction.getSender();
        int amount = transaction.getAmount();

        if (Utils.FindClusterOfDataItem(sender) == this.node.clusterNumber) {
            int senderBalance = getBalance(sender);

            if (transaction.getReceiver2() != 0) {
                return senderBalance >= (amount + transaction.getAmount2());
            }

            return senderBalance >= amount;
        }
        return true;
    }

    public boolean executeTransaction(Transaction transaction) {

        if (transaction == null) return false;

        this.node.walLogger.log(transaction.getTransactionNum() + " - BEGIN TNX : " + Utils.toDataStoreString(transaction));

        int sender = transaction.getSender();
        int receiver = transaction.getReceiver();
        int amount = transaction.getAmount();

        this.node.walLogger.log(transaction.getTransactionNum() + " - READ : " + sender);

        int senderBalance = getBalance(sender);

        if (senderBalance < amount) {
            this.node.walLogger.log(transaction.getTransactionNum() + " - ABORTED : " + Utils.toDataStoreString(transaction));
            return false;
        }

        this.node.walLogger.log(transaction.getTransactionNum() + " - READ : " + receiver);

        int receiverBalance = getBalance(receiver);

        this.node.walLogger.log(transaction.getTransactionNum() + " - BEFORE TNX : " + Utils.toDataStoreString(transaction));

        if (Utils.FindClusterOfDataItem(receiver) == this.node.clusterNumber) {
            updateBalance(sender, senderBalance - amount);
            this.node.walLogger.log(transaction.getTransactionNum() + " - WRITE : " + sender + " : " + (senderBalance - amount));
        }


        if (Utils.FindClusterOfDataItem(receiver) == this.node.clusterNumber) {
            updateBalance(receiver, receiverBalance + amount);
            this.node.walLogger.log(transaction.getTransactionNum() + " - WRITE : " + receiver + " : " + (receiverBalance + amount));
        }

        this.node.walLogger.log(transaction.getTransactionNum() + " - COMMIT : " + Utils.toDataStoreString(transaction));

        return true;
    }

    public synchronized void updateBalance(int account, int amount) {
        try {
            String updateSQL = "UPDATE accounts SET amount = " + amount + " WHERE id = " + account + ";";
            statement.executeUpdate(updateSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized int getBalance(int account) {
        try {
            String selectSQL = "SELECT amount FROM accounts WHERE id = " + account + ";";
            return statement.executeQuery(selectSQL).getInt("amount");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public synchronized HashMap<Integer, Integer> getAllBalances() {
        HashMap<Integer, Integer> balances = new HashMap<>();
        try {
            String selectSQL = "SELECT * FROM accounts;";
            ResultSet resultSet = statement.executeQuery(selectSQL);

            while (resultSet.next()) {

                balances.put(
                        resultSet.getInt("id"),
                        resultSet.getInt("amount")
                );

            }
            return balances;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return balances;

    }

    public synchronized void updateBalances(Map<Integer, Integer> balances) {
        try {
            for (Integer account : balances.keySet()) {
                String updateSQL = "UPDATE accounts SET amount = " + balances.get(account) + " WHERE id = " + account + ";";
                statement.executeUpdate(updateSQL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void deleteAllBalances() {
        try {
            String deleteSQL = "DELETE FROM accounts;";
            statement.executeUpdate(deleteSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void insertBalances(Map<Integer, Integer> balances) {
        try {
            for (Integer account : balances.keySet()) {
                String insertSQL = "INSERT INTO accounts (id, amount) VALUES (" + account + ", " + balances.get(account) + ");";
                statement.executeUpdate(insertSQL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public synchronized void addToDataStore(PrepareRequest entry) {
        String str = Utils.toDataStoreString(entry);
        if (this.dataStoreBackup.contains(str)) {
            return;
        }
        this.dataStore.get().add(str);
        this.dataStoreBackup.add(str);
    }

    public synchronized void addToDataStore(CommitRequest entry) {
        if (entry == null) return;
        if (!entry.hasTransaction()) {
            entry = entry.toBuilder().setTransaction(transactionMap.get(entry.getSequenceNumber())).build();
        }
        if (entry.getTransaction().getSender() == 0 || entry.getTransaction().getReceiver() == 0) {
            return;
        }
        this.dataStore.get().add(Utils.toDataStoreString(entry));
        this.dataStoreBackup.add(Utils.toDataStoreString(entry));
    }


    public synchronized void addCrossShardCommitToDataStore(CommitRequest entry) {
        String str = Utils.toDataStoreStringCrossShardCommit(entry);
        if (this.dataStoreBackup.contains(str)) {
            return;
        }

        if (entry.getTransaction().getSender() == 0 || entry.getTransaction().getReceiver() == 0) {
            return;
        }

        if (entry.getAbort()) return;

        this.dataStore.get().add(str);
        this.dataStoreBackup.add(str);
    }

    public synchronized void addCrossShardPrepareToDataStore(CommitRequest entry) {
        String str = Utils.toDataStoreStringCrossShardPrepare(entry);
        if (this.dataStoreBackup.contains(str)) {
            return;
        }

        if (entry.getTransaction().getSender() == 0 || entry.getTransaction().getReceiver() == 0) {
            return;
        }
        this.dataStore.get().add(str);
        this.dataStoreBackup.add(str);
    }


    public ConcurrentHashMap<Integer, Integer> lockedDataItemsWithTransactionNum;


    public synchronized boolean lockDataItem(int dataItem, int transactionNum) {

        if (Utils.FindClusterOfDataItem(dataItem) != this.node.clusterNumber) {
            return false;
        }

        this.node.walLogger.log(transactionNum + " - LOCK : " + dataItem + " : " + transactionNum);
        lockedDataItemsWithTransactionNum.put(dataItem, transactionNum);
        lockDataItemEntry(dataItem, transactionNum);
        return true;
    }

    public synchronized boolean unlockDataItem(int dataItem, int transactionNum) {

        if (Utils.FindClusterOfDataItem(dataItem) != this.node.clusterNumber) {
            return false;
        }
        this.node.walLogger.log(transactionNum + " - UNLOCK : " + dataItem + " : " + transactionNum);
        lockedDataItemsWithTransactionNum.remove(dataItem);
//        if(lockedDataItemsWithTransactionNum.containsKey(dataItem) && lockedDataItemsWithTransactionNum.get(dataItem) == transactionNum){
//            return true;
//        }
        return false;
    }

    public boolean isDataItemLocked(int dataItem) {
        if (Utils.FindClusterOfDataItem(dataItem) != this.node.clusterNumber) {
            return false;
        }
        return lockedDataItemsWithTransactionNum.containsKey(dataItem);
        //return getDataItemLockedTnx(dataItem) != -1;

    }

    public synchronized boolean isDataItemLockedWithTnx(int dataItem, int transactionNum) {
        if (Utils.FindClusterOfDataItem(dataItem) != this.node.clusterNumber) {
            return false;
        }
        return lockedDataItemsWithTransactionNum.containsKey(dataItem) && lockedDataItemsWithTransactionNum.get(dataItem) == transactionNum;
    }












    public static class WALEntry{
        public int transactionNum;
        public int sender;
        public int receiver;
        public int senderOldBalance;
        public int receiverOldBalance;

        public int receiver2;
        public int receiver2OldBalance;

        public WALEntry(int transactionNum, int sender, int receiver, int senderOldBalance, int receiverOldBalance) {
            this.transactionNum = transactionNum;
            this.sender = sender;
            this.receiver = receiver;
            this.senderOldBalance = senderOldBalance;
            this.receiverOldBalance = receiverOldBalance;

        }

        public WALEntry(int transactionNum, int sender, int receiver, int senderOldBalance, int receiverOldBalance, int receiver2, int receiver2OldBalance) {
            this.transactionNum = transactionNum;
            this.sender = sender;
            this.receiver = receiver;
            this.senderOldBalance = senderOldBalance;
            this.receiverOldBalance = receiverOldBalance;
            this.receiver2 = receiver2;
            this.receiver2OldBalance = receiver2OldBalance;
        }

    }


    public ConcurrentHashMap<Integer, WALEntry> writeAheadLog;

    public void writeToWAL(Transaction transaction) {
        int sender = transaction.getSender();
        int receiver = transaction.getReceiver();
        int amount = transaction.getAmount();

        int senderBalance = getBalance(sender);
        int receiverBalance = getBalance(receiver);




        if(Utils.FindClusterOfDataItem(sender) == this.node.clusterNumber ) {
            updateBalance(sender, senderBalance - amount);
            this.node.walLogger.log(transaction.getTransactionNum() + " - WRITE TO WAL: " + sender + " :  new :: " + (senderBalance - amount) + " : old :: " + senderBalance);
        }


        if (Utils.FindClusterOfDataItem(receiver) == this.node.clusterNumber) {
            updateBalance(receiver, receiverBalance + amount);
            this.node.walLogger.log(transaction.getTransactionNum() + " - WRITE TO WAL : " + receiver + " : " + (receiverBalance + amount));
        }


        if (transaction.getReceiver2() != 0) {
            int receiver2 = transaction.getReceiver2();
            int amount2 = transaction.getAmount2();
            int receiver2Balance = getBalance(receiver2);

            if (Utils.FindClusterOfDataItem(receiver2) == this.node.clusterNumber) {
                updateBalance(receiver2, receiver2Balance + amount2);
                this.node.walLogger.log(transaction.getTransactionNum() + " - WRITE TO WAL : " + receiver2 + " : " + (receiver2Balance + amount2));
                writeAheadLog.put(transaction.getTransactionNum(), new WALEntry(transaction.getTransactionNum(), sender, receiver, senderBalance, receiverBalance, receiver2, receiver2Balance));
                return;
            }
        }


        writeAheadLog.put(transaction.getTransactionNum(), new WALEntry(transaction.getTransactionNum(), sender, receiver, senderBalance, receiverBalance));

    }

    public synchronized void rollbackWAL(int transactionNum) {

        WALEntry entry = null;
        entry = getWALEntry(transactionNum);

        entry = writeAheadLog.get(transactionNum);

        if (entry == null) {
            return;
        }

        int sender = entry.sender;
        int receiver = entry.receiver;
        int senderOldBalance = entry.senderOldBalance;
        int receiverOldBalance = entry.receiverOldBalance;

        if (sender != -1) {
            updateBalance(sender, senderOldBalance);
            this.node.walLogger.log(transactionNum + " - ROLLBACK : " + sender + " : " + senderOldBalance);
        }

        if (receiver != -1) {
            updateBalance(receiver, receiverOldBalance);
            this.node.walLogger.log(transactionNum + " - ROLLBACK : " + receiver + " : " + receiverOldBalance);
        }

        int receiver2 = entry.receiver2;
        int receiver2OldBalance = entry.receiver2OldBalance;

        if (receiver2 > 0) {
            updateBalance(receiver2, receiver2OldBalance);
            this.node.walLogger.log(transactionNum + " - ROLLBACK : " + receiver2 + " : " + receiver2OldBalance);
        }

    }

    public synchronized void commitWAL(int transactionNum) {
        writeAheadLog.remove(transactionNum);
        DeleteWALEntry(transactionNum);
    }












    public synchronized void addTransaction(int ballotNumber, Transaction transaction) {

        //Transaction tnx = getTransaction(ballotNumber);

        if (!this.transactionMap.containsKey(ballotNumber)) {

            try {
                String insertSQL = "INSERT INTO transactions (ballot, transactionNum, sender, receiver, amount, isCrossShard ) VALUES ("
                        + ballotNumber + ", " + transaction.getTransactionNum() + ", "
                        + transaction.getSender() + ", " + transaction.getReceiver() + ", "
                        + transaction.getAmount() + ", " + transaction.getIsCrossShard()
                        + ");";
                statement.executeUpdate(insertSQL);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            try {
                String insertSQL = "UPDATE transactions SET" +
                        " transactionNum = " + transaction.getTransactionNum()
                        + ", sender = " + transaction.getSender()
                        + ", receiver = " + transaction.getReceiver()
                        + ", amount = " + transaction.getAmount()
                        + ", isCrossShard = " + transaction.getIsCrossShard()
                        + " WHERE ballot = " + ballotNumber + ";";

                statement.executeUpdate(insertSQL);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }

        this.transactionMap.put(ballotNumber, transaction);
    }

    public Transaction getTransaction(int ballotNumber) {
        Transaction.Builder transactionBuilder = Transaction.newBuilder().setTransactionNum(-1);

        try {
            String selectSQL = "SELECT * FROM transactions WHERE ballot = " + ballotNumber + ";";

            ResultSet resultSet = statement.executeQuery(selectSQL);

            transactionBuilder.setTransactionNum(resultSet.getInt("transactionNum"));
            transactionBuilder.setSender(resultSet.getInt("sender"));
            transactionBuilder.setReceiver(resultSet.getInt("receiver"));
            transactionBuilder.setAmount(resultSet.getInt("amount"));
            transactionBuilder.setIsCrossShard(resultSet.getBoolean("isCrossShard"));

            return transactionBuilder.build();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactionBuilder.build();
    }




    public synchronized void addTransactionStatus( int ballotNumber, TransactionStatus status ) {

        transactionStatusMap.put(ballotNumber, status);
//        return;

        TransactionStatus transactionStatus = getTransactionStatus(ballotNumber);

        if(transactionStatus != TransactionStatus.PENDING){

            try {
                String insertSQL = "UPDATE transactionstatus SET status =" + status.getNumber() + " WHERE ballot = " + ballotNumber + ";";
                statement.executeUpdate(insertSQL);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }

        }
        else{
            try {
                String insertSQL = "INSERT INTO transactionstatus (ballot, status ) VALUES ("
                        + ballotNumber + ", " + status.getNumber()
                        + ");";
                statement.executeUpdate(insertSQL);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }



    }

    public TransactionStatus getTransactionStatus(int ballotNumber) {

        if (transactionStatusMap.containsKey(ballotNumber)) {
            return transactionStatusMap.get(ballotNumber);
        }

        try {
            String selectSQL = "SELECT * FROM transactionstatus WHERE ballot = " + ballotNumber + ";";

            ResultSet resultSet = statement.executeQuery(selectSQL);

            return TransactionStatus.forNumber(resultSet.getInt("status"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return TransactionStatus.PENDING;
    }


    public void initiateExecutions() {

        Main.node.logger.log("Initiating executions : " + lastExecutedSeqNum.get() + " : " + maxAddedSeqNum.get());

        while (lastExecutedSeqNum.get() <= maxAddedSeqNum.get()) {

            int seqNum = lastExecutedSeqNum.get() + 1;

            Main.node.logger.log("Initiating execution for seqNum: " + seqNum);
            Main.node.logger.log("Status : " + (transactionStatusMap.containsKey(seqNum) ? transactionStatusMap.get(seqNum) : "Not Found"));

            if (transactionStatusMap.containsKey(seqNum) && transactionStatusMap.get(seqNum) == TransactionStatus.COMMITTED) {
                // Execute the transaction
                executeTransaction(seqNum);
                lastExecutedSeqNum.set(seqNum);
            } else if (transactionMap.containsKey(seqNum) && transactionStatusMap.get(seqNum) == TransactionStatus.EXECUTED) {
                lastExecutedSeqNum.set(seqNum);
            } else {
                break;
            }
        }
    }

    public void executeTransaction(int seqNum) {

        if (this.transactionStatusMap.containsKey(seqNum) && this.transactionStatusMap.get(seqNum) == TransactionStatus.EXECUTED) {
            return;
        }

        Main.node.logger.log("Executing transaction: " + seqNum);
        Transaction transaction = transactionMap.get(seqNum);

        if (transaction == null) return;

        if (!transaction.getIsCrossShard()) {
            boolean success = executeTransaction(transaction);
            String failureReason = "";

            if (!success) {
                Main.node.logger.log("Transaction failed: " + seqNum);
                this.transactionStatusMap.put(seqNum, TransactionStatus.ABORTED);
                failureReason = "Execute Failed";
            } else {
                this.transactionStatusMap.put(seqNum, TransactionStatus.EXECUTED);
            }

            this.node.database.unlockDataItem(transaction.getSender(), transaction.getTransactionNum());
            this.node.database.unlockDataItem(transaction.getReceiver(), transaction.getTransactionNum());

            if (!transaction.getIsCrossShard()) {
                Main.node.sendExecutionReplyToClient(transaction, success, failureReason, success ? "EXECUTED" : "COMMITTED");
                Main.node.logger.log("Sent Reply to Client executed: " + seqNum);
            }

        } else {
            this.node.logger.log("WAL: Cross Shard Transaction: " + Utils.toDataStoreString(transaction));
            this.writeToWAL(transaction);
        }

    }


    public ReShardingInitData getReshardingInitData() {
        ReShardingInitData.Builder builder = ReShardingInitData.newBuilder();
        builder.setClusterId(this.node.clusterNumber);
        builder.putAllAccountBalances(getAllBalances());
        return builder.build();
    }


    public void processReshardingData(ReShardingData data) {

        GlobalConfigs.DataItemToClusterMap = new HashMap<>();

        GlobalConfigs.DataItemToClusterMap.putAll(data.getNewDataItemClusterConfigMap());

        deleteAllBalances();
        insertBalances(data.getAccountBalancesMap());
    }


    public void setLastExecutedSeqNum(int seqNum) {
        lastExecutedSeqNum.set(seqNum);
    }

    public void setMaxAddedSeqNum(int seqNum) {
        maxAddedSeqNum.set(Math.max(seqNum, maxAddedSeqNum.get()));
    }


    void initWALAndLockTables() throws SQLException {

        statement.executeUpdate("DELETE FROM locks;");

        String createTableSQL = "CREATE TABLE IF NOT EXISTS locks (" +
                "dataitem INTEGER PRIMARY KEY, " +
                "transactionNum INTEGER NOT NULL " +
                ");";

        statement.executeUpdate(createTableSQL);


        statement.executeUpdate("DELETE FROM wal;");

        createTableSQL = "CREATE TABLE IF NOT EXISTS wal (" +
                "transactionNum INTEGER PRIMARY KEY, " +
                "sender INTEGER NOT NULL, " +
                "receiver INTEGER NOT NULL, " +
                "senderOldBalance INTEGER NOT NULL, " +
                "receiverOldBalance INTEGER NOT NULL, " +
                "receiver2 INTEGER NOT NULL, " +
                "receiver2OldBalance INTEGER NOT NULL " +
                ");";

        statement.executeUpdate(createTableSQL);
    }

    public WALEntry getWALEntry(int transactionNum) {
        try {


            String selectSQL = "SELECT * FROM wal WHERE transactionNum = " + transactionNum + ";";

            ResultSet resultSet = statement.executeQuery(selectSQL);

            if (!resultSet.next()) {
                return null;
            }

            int sender = resultSet.getInt("sender");
            int receiver = resultSet.getInt("receiver");
            int senderOldBalance = resultSet.getInt("senderOldBalance");
            int receiverOldBalance = resultSet.getInt("receiverOldBalance");
            int receiver2 = resultSet.getInt("receiver2");
            int receiver2OldBalance = resultSet.getInt("receiver2OldBalance");

            return new WALEntry(transactionNum, sender, receiver, senderOldBalance, receiverOldBalance, receiver2, receiver2OldBalance);
        } catch (SQLException e) {

        }
        return null;

    }

    public void DeleteWALEntry(int transactionNum) {
        try {
            String deleteSQL = "DELETE FROM wal WHERE transactionNum = " + transactionNum + ";";
            statement.executeUpdate(deleteSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getDataItemLockedTnx(int dataItem) {
        try {
            String selectSQL = "SELECT * FROM locks WHERE dataitem = " + dataItem + ";";
            ResultSet resultSet = statement.executeQuery(selectSQL);
            if (!resultSet.next()) {
                return -1;
            }
            return resultSet.getInt("transactionNum");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void lockDataItemEntry(int dataItem, int transactionNum) {
        try {
            String insertSQL = "INSERT INTO locks (dataitem, transactionNum) VALUES (" + dataItem + ", " + transactionNum + ");";
            statement.executeUpdate(insertSQL);
            return;
        }
        catch(Exception e) {

            try {
                String updateSQL = "UPDATE locks SET transactionNum = " + transactionNum + " WHERE dataitem = " + dataItem + ";";
                statement.executeUpdate(updateSQL);
                return;
            }
            catch(Exception e1) {

            }

        }
    }







}
