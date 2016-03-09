package kafka.client.common;

/**
 * Created by d.asadullin on 02.03.2016.
 */
public interface KafkaListener<T> {
    void onMessage(Object key,T message);
}
