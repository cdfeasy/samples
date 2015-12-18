package zoo;


import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by d.asadullin on 16.03.2015.
 */
public class Client {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(100, 1);
        //  final CuratorFramework client = CuratorFrameworkFactory.newClient("172.27.14.10:2181", retryPolicy);
        final CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
        client.start();
        final PathChildrenCache pathcache = new PathChildrenCache(client, "/", true);
        PathChildrenCacheListener listner1 = new PathChildrenCacheListener() {
            Map<String, PathChildrenCache> listener2s = new HashMap<String, PathChildrenCache>();
            AtomicBoolean isClosed = new AtomicBoolean(false);

            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                if (isClosed.get()) {
                    for (PathChildrenCache cache : listener2s.values()) {
                        cache.close();
                    }
                    pathcache.close();
                    return;
                }
                String path = event.getData().getPath();
                if (path.equals("/my/node")) {
                    System.out.println("????????????????????");
                    System.out.println("" + Thread.currentThread().getId() + "\t" + event);
                    pathcache.close();
                    for (PathChildrenCache cache : listener2s.values()) {
                        cache.close();
                    }
                    isClosed.set(true);
                    return;
                }
                if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                    PathChildrenCache cache2 = new PathChildrenCache(client, path, true);
                    cache2.getListenable().addListener(this);
                    cache2.start();
                    listener2s.put(path, cache2);
                } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                    PathChildrenCache cache2 = listener2s.remove(path);
                    if (cache2 != null) cache2.close();
                }
                System.out.println("--------------------------");
                System.out.println("" + Thread.currentThread().getId() + "\t" + event);
            }
        };

        pathcache.getListenable().addListener(listner1);
        pathcache.start();

        //   System.out.println(new String(client.getData().forPath("/my/node")));
        System.out.println(client.checkExists().forPath("/my/node"));
        System.out.println(client.getChildren().forPath("/"));

        // System.out.println(client.getData().forPath("/my/node"));
        CuratorWatcher watcher = new CuratorWatcher() {
            @Override
            public void process(WatchedEvent watchedEvent) throws Exception {
                System.out.println("!!!!!!!!!!!!!!!!!!");
                System.out.println("changed " + watchedEvent);
                client.getChildren().usingWatcher(this).forPath(watchedEvent.getPath());
            }
        };
        if (client.checkExists().forPath("/my/node") != null) {
            client.delete().deletingChildrenIfNeeded().forPath("/my/node");
        }

        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/my/node");

        System.out.println(client.checkExists().forPath("/my/node"));


        client.getChildren().usingWatcher(watcher).forPath("/my/node");
        client.setData().forPath("/my/node", "bla".getBytes());
        System.out.println(new String(client.getData().forPath("/my/node")));
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/my/node/a");
        String s = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/my/node/b");
        System.out.println("node" + s);
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println("created" + curatorFramework);
                System.out.println("created" + curatorEvent);
            }
        }).forPath("/my/node/c");
        List<String> lst = client.getChildren().forPath("/my/node");
        System.out.println(lst);
        String path = lst.get(1);
        client.getChildren().usingWatcher(new CuratorWatcher() {
            @Override
            public void process(WatchedEvent watchedEvent) throws Exception {
                System.out.println("eph:" + watchedEvent);
            }
        }).forPath("/my/node/" + path);
        client.delete().deletingChildrenIfNeeded().forPath("/my/node");


//        client.getCuratorListenable().addListener(new CuratorListener() {
//            @Override
//            public void eventReceived(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
//                curatorEvent
//            }
//        });

//        client.create().creatingParentsIfNeeded().forPath("/my/path", "alive".getBytes());

        //  client.

        client.close();

    }

//    public static void main(String[] args) throws Exception {
//        String hostPort = "172.27.14.10:2181";
//        String znode = "/";
//
//        Executor ex=new Executor(hostPort, znode);
//
//        List<ACL> lst=new ArrayList<>();
//        lst.add(new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.ANYONE_ID_UNSAFE));
//        ex.getZk().create("/my/node", "bla".getBytes(), lst, CreateMode.EPHEMERAL);
//        System.out.println("----------------1");
//        Thread.sleep(100);
//        ex.getZk().exists("/my/node", new Watcher() {
//            @Override
//            public void process(WatchedEvent watchedEvent) {
//                System.out.println("444"+ watchedEvent+"/"+ watchedEvent.getPath());
//            }
//        });
//
//
//        ex.getZk().delete("/my/node",0);
//        System.out.println("----------------2");
//       // byte[] b= ex.getZk().getData("/my/node",false,null);
//      //  System.out.println("2222"+new String(b));
//        Thread.sleep(1000);
//
//        ex.getZk().close();
//
//    }
//
//
//    static public class Executor implements Watcher, Runnable {
//        ZooKeeper zk;
//
//        public ZooKeeper getZk() {
//            return zk;
//        }
//
//        public Executor(String hostPort, String znode) throws KeeperException, IOException {
//            zk = new ZooKeeper(hostPort, 3000, this);
//        }
//
//        public void run() {
//
//        }
//
//
//        @Override
//        public void process(WatchedEvent event) {
//            String path = event.getPath();
//            System.out.println("111"+event.getPath());
//            if (event.getType() == Event.EventType.None) {
//                // We are are being told that the state of the
//                // connection has changed
//                switch (event.getState()) {
//                    case SyncConnected:
//                        System.out.println(event);
//                        break;
//                    case Expired:
//                        System.out.println(event);
//                        break;
//                }
//            } else if(event.getType() == Event.EventType.NodeCreated){
//                try {
//                    System.out.println("created");
//                    byte[] b=zk.getData(path,false,null);
//                    System.out.println(new String(b));
//                } catch (KeeperException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }  else if(event.getType() == Event.EventType.NodeChildrenChanged){
//                try {   System.out.println("changed");
//                    byte[] b=zk.getData(path,false,null);
//                    System.out.println(new String(b));
//                } catch (KeeperException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
