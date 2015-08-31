package kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by d.asadullin on 25.08.2015.
 */
public class SampleProducer {
    Producer<String, String> producer;
    private AtomicBoolean isStarted = new AtomicBoolean(true);
    private String topic;
    private AtomicLong send;
    private AtomicLong received;

    public SampleProducer(String topic,AtomicLong send) {
        Properties props = new Properties();

        props.put("metadata.broker.list", "172.27.14.10:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        // props.put("partitioner.class", "example.producer.SimplePartitioner");
        props.put("request.required.acks", "0");
        props.put("auto.create.topics.enable", "true");

        ProducerConfig config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);
        this.topic = topic;
        this.send=send;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        SampleProducer sampleProducer=new SampleProducer("mass",new AtomicLong(0));
        sampleProducer.start();
    }

    public void stop() {
        isStarted.set(false);
    }

    public void start() throws UnsupportedEncodingException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long offset = 0;
                int i=0;
                while (i<100000 && isStarted.get()) {
                    send.incrementAndGet();
                    String str = genString();
                    KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, str);
                    producer.send(data);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                    if(i%10000==0){
                        System.out.println(new Date().toString()+"  processed "+i);
                    }
                }
            }
        }).start();

    }

    public String genString() {
        Random r = new Random();
        StringBuilder bb = new StringBuilder();
        for (int i = 0; i < 998; i++) {

            bb.append((char) ('a' + r.nextInt(25)));
        }
        bb.append(r.nextInt(10)).append(r.nextInt(10));
        return bb.toString();
    }


}
