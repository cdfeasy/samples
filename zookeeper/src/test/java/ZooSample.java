import com.ifree.zoo.ZooClientBuilder;
import com.ifree.zoo.ZooEvent;
import com.ifree.zoo.client.ZooClient;
import org.apache.zookeeper.CreateMode;
import org.junit.Ignore;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * Created by d.asadullin on 11.01.2016.
 */
public class ZooSample {
    public static String getStackTrace(final Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String err= sw.toString();
    //    if(err.length()>200)err=err.substring(0,200);
        return err;
    }
    @Test
    public void test1() throws Exception {
        TimeZone tz=TimeZone.getTimeZone("GMT+13");
      //  Calendar calendar=Calendar.getInstance();
       // calendar.setTimeZone(tz);
        System.out.println(tz.getID());


    }
    @Test
    @Ignore
    public void test() throws Exception {
        Map<String,String> mp=new HashMap<>();
        mp.put("key1","value1");
        mp.put("key2","value2");
        ZooClient client=new ZooClientBuilder().setHost("vm-debian-40.i-free.dev").build();
        client.start();
        client.createNode("/b/c/d",new byte[]{}, CreateMode.PERSISTENT,true);
        client.setData("/b/c/d").setObject(mp).set();
        Thread.sleep(1000);
        client.addListener("/b/c/d", new TestListener() {
            @Override
            public boolean onChange(String path, byte[] data, ZooEvent type) {
                try {
                    System.out.println(type.name() + "/" + client.getSerializer().fromBytes(data, mp.getClass()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        Thread.sleep(100000);
    }
}
