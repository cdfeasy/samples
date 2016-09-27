package kafka.client.impl;

import kafka.client.Serializer;
import kafka.client.common.KafkaBatchListener;
import kafka.client.common.KafkaConfigBuilder;
import kafka.client.common.KafkaEntry;
import kafka.client.common.KafkaListener;
import kafka.client.impl.callback.NoSendCallback;
import kafka.client.serializer.BasicSerializer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by d.asadullin on 02.03.2016.
 */
public class KafkaClientImpl {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private KafkaProducer<byte[], byte[]> producer;
    private LinkedBlockingQueue<KafkaEntry<byte[], byte[]>> send;
    private ScheduledExecutorService executorService;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private KafkaSendProcessor sendProcessor;
    private ExecutorService onMessageExecutor;
    private ConcurrentHashMap<String, TopicReceiver> topicMap = new ConcurrentHashMap<>();

    private static Class<?> getClass(Class type) {
        Type[] genericInterfaces = type.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericInterface;
                Type[] actualTypeArguments = pt.getActualTypeArguments();

                for (Type actualTypeArgument : actualTypeArguments) {
                    if (actualTypeArgument instanceof Class) {
                        return (Class) actualTypeArgument;
                    }
                }
            }
        }
        return null;
    }

    public KafkaClientImpl(KafkaConfigBuilder configBuilder) {
        send = new LinkedBlockingQueue<>();
        executorService = Executors.newScheduledThreadPool(4, new ThreadFactory() {
            AtomicInteger count = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "" + configBuilder.getTopic() + "KafkaWorker" + count.incrementAndGet());
            }
        });
        Properties props = new Properties();
        if (configBuilder.getProperties() != null) {
            props.putAll(configBuilder.getProperties());
        }
        onMessageExecutor = Executors.newFixedThreadPool(2);
        props.put("bootstrap.servers", configBuilder.getServers());
        props.put("acks", "all");
        props.put("retries", 1);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        producer = new KafkaProducer<byte[], byte[]>(props);
        if (configBuilder.getKeySerializer() != null) {
            configBuilder.getKeySerializer().configure(props, true);
        } else {
            configBuilder.setKeyDeserializer(new BasicSerializer());
        }
        if (configBuilder.getValueSerializer() != null) {
            configBuilder.getValueSerializer().configure(props, false);
        } else {
            configBuilder.setKeyDeserializer(new BasicSerializer());
        }
        sendProcessor = new KafkaSendProcessor(producer, send, configBuilder);
    }

    public KafkaTopicClient getClient(Class clazz,Serializer keySerializer,Serializer valueSerializer){

    }



    public void start() throws Exception {
        isRunning.set(true);
        executorService.submit(sendProcessor);
        executorService.scheduleWithFixedDelay(receiveProcessor, 10, 100, TimeUnit.MILLISECONDS);
        executorService.scheduleWithFixedDelay(new ListenerProcessor<>(listeners, batchListeners, this), 10, 100, TimeUnit.MILLISECONDS);
    }

    public void close() throws Exception {
        try {
            isRunning.set(false);
            executorService.shutdown();

            if (!executorService.awaitTermination(15, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } finally {
            if (producer != null) {
                producer.close(5, TimeUnit.SECONDS);
            }
        }
    }
}
