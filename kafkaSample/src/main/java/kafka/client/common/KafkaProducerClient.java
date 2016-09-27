package kafka.client.common;

import org.apache.kafka.clients.producer.Callback;

import java.util.List;

/**
 * Created by d.asadullin on 27.09.2016.
 */
public interface KafkaProducerClient<K,V> extends AutoCloseable {
    void send(V object) throws Exception;
    void send(List<V> objects) throws Exception;
    void send(V object, Callback callback);
    void send(K key, V object, Callback callback);
    void sendBatch(List<KafkaEntry<K, V>> objects);
}
