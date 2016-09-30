package kafka.client.common;

/**
 * Created by d.asadullin on 29.09.2016.
 */
public interface KafkaClient<K,V> extends KafkaConsumerClient<K,V>,KafkaProducerClient<K,V> {
}
