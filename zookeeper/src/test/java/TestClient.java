import org.apache.zookeeper.CreateMode;
import org.junit.Test;
import ru.cdf.zoo.ZPath;
import ru.cdf.zoo.client.ZooClient;
import ru.cdf.zoo.ZooClientBuilder;
import ru.cdf.zoo.ZooEvent;
import ru.cdf.zoo.listener.ListenerType;
import ru.cdf.zoo.listener.ZooListener;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by d.asadullin on 24.11.2015.
 */
public class TestClient {
    @Test
    public void test() throws Exception {
        ZooClient client=new ZooClientBuilder().build();
        client.start();
        Thread.sleep(1000);
        client.createNode("/a/b", new byte[]{}, CreateMode.PERSISTENT, true);
        client.getData("/a/b");

        System.out.println("reg");
       //client.registerListener("/a/b",null);
        System.out.println("createNode");
        client.createNode("/a/b/c",new byte[]{}, CreateMode.PERSISTENT,true);
        client.getData("/a/b");
      //  Thread.sleep(1000);
        System.out.println("setData");
        client.registerListener("/a/b", new ZooListener() {
            @Override
            public boolean onDelete(String path, ZooEvent type) {
                System.out.println("!!!1");
                return false;
            }

            @Override
            public boolean onCreate(String path,byte[] data, ZooEvent type) {
                System.out.println("!!!2");
                return false;
            }

            @Override
            public boolean onChange(String path,byte[] data, ZooEvent type) {
                System.out.println("LIST:CHANGED"+"/"+path+"/"+type.name());
                return false;
            }
        });
        client.setData("/a/b", new byte[]{1});
        client.getData("/a/b");
       // Thread.sleep(1000);
        System.out.println("setData1");
        client.setData("/a/b/c", new byte[]{1, 2, 3});
        client.getData("/a/b");
        //Thread.sleep(1000);
        System.out.println("deleteNode");
        ZPath path=ZPath.getZPath(client,"/",null);
        System.out.println(path.toTree());
        System.out.println(path.findZpath("/a/b/c"));




        client.deleteNode("/a/b/c",true);

        Thread.sleep(3000);
        client.stop();
    }

    @Test
    public void test1() throws Exception {
        ZooClient client=new ZooClientBuilder().build();
        client.start();
        client.createNode("/a/b", new byte[]{}, CreateMode.PERSISTENT, true);
        System.out.println(client.getStat("/a"));
        Thread.sleep(1000);
        System.out.println(client.getStat("/c/d"));
        client.stop();

    }

    @Test
    public void test2() throws Exception {
        ZooClient client=new ZooClientBuilder().setListenerTime(10).setListenerType(ListenerType.Scheduled).build();
        client.start();
        TimeUnit.SECONDS.sleep(1);
        client.registerListener("/a",new TestListener());
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
        client.setData("/a","blabla".getBytes());
        TimeUnit.SECONDS.sleep(20);
//        ZPath pp=ZPath.getZPath(client,"/a",null);
//        for(Map.Entry<String,ZPath> pc:ZPath.toFlatMap(pp).entrySet()) {
//            System.out.println(pc.getKey()+"/"+pc.getValue().getFullPath());
//        }
        client.stop();

    }
}
