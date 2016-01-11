package com.ifree.zoo;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by d.asadullin on 17.03.2015.
 */
public class Server {
    ZooKeeperServerMain zooKeeperServer;
    Thread thread;
    ExecutorService service= Executors.newFixedThreadPool(1);
    public void start(){
        Properties properties=new Properties();
        /*
        tickTime=2000
initLimit=10
syncLimit=5
dataDir=/tmp/zookeeper

clientPort=2181
         */
        properties.put("tickTime","2000");
        properties.put("initLimit","10");
        properties.put("syncLimit","5");
        properties.put("dataDir","zoo");
        properties.put("clientPort","2181");
        QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
        try {
            quorumConfiguration.parseProperties(properties);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        zooKeeperServer = new ZooKeeperServerMain();
        final ServerConfig configuration = new ServerConfig();
        configuration.readFrom(quorumConfiguration);

        Runnable task=new Runnable() {
            public void run() {
                try {
                    zooKeeperServer.runFromConfig(configuration);
                } catch (IOException e) {
                }
            }
        };
        service.submit(task);
    }
    public void stop(){
        service.shutdown();
    }

    public static void main(String[] args){
        Properties properties=new Properties();
        /*
        tickTime=2000
initLimit=10
syncLimit=5
dataDir=/tmp/zookeeper

clientPort=2181
         */
        properties.put("tickTime","2000");
        properties.put("initLimit","10");
        properties.put("syncLimit","5");
        properties.put("dataDir","zoo");
        properties.put("clientPort","2181");
        QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
        try {
            quorumConfiguration.parseProperties(properties);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        final ZooKeeperServerMain zooKeeperServer = new ZooKeeperServerMain();
        final ServerConfig configuration = new ServerConfig();
        configuration.readFrom(quorumConfiguration);

        new Thread() {
            public void run() {
                try {
                    zooKeeperServer.runFromConfig(configuration);
                } catch (IOException e) {
                }
            }
        }.start();


    }
}
