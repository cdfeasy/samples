package kafka.v1;

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
 * Created by d.asadullin on 31.08.2015.
 */
public class KafkaProducer {
    Producer<String, String> producer;
    private AtomicBoolean isStarted = new AtomicBoolean(true);
    private AtomicLong send;
    private AtomicLong received;

    public KafkaProducer(AtomicLong send) {
        Properties props = new Properties();

        props.put("metadata.broker.list", "172.27.14.10:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        // props.put("partitioner.class", "example.producer.SimplePartitioner");
        props.put("request.required.acks", "0");
        props.put("auto.create.topics.enable", "true");

        ProducerConfig config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);
        this.send=send;
    }

    public void sendMessage(String topic,String key, String message){
        send.incrementAndGet();
        KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, key,message);
        producer.send(data);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        KafkaProducer sampleProducer=new KafkaProducer(new AtomicLong(0));
        sampleProducer.sendMessage("mass223", "aaa", "blabla");
        sampleProducer.stop();
    }

    public void stop() {
        producer.close();
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
