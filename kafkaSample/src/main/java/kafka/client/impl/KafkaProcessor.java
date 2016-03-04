package kafka.client.impl;

import kafka.client.common.BatchEntry;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by d.asadullin on 02.03.2016.
 */
public class KafkaProcessor<T> implements Runnable {
    private KafkaProducer<byte[],byte[]> producer;
    private KafkaConsumer<byte[],byte[]> consumer;
    private LinkedBlockingQueue<BatchEntry<T>> send;
    private LinkedBlockingQueue<T> receive;

    public KafkaProcessor(KafkaProducer<byte[], byte[]> producer, KafkaConsumer<byte[], byte[]> consumer, LinkedBlockingQueue<BatchEntry<T>> send, LinkedBlockingQueue<T> receive) {
        this.producer = producer;
        this.consumer = consumer;
        this.send = send;
        this.receive = receive;
    }

    @Override
    public void run() {

    }
}
