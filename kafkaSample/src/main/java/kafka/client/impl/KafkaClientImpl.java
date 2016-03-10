package kafka.client.impl;

import kafka.client.common.*;
import kafka.client.serializer.BasicSerializer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by d.asadullin on 02.03.2016.
 */
public class KafkaClientImpl<K, V> implements KafkaClient<K,V> {
    private KafkaProducer<byte[], byte[]> producer;
    private KafkaConsumer<byte[], byte[]> consumer;
    private LinkedBlockingQueue<KafkaEntry<K,V>> send;
    private List<KafkaListener<K,V>> listeners;
    private List<KafkaBatchListener<K,V>> batchListeners;
    private ScheduledExecutorService executorService;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private KafkaReceiveProcessor<K,V> receiveProcessor;
    private KafkaSendProcessor<K,V> sendProcessor;
   // private KafkaListener<byte[]> exceptionListener;

    private static Class<?> getClass(Class type)
    {
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
    public KafkaClientImpl(KafkaConfigBuilder<K,V> configBuilder) {
        send = new LinkedBlockingQueue<>();
        listeners = new CopyOnWriteArrayList<>();
        batchListeners = new CopyOnWriteArrayList<>();
        executorService = Executors.newScheduledThreadPool(4, new ThreadFactory() {
            AtomicInteger count = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "" + configBuilder.getTopic() + "KafkaWorker" + count.incrementAndGet());
            }
        });
        Properties props = new Properties();
        if(configBuilder.getProperties()!=null) {
            props.putAll(configBuilder.getProperties());
        }
        if (Mode.Producer.equals(configBuilder.getMode()) || Mode.All.equals(configBuilder.getMode())) {
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

            sendProcessor = new KafkaSendProcessor<>(producer, send, configBuilder);
        }
        if (Mode.Consumer.equals(configBuilder.getMode()) || Mode.All.equals(configBuilder.getMode())) {
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
           // getClass(this.getClass());
         //   Type type = this.getClass().getTypeParameters()[0].getBounds()[0];
//            Class<K> key = (Class<K>) ((ParameterizedType) configBuilder.getClass()
//                    .getGenericSuperclass()).getActualTypeArguments()[0];
//            Class<V> value = (Class<V>) (Class<V>) ((ParameterizedType) configBuilder.getClass()
//                    .getGenericSuperclass()).getActualTypeArguments()[1];
            receiveProcessor = new KafkaReceiveProcessor<>(isRunning, consumer, configBuilder);
        }
    }

    @Override
    public void send(V object) {
        send.add(new KafkaEntry<K,V>(object, null));
    }

    @Override
    public void send(List<V> objects) throws Exception {
        send.addAll(objects.stream().map((a) -> new KafkaEntry<K,V>(a, null)).collect(Collectors.toList()));
    }

    @Override
    public void send(V object, Callback callback) {
        send.add(new KafkaEntry<K,V>(object, callback));
    }

    @Override
    public void send(K key, V object, Callback callback) {
        send.add(new KafkaEntry<K,V>(key, object, callback));
    }

    @Override
    public void sendBatch(List<KafkaEntry<K, V>> objects) {
        send.addAll(objects);
    }

    @Override
    public V receive() throws InterruptedException {

        KafkaEntry<K,V> t = receiveProcessor.receive();
        if(t!=null){
            return t.getObject();
        } else{
            return null;
        }
    }

    @Override
    public KafkaEntry<K, V> receiveEntry() throws InterruptedException {
        return null;
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
    public void addListener(KafkaBatchListener<K, V> listener) {
        batchListeners.add(listener);
    }

    @Override
    public void removeListener(KafkaListener<K, V> listener) {
        listeners.remove(listener);
    }

    @Override
    public void removeListener(KafkaBatchListener<K, V> listener) {
        batchListeners.remove(listener);
    }

    @Override
    public void start() throws Exception {
        isRunning.set(true);
        executorService.scheduleWithFixedDelay(sendProcessor, 10, 100, TimeUnit.MILLISECONDS);
        executorService.scheduleWithFixedDelay(receiveProcessor, 10, 100, TimeUnit.MILLISECONDS);
        executorService.scheduleWithFixedDelay(new ListenerProcessor<>(listeners, batchListeners, this), 10, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() throws Exception {
        try {
            isRunning.set(false);
            listeners.clear();
            batchListeners.clear();
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
