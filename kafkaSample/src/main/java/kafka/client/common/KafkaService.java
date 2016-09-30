package kafka.client.common;

import org.apache.kafka.clients.producer.Callback;

import java.util.List;

/**
 * Created by d.asadullin on 26.02.2016.
 */
public interface KafkaService  {
    public static enum Mode {
        Consumer,
        Producer,
        All
    }
    void start() throws Exception;
    void close() throws Exception;
    <K, V> ClientBuilder<K, V> getBuilder(String topic, Class<K> keyClazz, Class<V> valueClazz) throws Exception;
    ClientBuilder<byte[], byte[]> getBuilder(String topic) throws Exception;
}