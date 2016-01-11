package com.ifree.zoo.client;

import com.ifree.zoo.listener.ZooListener;
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
    void addListener(String path, ZooListener listener);
    void removeListener(ZooListener listener);
    List<String> getChildren(String path) throws Exception;
    ZooSerializer getSerializer();
    byte[] getData(String path) throws Exception;
    <T>T getObject(String path,Class<T> toClass) throws Exception;
    Stat getStat(String path) throws Exception;
    String createNode(String path,byte[] data,CreateMode mode,boolean createParentsIfNeeded) throws Exception;
    <T>String createNode(String path,T data,CreateMode mode,boolean createParentsIfNeeded) throws Exception;
    void setData(String path,byte[] data) throws Exception;
    <T>void setObject(String path, T data) throws Exception;
    void deleteNode(String path,boolean deleteChildrenIfNeeded) throws Exception;
}
