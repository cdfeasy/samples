package kafka.client.common;

import org.apache.kafka.clients.producer.Callback;

import java.util.List;

/**
 * Created by d.asadullin on 26.02.2016.
 */
public interface KafkaClient<K, V> extends AutoCloseable  {
    public static enum Mode{
        Consumer,
        Producer,
        All
    }
    void send(V object);
    void send(List<V> objects) throws Exception;
    void send(V object, Callback callback);
    void send(K key, V object, Callback callback);
    void sendBatch(List<KafkaEntry<K, V>> objects);
    V receive() throws InterruptedException;
    KafkaEntry<K,V> receiveEntry() throws InterruptedException;
    List<V> receive(int count);
    List<KafkaEntry<K,V>> receiveEntries(int count);
    void addListener(KafkaListener<K,V> listener);
  //  void addExceptionMessageListener(KafkaListener<K,V> listener);
    void addListener(KafkaBatchListener<K,V> listener);
    void removeListener(KafkaListener<K,V> listener);
    void removeListener(KafkaBatchListener<K,V> listener);
    void start() throws Exception;
    void close() throws Exception;
}
