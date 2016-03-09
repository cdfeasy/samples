package kafka.client.impl;

import kafka.client.common.KafkaBatchListener;
import kafka.client.common.KafkaClient;
import kafka.client.common.KafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by dmitry on 06.03.2016.
 */
public class ListenerProcessor<T> implements Runnable {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<KafkaListener<T>> listeners;
    private List<KafkaBatchListener<T>> batchListeners;
    private KafkaClient<T> client;

    public ListenerProcessor(List<KafkaListener<T>> listeners, List<KafkaBatchListener<T>> batchListeners, KafkaClient client) {
        this.listeners = listeners;
        this.batchListeners = batchListeners;
        this.client = client;
    }

    @Override
    public void run() {
        while (listeners.size() > 0 || batchListeners.size() > 0) {
            try {
                for (KafkaListener<T> listener : listeners) {
                    T t = client.receive();
                    if (t != null) {
                        try {
                            listener.onMessage(t);
                        } catch (Exception ex) {
                            logger.error(String.format("Listener %s error", listener.getClass(), ex));
                        }
                    }
                }
                for (KafkaBatchListener<T> listener : batchListeners) {
                    List<T> t = client.receive(listener.getBatchSize());
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
