import com.ifree.zoo.listener.ZooListenerWrapper;
import org.apache.zookeeper.CreateMode;
import org.junit.Assert;
import org.junit.Test;
import com.ifree.zoo.Server;
import com.ifree.zoo.ZPath;
import com.ifree.zoo.client.ZooClient;
import com.ifree.zoo.ZooClientBuilder;
import com.ifree.zoo.ZooEvent;
import com.ifree.zoo.listener.ListenerType;
import com.ifree.zoo.listener.ZooListener;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by d.asadullin on 24.11.2015.
 */
public class TestClient {
    @Test
    public void test() throws Exception {
        Server server=new Server();
        server.start();
        ZooClient client=new ZooClientBuilder().build();
        client.start();
        Thread.sleep(1000);
        client.createNode("/a/b", new byte[]{}, CreateMode.PERSISTENT, true);
        client.getData("/a/b").getBytes();

        System.out.println("reg");
       //client.addListener("/a/b",null);
        System.out.println("createNode");
        client.createNode("/a/b/c",new byte[]{}, CreateMode.PERSISTENT,true);
        client.getData("/a/b").getBytes();
      //  Thread.sleep(1000);
        System.out.println("setData");
        client.addListener("/a/b", new ZooListener() {
            @Override
            public boolean onDelete(String path, ZooEvent type) {
                System.out.println("!!!1");
                return false;
            }

            @Override
            public boolean onChange(String path, byte[] data, ZooEvent type) {
                System.out.println("LIST:CHANGED" + "/" + path + "/" + type.name());
                return false;
            }
        });
        client.setData("/a/b").setBytes(new byte[]{1}).set();
        client.getData("/a/b");
       // Thread.sleep(1000);
        System.out.println("setData1");
        client.setData("/a/b/c").setBytes( new byte[]{1, 2, 3}).set();
        client.getData("/a/b");
        //Thread.sleep(1000);
        System.out.println("deleteNode");
        ZPath path=ZPath.getZPath(client,"/",null);
        System.out.println(path.toTree());
        System.out.println(path.findZpath("/a/b/c"));




        client.deleteNode("/a/b/c",true);

        Thread.sleep(3000);
        client.stop();
        server.stop();
    }

    @Test
    public void test1() throws Exception {
        Server server=new Server();
        server.start();
        ZooClient client=new ZooClientBuilder().build();
        client.start();
        System.out.println(client.createNode("/a/s", new byte[]{}, CreateMode.EPHEMERAL, true));
        System.out.println(client.createNode("/a/s", new byte[]{}, CreateMode.EPHEMERAL, true));
        System.out.println(client.createNode("/a/e", new byte[]{}, CreateMode.PERSISTENT_SEQUENTIAL, true));
        System.out.println(client.createNode("/a/e", new byte[]{}, CreateMode.PERSISTENT_SEQUENTIAL, true));
        System.out.println(client.createNode("/a/e", new byte[]{}, CreateMode.PERSISTENT_SEQUENTIAL, true));
        Thread.sleep(1000);
        ((UnaryOperator<Integer>)(a)->++a).apply(2015);
        client.stop();
        server.stop();

    }

    @Test
    public void testString() throws Exception {
        Server server=new Server();
        server.start();
        Map<String,String> mp=new HashMap<>();
        mp.put("sfdg,df","sfdgfdg==");
        mp.put("sdfgdfd232","sd33=-==1,");
        ZooClient client=new ZooClientBuilder().build();
        client.start();
        client.createNode("/b/c/d",new byte[]{},CreateMode.PERSISTENT,true);
        client.setData("/b/c/d").setObject(mp).set();
        Map newMap=client.getData("/b/c/d").setToClass(mp.getClass()).getObject();
        System.out.println(newMap);
        server.stop();
      //  System.out.println(s.next());

    }

    @Test
    public void test2() throws Exception {
        Server server=new Server();
        server.start();
        ZooClient client=new ZooClientBuilder().setListenerTime(10).setListenerType(ListenerType.Scheduled).build();
        client.start();
        System.out.println(client.getData("/b/cv/d"));
        TimeUnit.SECONDS.sleep(1);
        client.addListener("/a", new TestListener());
        client.createNode("/a/d",new byte[]{}, CreateMode.PERSISTENT,true);
        client.createNode("/a/d/a",new byte[]{}, CreateMode.PERSISTENT,true);
        client.createNode("/a/d/a1",new byte[]{}, CreateMode.PERSISTENT,true);
        client.createNode("/a/d/a2",new byte[]{}, CreateMode.PERSISTENT,true);
        client.createNode("/a/d/a3",new byte[]{}, CreateMode.PERSISTENT,true);
        client.createNode("/a/d/a4",new byte[]{}, CreateMode.PERSISTENT,true);
        client.createNode("/a/d/a5",new byte[]{}, CreateMode.PERSISTENT,true);
        client.createNode("/a/d/a5/b1",new byte[]{}, CreateMode.PERSISTENT,true);
        client.createNode("/a/d/a5/b2",new byte[]{}, CreateMode.PERSISTENT,true);
        client.createNode("/a/d/a5/b3",new byte[]{}, CreateMode.PERSISTENT,true);
        client.createNode("/a/d/a5/b4",new byte[]{}, CreateMode.PERSISTENT,true);

        TimeUnit.SECONDS.sleep(10);
        System.out.println("delete");
        client.deleteNode("/a/d/a5", true);
        client.setData("/a").setBytes("blabla".getBytes()).set();
        TimeUnit.SECONDS.sleep(20);
//        ZPath pp=ZPath.getZPath(client,"/a",null);
//        for(Map.Entry<String,ZPath> pc:ZPath.toFlatMap(pp).entrySet()) {
//            System.out.println(pc.getKey()+"/"+pc.getValue().getFullPath());
//        }
        client.stop();
        server.stop();

    }

    @Test
    public void test3() throws Exception {
        Server server=new Server();
        server.start();
        ZooClient client=new ZooClientBuilder().build();
        client.start();
        client.deleteNode("/a/d", true);
        client.getData("/a/d");
        TimeUnit.SECONDS.sleep(1);
        client.addListener("/a", new TestListener());
        System.out.println(client.createNode("/a/d/b5-", new byte[]{}, CreateMode.EPHEMERAL_SEQUENTIAL, true));
        client.getData("/a/d");
        System.out.println(client.createNode("/a/d/a-", new byte[]{}, CreateMode.EPHEMERAL_SEQUENTIAL, true));
        System.out.println(client.createNode("/a/d/a1-", new byte[]{}, CreateMode.EPHEMERAL_SEQUENTIAL, true));
        System.out.println(client.createNode("/a/d/a2-", new byte[]{}, CreateMode.EPHEMERAL_SEQUENTIAL, true));
        System.out.println(client.createNode("/a/d/a3-", new byte[]{}, CreateMode.EPHEMERAL_SEQUENTIAL, true));
        System.out.println(client.createNode("/a/d/a4-", new byte[]{}, CreateMode.EPHEMERAL_SEQUENTIAL, true));
        System.out.println(client.createNode("/a/d/a5-", new byte[]{}, CreateMode.EPHEMERAL_SEQUENTIAL, true));
        System.out.println(client.getChildren("/a/d").get());
        client.awaitingDelete(client.getChildren("/a/d").setPrefix("a").setFullPath(true).get(),()->System.out.println("/nDELETED!!!/n"));
        System.out.println(client.getChildren("/a/d").setComparator((a,b)->a.compareTo(b)).get());
        Thread.sleep(3000);
        System.out.println(client.getData("/a/d/a-"));
        System.out.println(client.getChildren("/a/d").setPrefix("a-").get());
        client.deleteNode("/a/d", true);
        client.awaitingDelete(Arrays.asList(new String[]{"/a/d/a111"}),()->System.out.println("/nDELETED1111!!!/n"));
        Thread.sleep(3000);
        client.stop();
        server.stop();

    }


    @Test
    public void testNonChildListener() throws Exception {
        Server server=new Server();
        server.start();
        ZooClient client=new ZooClientBuilder().build();
        client.start();
        try {
            client.deleteNode("/listener/check", true);
            client.createNode("/listener/check", new byte[]{}, CreateMode.PERSISTENT, true);
            Thread.sleep(2000);
            AtomicInteger i = new AtomicInteger(0);
            client.addListener("/listener/check", new ZooListener() {
                @Override
                public boolean onDelete(String path, ZooEvent type) {
                    return false;
                }

                @Override
                public boolean onChange(String path, byte[] data, ZooEvent type) {
                    i.incrementAndGet();
                    return false;
                }
            });
         //   Thread.sleep(1000);
            client.setData("/listener/check").setBytes(new byte[]{1}).set();
            Thread.sleep(1000);

            Assert.assertEquals(i.get(),1);
            client.createNode("/listener/check/node", new byte[]{1}, CreateMode.PERSISTENT, true);
            Thread.sleep(1000);
            Assert.assertEquals(i.get(),1);
        }finally {
            client.stop();
            server.stop();
        }
    }
    @Test
    public void testChildListener() throws Exception {
        Server server=new Server();
        server.start();
        ZooClient client=new ZooClientBuilder().build();
        client.start();
        try {
            client.deleteNode("/listener/check", true);
            client.createNode("/listener/check", new byte[]{}, CreateMode.PERSISTENT, true);
            Thread.sleep(2000);
            AtomicInteger i = new AtomicInteger(0);
            client.addListener("/listener/check", new ZooListener() {
                @Override
                public boolean onDelete(String path, ZooEvent type) {
                    return false;
                }

                @Override
                public boolean onChange(String path, byte[] data, ZooEvent type) {
                    i.incrementAndGet();
                    return false;
                }
            },true);
            //   Thread.sleep(1000);
            client.setData("/listener/check").setBytes(new byte[]{1}).set();
            Thread.sleep(1000);

            Assert.assertEquals(i.get(),1);
            client.createNode("/listener/check/node", new byte[]{1}, CreateMode.PERSISTENT, true);
            Thread.sleep(1000);
            Assert.assertEquals(i.get(),2);
        }finally {
            client.stop();
            server.stop();
        }
    }

}
