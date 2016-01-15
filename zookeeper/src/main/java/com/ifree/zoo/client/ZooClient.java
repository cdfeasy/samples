package com.ifree.zoo.client;

import com.ifree.zoo.listener.ZooListener;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * Created by d.asadullin on 23.11.2015.
 */
public interface ZooClient {
    void start() throws Exception;
    void stop() throws IOException;
    void addListener(String path, ZooListener listener, boolean checkChildren);
    void addListener(String path, ZooListener listener);
    void removeListener(ZooListener listener);

    GetChildrenBuilder getChildren(String path) throws Exception;
    GetResultBuilder getData(String path) throws Exception;

    Stat getStat(String path) throws Exception;
    Stat getStat(String path,CuratorWatcher listener) throws Exception;

    void awaitingDelete(List<String> list,Runnable eventListener) throws Exception;

    String createNode(String path,byte[] data,CreateMode mode,boolean createParentsIfNeeded) throws Exception;
    <T>String createNode(String path,T data,CreateMode mode,boolean createParentsIfNeeded) throws Exception;

    SetDataBuilder setData(String path) throws Exception;

    void deleteNode(String path,boolean deleteChildrenIfNeeded) throws Exception;

    ZooSerializer getSerializer();
    CuratorFramework getCuratorClient();
}
