import org.apache.zookeeper.CreateMode;
import org.junit.Test;
import zoo.ZooClient;
import zoo.ZooClientImpl;

/**
 * Created by d.asadullin on 24.11.2015.
 */
public class TestClient {
    @Test
    public void test() throws Exception {
        ZooClient client=new ZooClientImpl();
        client.start();
        Thread.sleep(1000);
        client.createNode("/a/b", new byte[]{}, CreateMode.PERSISTENT, true);

        System.out.println("reg");
       //client.registerListener("/a/b",null);
        System.out.println("createNode");
        client.createNode("/a/b/c",new byte[]{}, CreateMode.PERSISTENT,true);
      //  Thread.sleep(1000);
        System.out.println("setData");
        client.setData("/a/b", new byte[]{1});
       // Thread.sleep(1000);
        System.out.println("setData1");
        client.setData("/a/b/c", new byte[]{1, 2, 3});
        //Thread.sleep(1000);
        System.out.println("deleteNode");
        client.deleteNode("/a/b/c",true);
        Thread.sleep(1000);
        client.stop();
    }
}
