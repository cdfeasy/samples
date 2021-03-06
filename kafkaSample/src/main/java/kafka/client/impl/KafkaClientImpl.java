package kafka.client.impl;

import kafka.client.common.*;
import org.apache.kafka.clients.producer.Callback;

import java.util.List;

/**
 * Created by d.asadullin on 27.09.2016.
 */
public class KafkaClientImpl<K, V> implements KafkaClient<K,V> {
    private KafkaConsumerClient<K, V> consumer;
    private KafkaProducerClient<K, V> producer;
    private String topic;
    private Class<K> keyClass;
    private Class<V> valueClass;

    public KafkaClientImpl(KafkaConsumerClient<K, V> consumer, KafkaProducerClient<K, V> producer,String topic,Class<K> keyClass,Class<V> valueClass) {
        this.consumer = consumer;
        this.producer = producer;
        this.topic = topic;
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    @Override
    public V receive() throws Exception {
        if(consumer==null){
            throw new Exception("Client created only for send");
        }
        return consumer.receive();
    }

    @Override
    public KafkaEntry<K, V> receiveEntry() throws Exception {
        return consumer.receiveEntry();
    }

    @Override
    public List<V> receive(int count) throws Exception {
        return consumer.receive(count);
    }

    @Override
    public List<KafkaEntry<K, V>> receiveEntries(int count) throws Exception  {
        return consumer.receiveEntries(count);
    }

    @Override
    public void addListener(KafkaListener<K, V> listener) {
        consumer.addListener(listener);
    }

    @Override
    public void removeListener(KafkaListener<K, V> listener) {
        consumer.removeListener(listener);
    }


    @Override
    public void start() throws Exception {

    }

    @Override
    public void close() throws Exception {
        if(consumer!=null){
            consumer.close();
        }
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public Class<K> getKeyClass() {
        return keyClass;
    }

    @Override
    public Class<V> getValueClass() {
        return valueClass;
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
