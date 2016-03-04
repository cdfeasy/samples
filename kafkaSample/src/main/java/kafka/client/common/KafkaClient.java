package kafka.client.common;

import kafka.client.common.KafkaBatchListener;
import kafka.client.common.KafkaListener;
import org.apache.kafka.clients.producer.Callback;

import java.util.List;

/**
 * Created by d.asadullin on 26.02.2016.
 */
public interface KafkaClient<T> {
    public static enum Mode{
        Consumer,
        Producer,
        All
    }
    void send(T object);
    void send(List<T> objects);
    void send(T object, Callback callback);
    void sendBatch(List<BatchEntry<T>> objects);
    T receive() throws InterruptedException;
    List<T> receive(int count);
    void addListener(KafkaListener<T> listener);
    void addListener(KafkaBatchListener<T> listener);
    void removeListener(KafkaListener<T> listener);
    void removeListener(KafkaBatchListener<T> listener);
}
