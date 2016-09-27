package com.ifree.zoo.client;

import com.ifree.zoo.listener.*;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * Created by d.asadullin on 23.11.2015.
 */
public class ZooClientImpl implements ZooClient {
    private CuratorFramework client;

    private String url;
    private RetryPolicy retryPolicy;
    private ListenerType listenerType;
    private Integer listenerTime;
    private ListenerProcessor processor;
    private ZooSerializer serializer;


    public ZooClientImpl(String url, RetryPolicy retryPolicy, ListenerType listenerType, Integer listenerTime, ZooSerializer serializer) {
        this.url = url;
        this.retryPolicy = retryPolicy;
        this.listenerType = listenerType;
        this.listenerTime = listenerTime;
        client = CuratorFrameworkFactory.newClient(url, retryPolicy);
        if (ListenerType.RealTime.equals(listenerType)) {
            processor = new RealTimeProcessor(client);
        } else {
            processor = new ScheduledProcessor(this, listenerTime);
        }
        this.serializer = serializer;
    }

    @Override
    public void start() throws Exception {
        if(!CuratorFrameworkState.STARTED.equals(client.getState())) {
            client.start();
            processor.start();
        }
    }

    @Override
    public void stop() throws IOException {
        if(CuratorFrameworkState.STARTED.equals(client.getState())) {
            processor.stop();
            client.close();
        }
    }

    @Override
    public void addListener(String path, ZooListener listener, boolean checkChildren) {
        processor.registerListener(path, listener, checkChildren);
    }

    @Override
    public void addListener(String path, ZooListener listener) {
        addListener(path, listener, false);
    }

    @Override
    public void removeListener(ZooListener listener) {
        processor.removeListener(listener);
    }

    @Override
    public GetChildrenBuilder getChildren(String path) throws Exception {
        return new GetChildrenBuilder(path,client);
    }

    @Override
    public ZooSerializer getSerializer() {
        return serializer;
    }

    @Override
    public CuratorFramework getCuratorClient() {
        return client;
    }

    @Override
    public GetResultBuilder getData(String path) throws Exception {
        return new GetResultBuilder(path,client,serializer);
    }

    @Override
    public Stat getStat(String path) throws Exception {
        return client.checkExists().forPath(path);
    }

    @Override
    public Stat getStat(String path, CuratorWatcher listener) throws Exception {
        return client.checkExists().usingWatcher(listener).forPath(path);
    }

    @Override
    public void awaitingDelete(List<String> list, Runnable eventListener) throws Exception {
        CountDownLatch latch = new CountDownLatch(list.size());
        CuratorWatcher watcher= event -> {
            if (Watcher.Event.EventType.NodeDeleted.equals(event.getType())) {
                latch.countDown();
                if (latch.getCount() == 0) {
                    eventListener.run();
                }
            }
        };
        for (String path : list) {
            if (getStat(path) != null) {
                getStat(path, watcher);
            } else {
                latch.countDown();
                if (latch.getCount() == 0) {
                    eventListener.run();
                }
            }
        }
    }

    @Override
    public String createNode(String path, byte[] data, CreateMode mode, boolean createParentsIfNeeded) throws Exception {
        String res = path;
        if (!CreateMode.EPHEMERAL_SEQUENTIAL.equals(path) && !CreateMode.PERSISTENT_SEQUENTIAL.equals(mode)) {
            Stat stat = client.checkExists().forPath(path);
            if (stat != null) {
                setData(path).setBytes(data).set();
                return res;
            }
        }
        if (createParentsIfNeeded) {
            res = client.create().creatingParentsIfNeeded().withMode(mode).forPath(path, data);
        } else {
            res = client.create().withMode(mode).forPath(path, data);
        }
        return res;
    }

    @Override
    public <T> String createNode(String path, T data, CreateMode mode, boolean createParentsIfNeeded) throws Exception {
        return createNode(path, serializer.getBytes(data), mode, createParentsIfNeeded);
    }

    @Override
    public SetDataBuilder setData(String path) throws Exception {
        return new SetDataBuilder(path,client,serializer);
    }

    @Override
    public void deleteNode(String path, boolean deleteChildrenIfNeeded) throws Exception {
        Stat stat = client.checkExists().forPath(path);
        if (stat == null) {
            return;
        }
        if (deleteChildrenIfNeeded) {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } else {
            client.delete().forPath(path);
        }
    }
    public static void main(String args[]){}
}
