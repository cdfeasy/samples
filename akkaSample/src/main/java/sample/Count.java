package sample;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by d.asadullin on 27.03.2015.
 */
public class Count {
    private static String msisdn(long i){
        return "9"+Long.toString(10000000000l+i);

    }
    public static void main(String[] args) throws InterruptedException {
        Random random=new Random();
        HashMap map=new HashMap(100000000);
        for(int i=0;i<1000000;i++){
            String msisdn=msisdn(random.nextLong());
            while(map.containsKey(msisdn)){
                msisdn=msisdn(random.nextLong());
            }
            map.put(msisdn,"");
            if(i%10000==0){
                System.out.println(i);
            }
        }
        Object val=map.get(msisdn(random.nextLong()));
        Thread.sleep(1000000);
    }
}
