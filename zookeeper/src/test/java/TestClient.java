import org.apache.zookeeper.CreateMode;
import org.junit.Test;
import ru.cdf.zoo.client.ZooClient;
import ru.cdf.zoo.ZooClientBuilder;
import ru.cdf.zoo.ZooEvent;
import ru.cdf.zoo.listener.ZooListener;

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

        System.out.println("reg");
       //client.registerListener("/a/b",null);
        System.out.println("createNode");
        client.createNode("/a/b/c",new byte[]{}, CreateMode.PERSISTENT,true);
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
            public boolean onChildChanged(String path, byte[] data, ZooEvent type) {
                System.out.println("LIST:CHILD:CHANGED"+path+"/"+type.name());
                return false;
            }

            @Override
            public boolean onChange(String path,byte[] data, ZooEvent type) {
                System.out.println("LIST:CHANGED"+"/"+path+"/"+type.name());
                return false;
            }
        });
        client.setData("/a/b", new byte[]{1});
       // Thread.sleep(1000);
        System.out.println("setData1");
        client.setData("/a/b/c", new byte[]{1, 2, 3});
        //Thread.sleep(1000);
        System.out.println("deleteNode");
        client.deleteNode("/a/b/c",true);
        Thread.sleep(3000);
        client.stop();
    }
}
