package kafka;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by d.asadullin on 26.08.2015.
 */
public class Application {
    public static void main(String[] args) throws Exception {
        AtomicLong send=new AtomicLong(0);
        AtomicLong received=new AtomicLong(0);
        RouterNonFetch router=new RouterNonFetch("mass",send,received);

        router.start();
        for(int i=0;i<100;i++){
            String num=String.valueOf(i);
            if(num.length()==1) num="0"+num;
            num="test"+num;
            SampleConsumer sampleConsumer=new SampleConsumer(num,received);
          //sampleConsumer.start();
        }
//
//       SampleProducer sampleProducer=new SampleProducer("mass",send);
   //    sampleProducer.start();
       Thread.sleep(1000000);
     //   SampleProducer.

    }
}
