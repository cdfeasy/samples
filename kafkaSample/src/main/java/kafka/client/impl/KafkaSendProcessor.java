package kafka.client.impl;

import kafka.client.common.BatchEntry;
import kafka.client.serializer.GsonSerializer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by d.asadullin on 03.03.2016.
 */
public class KafkaSendProcessor<T> implements Runnable {
    private KafkaProducer<byte[],byte[]> producer;
    private LinkedBlockingQueue<BatchEntry<T>> send;
    private GsonSerializer serializer;
    private String defaultTopic;

    public KafkaSendProcessor(KafkaProducer<byte[], byte[]> producer, LinkedBlockingQueue<BatchEntry<T>> send, GsonSerializer serializer, String defaultTopic) {
        this.producer = producer;
        this.send = send;
        this.serializer = serializer;
        this.defaultTopic = defaultTopic;
    }

    @Override
    public void run() {
        List<BatchEntry<T>> lst=new ArrayList<>();
        send.drainTo(lst);
        for(BatchEntry<T> entry:lst) {
            byte[] value = serializer.getBytes(entry.getObject());
            if(entry.getCallback()!=null) {
                producer.send(new ProducerRecord<byte[], byte[]>(entry.getTopic(), value),entry.getCallback());
            }  else {
                producer.send(new ProducerRecord<byte[], byte[]>(entry.getTopic(), value));
            }
        }
        producer.flush();
    }
}
