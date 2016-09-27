package kafka.client.common;

import org.apache.kafka.clients.producer.Callback;

import java.util.List;

/**
 * Created by d.asadullin on 27.09.2016.
 */
public interface KafkaConsumerClient<K, V>  extends AutoCloseable {
    V receive() throws InterruptedException;
    KafkaEntry<K, V> receiveEntry() throws InterruptedException;
    List<V> receive(int count);
    List<KafkaEntry<K, V>> receiveEntries(int count);
    void addListener(KafkaListener<K, V> listener);
    void addListener(KafkaBatchListener<K, V> listener);
    void removeListener(KafkaListener<K, V> listener);
    void removeListener(KafkaBatchListener<K, V> listener);
    void start() throws Exception;
    void close() throws Exception;
}