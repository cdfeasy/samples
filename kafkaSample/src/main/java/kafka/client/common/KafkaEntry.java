package kafka.client.common;

import org.apache.kafka.clients.producer.Callback;

/**
 * Created by d.asadullin on 02.03.2016.
 */
public class KafkaEntry<K,V> {
    private K key;
    private V object;
    private String topic;
    private Callback callback;

    public KafkaEntry(V object, Callback callback) {
        this.object = object;
        this.callback = callback;
    }

    public KafkaEntry(K key, V object, Callback callback) {
        this.key = key;
        this.object = object;
        this.callback = callback;
    }

    public KafkaEntry(K key, V object) {
        this.key = key;
        this.object = object;
    }

    public KafkaEntry(V object, String topic, Callback callback) {
        this.object = object;
        this.topic = topic;
        this.callback = callback;
    }

    public KafkaEntry(K key, V object, String topic, Callback callback) {
        this.key = key;
        this.object = object;
        this.topic = topic;
        this.callback = callback;
    }

    public K getKey() {
        return key;
    }

    public V getObject() {
        return object;
    }

    public Callback getCallback() {
        return callback;
    }

    public String getTopic() {
        return topic;
    }
}
