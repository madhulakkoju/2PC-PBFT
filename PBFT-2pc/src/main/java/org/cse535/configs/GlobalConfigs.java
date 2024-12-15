package org.cse535.configs;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalConfigs {

    public static final HashMap<Integer, Integer> ServerToPortMap = new HashMap<Integer, Integer>(); // server number to port map

    public static final Integer basePort = 8000;

    public static final Integer ViewServerPort = 8000;
    public static final String ViewServerName = "VS";

    public static Integer TotalServers;

    public static int numServersPerCluster;
    public static int numClusters;

    public static int TotalDataItems;

    public static int InitialBalance = 10;


    public static int f = 1;
    public static int ShardConsesusThreshold = 2*f + 1;




    public static HashMap<String, KeyPair> serversToSignKeys = new HashMap<String, KeyPair>(){{
        put(ViewServerName,  PBFTSignUtils.generateKeyPairFromText2(ViewServerName) );
        put("S1",  PBFTSignUtils.generateKeyPairFromText2("S1") );
        put("S2",  PBFTSignUtils.generateKeyPairFromText2("S2"));
        put("S3",  PBFTSignUtils.generateKeyPairFromText2("S3"));
        put("S4",  PBFTSignUtils.generateKeyPairFromText2("S4"));
        put("S5",  PBFTSignUtils.generateKeyPairFromText2("S5"));
        put("S6",  PBFTSignUtils.generateKeyPairFromText2("S6"));
        put("S7",  PBFTSignUtils.generateKeyPairFromText2("S7"));
        put("S8",  PBFTSignUtils.generateKeyPairFromText2("S8"));
        put("S9",  PBFTSignUtils.generateKeyPairFromText2("S9"));
        put("S10",  PBFTSignUtils.generateKeyPairFromText2("S10"));
        put("S11",  PBFTSignUtils.generateKeyPairFromText2("S11"));
        put("S12",  PBFTSignUtils.generateKeyPairFromText2("S12"));
    }};











    // Cluster Number: Max Data Item Number
    public static HashMap<Integer, Integer> clusterShardMap;

    public static HashMap<Integer, List<Integer>> clusterToServersMap;


    public static HashMap<Integer, Integer> DataItemToClusterMap ;
    public static long TransactionTimeout = 3000;


    public static HashMap<Integer, Integer> primaryServers = new HashMap<Integer, Integer>();

    public static void LoadConfigs( ){

        primaryServers.put(1, 1);
        primaryServers.put(2, 5);
        primaryServers.put(3, 9);

        numClusters = GlobalConfigs.TotalServers / GlobalConfigs.numServersPerCluster;
        clusterShardMap = new HashMap<Integer, Integer>();
        clusterToServersMap = new HashMap<Integer, List<Integer>>();

        DataItemToClusterMap = new HashMap<Integer, Integer>();

        clusterShardMap.put(0, 0);


        ServerToPortMap.put(0, ViewServerPort);

        for (int i = 1; i <= TotalServers ; i++) {
            ServerToPortMap.put(i, basePort + i);
        }

        int dataItemsPerCluster = TotalDataItems / numClusters;

        int starter = 0;
        int counter = 1;

        int a = 1;

        for (int i = 1; i <= numClusters; i++) {

            for(int j = 1; j <= dataItemsPerCluster; j++){
                DataItemToClusterMap.put(a++, i);
            }

            starter += dataItemsPerCluster;
            clusterShardMap.put(i, starter);
            clusterToServersMap.put(i, new ArrayList<Integer>());

            for(int j = 0; j < numServersPerCluster; j++){
                clusterToServersMap.get(i).add(counter++);
            }
            System.out.println("Cluster " + i + " has servers: " + clusterToServersMap.get(i).toString());

        }
        clusterShardMap.put(numClusters, TotalDataItems);

        //DataItemToClusterMap.forEach((k,v) -> System.out.println( k + " " + v));


    }


}
