package kafka.client.impl;

import kafka.client.common.BatchEntry;
import kafka.client.common.KafkaConfigBuilder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by d.asadullin on 03.03.2016.
 */
public class KafkaSendProcessor<T> implements Runnable {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private KafkaProducer<byte[], byte[]> producer;
    private LinkedBlockingQueue<BatchEntry<T>> send;
    private Serializer keySerializer;
    private Serializer valueSerializer;
    private String defaultTopic;

    public KafkaSendProcessor(KafkaProducer<byte[], byte[]> producer, LinkedBlockingQueue<BatchEntry<T>> send, KafkaConfigBuilder configBuilder) {
        this.producer = producer;
        this.send = send;
        this.keySerializer = configBuilder.getKeySerializer();
        this.valueSerializer = configBuilder.getValueSerializer();
        this.defaultTopic = configBuilder.getTopic();
    }

    @Override
    public void run() {
        List<BatchEntry<T>> lst = new ArrayList<>();
        while (send.drainTo(lst, 100) != 0) {
            for (BatchEntry<T> entry : lst) {
                try {
                    String topic = entry.getTopic() != null ? entry.getTopic() : defaultTopic;
                    byte[] value = valueSerializer.serialize(topic, entry.getObject());
                    byte[] key = null;
                    if (entry.getKey() != null) {
                        key = keySerializer.serialize(topic, entry.getKey());
                    }
                    if (entry.getCallback() != null) {
                        producer.send(new ProducerRecord<byte[], byte[]>(topic, key, value), entry.getCallback());
                    } else {
                        producer.send(new ProducerRecord<byte[], byte[]>(topic, key, value));
                    }
                } catch (Exception ex) {
                    logger.error(String.format("Cannot send message %s", entry.getObject().toString()), ex);
                    if (entry.getCallback() != null) {
                        try {
                            entry.getCallback().onCompletion(null, ex);
                        } catch (Exception nope) {
                            //nope
                        }

                    }
                }
            }
        }
        producer.flush();
    }
}
