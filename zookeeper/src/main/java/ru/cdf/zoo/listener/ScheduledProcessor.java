package ru.cdf.zoo.listener;

import org.apache.curator.framework.CuratorFramework;
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
    private Map<>
    public ScheduledProcessor(ZooClient client,Integer timeOut){
        this.client=client;
        this.timeOut=timeOut;
        executorService= Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws IOException {

    }

    private class CheckListeners implements Runnable{
        @Override
        public void run() {
            for(String path:)
        }
    }

}
