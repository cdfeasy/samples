package kafka.client.impl;

import kafka.client.common.*;
import org.apache.kafka.clients.producer.Callback;

import java.util.List;

/**
 * Created by d.asadullin on 27.09.2016.
 */
public class KafkaTopicClient<K, V> implements KafkaConsumerClient<K, V>, KafkaProducerClient<K, V> {
    private KafkaConsumerClient<K, V> consumer;
    private KafkaProducerClient<K, V> producer;

    public KafkaTopicClient(KafkaConsumerClient<K, V> consumer, KafkaProducerClient<K, V> producer) {
        this.consumer = consumer;
        this.producer = producer;
    }

    @Override
    public V receive() throws InterruptedException {
        return consumer.receive();
    }

    @Override
    public KafkaEntry<K, V> receiveEntry() throws InterruptedException {
        return consumer.receiveEntry();
    }

    @Override
    public List<V> receive(int count) {
        return consumer.receive(count);
    }

    @Override
    public List<KafkaEntry<K, V>> receiveEntries(int count) {
        return consumer.receiveEntries(count);
    }

    @Override
    public void addListener(KafkaListener<K, V> listener) {
        consumer.addListener(listener);
    }

    @Override
    public void addListener(KafkaBatchListener<K, V> listener) {
        consumer.addListener(listener);
    }

    @Override
    public void removeListener(KafkaListener<K, V> listener) {
        consumer.removeListener(listener);
    }

    @Override
    public void removeListener(KafkaBatchListener<K, V> listener) {
        consumer.removeListener(listener);
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void close() throws Exception {

    }

    public void send(V object) throws Exception {
        producer.send(object);
    }

    public void send(List<V> objects) throws Exception {
        producer.send(objects);
    }

    public void send(V object, Callback callback) {
        producer.send(object, callback);
    }

    public void send(K key, V object, Callback callback) {
        producer.send(key, object, callback);
    }

    public void sendBatch(List<KafkaEntry<K, V>> objects) {
        producer.sendBatch(objects);
    }
}
