package ru.cdf.zoo.client;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import ru.cdf.zoo.listener.ListenerProcessor;
import ru.cdf.zoo.listener.ListenerType;
import ru.cdf.zoo.listener.RealTimeProcessor;
import ru.cdf.zoo.listener.ZooListener;

import java.io.IOException;
import java.util.List;

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


    public ZooClientImpl(String url, RetryPolicy retryPolicy, ListenerType listenerType, Integer listenerTime) {
        this.url = url;
        this.retryPolicy = retryPolicy;
        this.listenerType = listenerType;
        this.listenerTime = listenerTime;
        client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
        if(ListenerType.RealTime.equals(listenerType)){
            processor=new RealTimeProcessor(client);
        }else{

        }
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

    }

    @Override
    public List<String> getChildren(String path) throws Exception {
        List<String> strings = client.getChildren().forPath(path);
        return strings;
    }

    @Override
    public byte[] getData(String path) throws Exception {
     //   Stat stat = client.checkExists().forPath(path);
     //   System.out.println(stat);
        return client.getData().forPath(path);
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
    public void setData(String path, byte[] data) throws Exception {
        client.setData().forPath(path, data);
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
