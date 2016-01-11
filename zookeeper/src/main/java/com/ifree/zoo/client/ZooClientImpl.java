package com.ifree.zoo.client;

import com.ifree.zoo.listener.*;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.*;

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


    public ZooClientImpl(String url, RetryPolicy retryPolicy, ListenerType listenerType, Integer listenerTime,ZooSerializer serializer) {
        this.url = url;
        this.retryPolicy = retryPolicy;
        this.listenerType = listenerType;
        this.listenerTime = listenerTime;
        client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
        if(ListenerType.RealTime.equals(listenerType)){
            processor=new RealTimeProcessor(client);
        }else{
            processor=new ScheduledProcessor(this,listenerTime);
        }
        this.serializer=serializer;
    }

    @Override
    public void start() throws Exception {
        client.start();
        processor.start();
    }

    @Override
    public void stop() throws IOException {
        client.close();
        processor.stop();
    }

    @Override
    public void registerListener(String path, ZooListener listener) {
        processor.registerListener(path,listener);
    }

    @Override
    public void removeListener(ZooListener listener) {
        processor.removeListener(listener);
    }

    @Override
    public List<String> getChildren(String path) throws Exception {
        List<String> strings = client.getChildren().forPath(path);
        return strings;
    }

    @Override
    public byte[] getData(String path) throws Exception {
        Stat stat = client.checkExists().forPath(path);
        if(stat==null){
            return null;
        }
        return client.getData().forPath(path);
    }

    @Override
    public <T> T getObject(String path, Class<T> toClass) throws Exception {
        byte[] bytes=getData(path);
        if(bytes==null){
            return null;
        }
        return serializer.fromBytes(bytes,toClass);
    }

    @Override
    public Stat getStat(String path) throws Exception {
        return  client.checkExists().forPath(path);
    }

    @Override
    public void createNode(String path, byte[] data, CreateMode mode, boolean createParentsIfNeeded) throws Exception {
        Stat stat = client.checkExists().forPath(path);
        if(stat!=null){
            setData(path,data);
            return;
        }
        if(createParentsIfNeeded) {
            client.create().creatingParentsIfNeeded().withMode(mode).forPath(path,data);
        } else{
            client.create().withMode(mode).forPath(path,data);
        }
    }

    @Override
    public <T> void createNode(String path, T data, CreateMode mode, boolean createParentsIfNeeded) throws Exception {
        createNode(path,serializer.getBytes(data),mode,createParentsIfNeeded);
    }

    @Override
    public void setData(String path, byte[] data) throws Exception {
        client.setData().forPath(path, data);
    }

    @Override
    public <T> void setObject(String path, T data) throws Exception {
        setData(path, serializer.getBytes(data));
    }

    @Override
    public void deleteNode(String path, boolean deleteChildrenIfNeeded) throws Exception {
        if(deleteChildrenIfNeeded) {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } else {
            client.delete().forPath(path);
        }
    }
}
