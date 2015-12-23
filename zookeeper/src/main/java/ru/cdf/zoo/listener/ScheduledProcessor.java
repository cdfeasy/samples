package ru.cdf.zoo.listener;

import org.apache.curator.framework.CuratorFramework;
import ru.cdf.zoo.ZPath;
import ru.cdf.zoo.client.ZooClient;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by dmitry on 20.12.2015.
 */
public class ScheduledProcessor extends AbstractListenerProcessor {
    private ZooClient client;
    private ScheduledExecutorService executorService;
    private Integer timeOut;
    private ZPath root;
    public ScheduledProcessor(ZooClient client,Integer timeOut){
        this.client=client;
        this.timeOut=timeOut;
        executorService= Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void start() throws Exception {
        root=ZPath.getZPath(client,"/",null);
    }

    @Override
    public void stop() throws IOException {

    }

    private class CheckListeners implements Runnable{
        @Override
        public void run() {

        }
    }

}
