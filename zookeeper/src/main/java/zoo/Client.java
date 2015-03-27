package zoo;


import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;

import java.util.List;

/**
 * Created by d.asadullin on 16.03.2015.
 */
public class Client {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(100, 1);
      //  final CuratorFramework client = CuratorFrameworkFactory.newClient("172.27.14.10:2181", retryPolicy);
        final CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2182", retryPolicy);
        client.start();
        System.out.println(client.getChildren().forPath("/"));
        CuratorWatcher watcher=new CuratorWatcher() {
            @Override
            public void process(WatchedEvent watchedEvent) throws Exception {
                System.out.println("changed"+watchedEvent);
                client.getChildren().usingWatcher(this).forPath(watchedEvent.getPath());
            }
        };
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/my/node");
        client.getChildren().usingWatcher(watcher).forPath("/my/node");
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/my/node/a");
        String s=client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/my/node/b");
        System.out.println("node"+s);
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println("created"+curatorFramework);
                System.out.println("created"+curatorEvent);
            }
        }).forPath("/my/node/c");
        List<String> lst=client.getChildren().forPath("/my/node");
        System.out.println(lst);
        String path=lst.get(1);
        client.getChildren().usingWatcher(new CuratorWatcher() {
            @Override
            public void process(WatchedEvent watchedEvent) throws Exception {
                System.out.println("eph:"+watchedEvent);
            }
        }).forPath("/my/node/"+path);
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
