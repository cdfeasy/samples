package zoo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by d.asadullin on 23.11.2015.
 */
public class ZooClientImpl implements ZooClient {
    CuratorFramework client;
    @Override
    public void start() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(100, 1);
        client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
        client.start();
        final PathChildrenCache pathcache = new PathChildrenCache(client, "/", true);
        PathChildrenCacheListener listner1 = new PathChildrenCacheListener() {
            Map<String, PathChildrenCache> listener2s = new HashMap<String, PathChildrenCache>();

            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                String path = event.getData().getPath();
                if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                    System.out.println("added "+path);
                    PathChildrenCache cache2 = new PathChildrenCache(client, path, true);
                    cache2.getListenable().addListener(this);
                    cache2.start();
                    listener2s.put(path, cache2);
                } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                    System.out.println("deleted "+path);
                    PathChildrenCache cache2 = listener2s.remove(path);
                    if (cache2 != null) cache2.close();
                } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED){
                    System.out.println("changed "+path);

                }
            }
        };
        pathcache.getListenable().addListener(listner1);
        pathcache.start();


    }

    @Override
    public void stop() {
        client.close();
    }

    @Override
    public void registerListener(String path, ZooListener listener) {
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent)  {
                System.out.println("bla");
                switch (watchedEvent.getType()){
                    case NodeCreated:System.out.println("cr:"+watchedEvent);break ;
                    case NodeDeleted:System.out.println("del:"+watchedEvent);break ;
                    case NodeDataChanged:System.out.println("change:"+watchedEvent);break ;
                    case NodeChildrenChanged:System.out.println("chChange:"+watchedEvent);break ;
                    default:break;
                   }

                try {
                    client.getChildren().usingWatcher(this).forPath(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            client.getChildren().usingWatcher(watcher).forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeListener(ZooListener listener) {

    }

    @Override
    public String[] getChildren(String path) {
        return new String[0];
    }

    @Override
    public byte[] getData(String path) throws Exception {
        return client.getData().forPath(path);
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
