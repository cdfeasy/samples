package ru.cdf.zoo.client;

import org.apache.zookeeper.CreateMode;
import ru.cdf.zoo.listener.ZooListener;

import java.io.IOException;
import java.util.List;

/**
 * Created by d.asadullin on 23.11.2015.
 */
public interface ZooClient {
    void start() throws Exception;
    void stop() throws IOException;
    void registerListener(String path,ZooListener listener);
    void removeListener(ZooListener listener);
    List<String> getChildren(String path) throws Exception;
    byte[] getData(String path) throws Exception;
    void createNode(String path,byte[] data,CreateMode mode,boolean createParentsIfNeeded) throws Exception;
    void setData(String path,byte[] data) throws Exception;
    void deleteNode(String path,boolean deleteChildrenIfNeeded) throws Exception;
}
