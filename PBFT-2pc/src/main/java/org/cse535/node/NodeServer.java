package org.cse535.node;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.cse535.configs.GlobalConfigs;
import org.cse535.configs.Utils;
import org.cse535.database.DatabaseService;
import org.cse535.loggers.LogUtils;
import org.cse535.proto.*;
import org.cse535.service.ActivateServersService;
import org.cse535.service.CommandsService;
import org.cse535.service.LinearPBFTService;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;


public class NodeServer {

    public int port;
    public LogUtils logger;
    public LogUtils commandLogger;
    public LogUtils walLogger;

    public AtomicBoolean isServerActive;

    public Integer serverNumber;
    public String serverName;
    public Integer clusterNumber;

    public DatabaseService database;

    // GRPC Server Items
    public Server server;
    public HashMap<Integer, ManagedChannel> serversToChannel;
    public HashMap<Integer, LinearPBFTGrpc.LinearPBFTBlockingStub> serversToPaxosStub;
    public HashMap<Integer, ActivateServersGrpc.ActivateServersBlockingStub> serversToActivateServersStub;
    public HashMap<Integer, CommandsGrpc.CommandsBlockingStub> serversToCommandsStub;

    public NodeServer(Integer serverNum, int port) {
        this.port = port;
        this.serverNumber = serverNum;
        this.serverName = "S"+serverNum;

        this.clusterNumber = Utils.FindMyCluster(serverNum);

        this.logger = new LogUtils(port);
        this.commandLogger = new LogUtils("Commands", port);
        this.walLogger = new LogUtils(port, true);



        initiateChannelsAndStubs();

        this.server = ServerBuilder.forPort(port)
                .addService(new LinearPBFTService ())
                .addService(new ActivateServersService())
                .addService(new CommandsService())
                .build();

    }

    //Setup Channels Stubs for GRPC
    public void initiateChannelsAndStubs() {
        serversToChannel = new HashMap<>();


        serversToPaxosStub = new HashMap<>();
        serversToActivateServersStub = new HashMap<>();
        serversToCommandsStub = new HashMap<>();

        isServerActive = new AtomicBoolean(true);

        database = new DatabaseService(this.serverNumber, this);

        System.out.println("Current Server: " + serverName + " Port: " + port);

        GlobalConfigs.ServerToPortMap.forEach((serverNum, port) -> {
            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build();

            System.out.println("Channel created for server: " + serverNum);

            serversToChannel.put(serverNum, channel);
            serversToPaxosStub.put(serverNum, LinearPBFTGrpc.newBlockingStub(channel));
            serversToActivateServersStub.put(serverNum, ActivateServersGrpc.newBlockingStub(channel));
            serversToCommandsStub.put(serverNum, CommandsGrpc.newBlockingStub(channel));
        });


    }

}
