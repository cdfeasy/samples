package kafka.client.common;

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
    void stop() throws Exception;

    <K, V> KafkaClient<K, V> getClient(KafkaClient.ClientConfig<K,V> config) throws Exception;
}