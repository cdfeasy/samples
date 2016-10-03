package kafka.client.impl;

import kafka.client.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by dmitry on 06.03.2016.
 */
public class ListenerProcessor<K,V> implements Runnable {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<KafkaListener<K,V>> listeners;
    private KafkaConsumerClient<K,V> client;

    public ListenerProcessor(List<KafkaListener<K,V>> listeners, KafkaConsumerClient client) {
        this.listeners = listeners;
        this.client = client;
    }

    @Override
    public void run() {
        while (listeners.size() > 0) {
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
            } catch (Exception ex) {

            }
        }
    }
}
