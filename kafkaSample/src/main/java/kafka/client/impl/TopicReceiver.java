package kafka.client.impl;

import kafka.client.common.*;
import kafka.client.serializer.BasicSerializer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by d.asadullin on 27.09.2016.
 */
public class TopicReceiver<K, V> implements KafkaConsumerClient<K, V> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private KafkaServiceImpl client;
    private KafkaConsumer<byte[], byte[]> consumer;
    private KafkaReceiveProcessor<K, V> receiveProcessor;
    private List<KafkaListener<K, V>> listeners=new ArrayList<>();
    private AtomicBoolean isRunning;
    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> scheduledFuture;

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

    public TopicReceiver(ScheduledExecutorService executorService,KafkaConfigBuilder<K, V> configBuilder, KafkaServiceImpl client, AtomicBoolean isRunning) {
        this.client = client;
        this.isRunning = isRunning;
        this.executorService=executorService;
        Properties props = new Properties();
        if (configBuilder.getProperties() != null) {
            props.putAll(configBuilder.getProperties());
        }

        props.put("group.id", configBuilder.getGroupId());
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        //   props.put("fetch.min.bytes", "500");
        // props.put("receive.buffer.bytes", "30000");
        //  props.put("max.partition.fetch.bytes", "3000");
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        if (configBuilder.getKeyDeserializer() != null) {
            configBuilder.getKeyDeserializer().configure(props, true);
        } else {
            configBuilder.setKeyDeserializer(new BasicSerializer());
        }
        if (configBuilder.getValueDeserializer() != null) {
            configBuilder.getValueDeserializer().configure(props, false);
        } else {
            configBuilder.setValueDeserializer(new BasicSerializer());
        }
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(configBuilder.getTopic()));
        receiveProcessor = new KafkaReceiveProcessor<>(isRunning, consumer, configBuilder);
    }

    @Override
    public V receive() throws InterruptedException {
        KafkaEntry<K, V> t = receiveProcessor.receive();
        if (t != null) {
            return t.getObject();
        } else {
            return null;
        }
    }

    @Override
    public KafkaEntry<K, V> receiveEntry() throws InterruptedException {
        List<KafkaEntry<K, V>> list= receiveProcessor.batchReceive(1);;
        return list.isEmpty()?null:list.get(0);
    }

    @Override
    public List<V> receive(int count) {
        List<V> result = receiveProcessor.batchReceiveValue(count);
        return result;
    }

    @Override
    public List<KafkaEntry<K, V>> receiveEntries(int count) {
        return receiveProcessor.batchReceive(count);
    }

    @Override
    public void addListener(KafkaListener<K, V> listener) {
        listeners.add(listener);
    }

//    @Override
//    public void addExceptionMessageListener(KafkaListener listener) {
//        this.exceptionListener=listener;
//    }

    @Override
    public void removeListener(KafkaListener<K, V> listener) {
        listeners.remove(listener);
    }


    @Override
    public void start() throws Exception {
        isRunning.set(true);
        scheduledFuture= executorService.scheduleWithFixedDelay(receiveProcessor, 10, 100, TimeUnit.MILLISECONDS);
        executorService.scheduleWithFixedDelay(new ListenerProcessor<>(listeners, this), 10, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() throws Exception {
        try {
            listeners.clear();
            scheduledFuture.cancel(false);
            receiveProcessor.commit();
        } finally {

        }
    }
}

