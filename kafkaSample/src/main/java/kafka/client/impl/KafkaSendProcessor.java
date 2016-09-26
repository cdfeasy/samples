package kafka.client.impl;

import kafka.client.common.KafkaConfigBuilder;
import kafka.client.common.KafkaEntry;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by d.asadullin on 03.03.2016.
 */
public class KafkaSendProcessor<K, V> implements Runnable {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private KafkaProducer<byte[], byte[]> producer;
    private LinkedBlockingQueue<KafkaEntry<K, V>> send;
    private Serializer keySerializer;
    private Serializer valueSerializer;
    private String defaultTopic;

    public KafkaSendProcessor(KafkaProducer<byte[], byte[]> producer, LinkedBlockingQueue<KafkaEntry<K, V>> send, KafkaConfigBuilder configBuilder) {
        this.producer = producer;
        this.send = send;
        this.keySerializer = configBuilder.getKeySerializer();
        this.valueSerializer = configBuilder.getValueSerializer();
        this.defaultTopic = configBuilder.getTopic();
    }

    @Override
    public void run() {
        KafkaEntry<K, V> entry;
        int i = 0;
        while (true) {
            try {
                entry = send.take();
                i++;
                try {
                    String topic = entry.getTopic() != null ? entry.getTopic() : defaultTopic;
                    byte[] value = valueSerializer.serialize(topic, entry.getObject());
                    byte[] key = null;
                    if (entry.getKey() != null) {
                        key = keySerializer.serialize(topic, entry.getKey());
                    }
                    producer.send(new ProducerRecord<byte[], byte[]>(topic, key, value), entry.getCallback());
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
                if (i >= 100) {
                    producer.flush();
                    i = 0;
                }
            } catch (Exception ex) {
                //
            }
        }

    }
}
