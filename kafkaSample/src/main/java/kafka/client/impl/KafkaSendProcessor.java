package kafka.client.impl;

import kafka.client.common.KafkaConfigBuilder;
import kafka.client.common.KafkaEntry;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by d.asadullin on 03.03.2016.
 */
public class KafkaSendProcessor implements Runnable {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private KafkaProducer<byte[], byte[]> producer;
    private LinkedBlockingQueue<KafkaEntry<byte[], byte[]>> send;
    private String defaultTopic;

    public KafkaSendProcessor(KafkaProducer<byte[], byte[]> producer, LinkedBlockingQueue<KafkaEntry<byte[], byte[]>> send, KafkaConfigBuilder configBuilder) {
        this.producer = producer;
        this.send = send;
        this.defaultTopic = configBuilder.getTopic();
    }

    @Override
    public void run() {
        KafkaEntry<byte[], byte[]> entry;
        int i = 0;
        while (true) {
            try {
                entry = send.take();
                i++;
                try {
                    String topic = entry.getTopic() != null ? entry.getTopic() : defaultTopic;
                    producer.send(new ProducerRecord<byte[], byte[]>(topic, entry.getKey(), entry.getObject()), entry.getCallback());
                } catch (Exception ex) {
                    logger.error(String.format("Cannot send message %s", entry.getObject().toString()), ex);
                    entry.getCallback().onCompletion(null, ex);
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
