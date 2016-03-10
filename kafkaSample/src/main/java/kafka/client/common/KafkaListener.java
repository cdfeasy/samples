package kafka.client.common;

/**
 * Created by d.asadullin on 02.03.2016.
 */
public interface KafkaListener<K,T> {
    void onMessage(K key,T message);
}
