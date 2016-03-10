package kafka.client.impl;

import kafka.client.common.KafkaBatchListener;
import kafka.client.common.KafkaClient;
import kafka.client.common.KafkaEntry;
import kafka.client.common.KafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by dmitry on 06.03.2016.
 */
public class ListenerProcessor<K,V> implements Runnable {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<KafkaListener<K,V>> listeners;
    private List<KafkaBatchListener<K,V>> batchListeners;
    private KafkaClient<K,V> client;

    public ListenerProcessor(List<KafkaListener<K,V>> listeners, List<KafkaBatchListener<K,V>> batchListeners, KafkaClient client) {
        this.listeners = listeners;
        this.batchListeners = batchListeners;
        this.client = client;
    }

    @Override
    public void run() {
        while (listeners.size() > 0 || batchListeners.size() > 0) {
            try {
                for (KafkaListener<K,V> listener : listeners) {
                    KafkaEntry<K,V> t = client.receiveEntry();
                    if (t != null) {
                        try {
                            listener.onMessage(t.getKey(),t.getObject());
                        } catch (Exception ex) {
                            logger.error(String.format("Listener %s error", listener.getClass(), ex));
                        }
                    }
                }
                for (KafkaBatchListener<K,V> listener : batchListeners) {
                    List<KafkaEntry<K,V>> t = client.receiveEntries(listener.getBatchSize());
                    if (t != null && !t.isEmpty()) {
                        try {
                            listener.onMessages(t);
                        } catch (Exception ex) {
                            logger.error(String.format("Listener %s error", listener.getClass(), ex));
                        }
                    }
                }
            } catch (Exception ex) {

            }
        }
    }
}
