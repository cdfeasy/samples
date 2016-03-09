package kafka.client.impl;

import kafka.client.common.*;
import kafka.client.serializer.BasicSerializer;
import kafka.client.serializer.GsonSerializer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;

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
public class KafkaClientImpl<T> implements KafkaClient<T> {
    private KafkaProducer<byte[], byte[]> producer;
    private KafkaConsumer<byte[], byte[]> consumer;
    private LinkedBlockingQueue<BatchEntry<T>> send;
    private List<KafkaListener<T>> listeners;
    private List<KafkaBatchListener<T>> batchListeners;
    private ScheduledExecutorService executorService;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private KafkaReceiveProcessor<T> receiveProcessor;
    private KafkaSendProcessor<T> sendProcessor;
    private KafkaListener<byte[]> exceptionListener;


    public KafkaClientImpl(KafkaConfigBuilder<T> configBuilder) {
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
            //   Class<T> c = (Class<T>) ((ParameterizedTyp) getClass()
            //             .getGenericSuperclass()).getActualTypeArguments()[0];
            receiveProcessor = new KafkaReceiveProcessor<>(isRunning, consumer, configBuilder, String.class);
        }
    }

    @Override
    public void send(T object) {
        send.add(new BatchEntry<T>(object, null));
    }

    @Override
    public void send(List<T> objects) throws Exception {
        send.addAll(objects.stream().map((a) -> new BatchEntry<T>(a, null)).collect(Collectors.toList()));
    }

    @Override
    public void send(T object, Callback callback) {
        send.add(new BatchEntry<T>(object, callback));
    }

    @Override
    public void send(Object key, T object, Callback callback) {
        send.add(new BatchEntry<T>(key, object, callback));
    }

    @Override
    public void sendBatch(List<BatchEntry<T>> objects) {
        send.addAll(objects);
    }

    @Override
    public T receive() throws InterruptedException {
        T t = receiveProcessor.receive();
        return t;
    }

    @Override
    public List<T> receive(int count) {
        List<T> result = receiveProcessor.batchReceive(count);
        return result;
    }

    @Override
    public void addListener(KafkaListener<T> listener) {
        listeners.add(listener);
    }

    @Override
    public void addExceptionMessageListener(KafkaListener listener) {
        this.exceptionListener=listener;
    }

    @Override
    public void addListener(KafkaBatchListener<T> listener) {
        batchListeners.add(listener);
    }

    @Override
    public void removeListener(KafkaListener<T> listener) {
        listeners.remove(listener);
    }

    @Override
    public void removeListener(KafkaBatchListener<T> listener) {
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
