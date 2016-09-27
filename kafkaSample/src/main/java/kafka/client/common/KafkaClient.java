package kafka.client.common;

import org.apache.kafka.clients.producer.Callback;

import java.util.List;

/**
 * Created by d.asadullin on 26.02.2016.
 */
public interface KafkaClient extends KafkaProducerClient,KafkaConsumerClient {
    public static enum Mode{
        Consumer,
        Producer,
        All
    }
    void start() throws Exception;
    void close() throws Exception;
}
