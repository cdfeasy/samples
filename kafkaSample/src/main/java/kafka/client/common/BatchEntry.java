package kafka.client.common;

import org.apache.kafka.clients.producer.Callback;

/**
 * Created by d.asadullin on 02.03.2016.
 */
public class BatchEntry<T> {
    private Object key;
    private T object;
    private String topic;
    private Callback callback;

    public BatchEntry(T object, Callback callback) {
        this.object = object;
        this.callback = callback;
    }
    public BatchEntry(Object key, T object,Callback callback) {
        this.key = key;
        this.object = object;
        this.callback = callback;
    }

    public BatchEntry(T object, String topic, Callback callback) {
        this.object = object;
        this.topic = topic;
        this.callback = callback;
    }

    public BatchEntry(Object key, T object, String topic, Callback callback) {
        this.key = key;
        this.object = object;
        this.topic = topic;
        this.callback = callback;
    }

    public Object getKey() {
        return key;
    }

    public T getObject() {
        return object;
    }

    public Callback getCallback() {
        return callback;
    }

    public String getTopic() {
        return topic;
    }
}
