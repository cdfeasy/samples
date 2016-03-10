package kafka.client.common;

import java.util.List;

/**
 * Created by d.asadullin on 02.03.2016.
 */
public interface KafkaBatchListener<K,V> {
    int getBatchSize();
    void onMessages(List<KafkaEntry<K,V>> messages);
}
