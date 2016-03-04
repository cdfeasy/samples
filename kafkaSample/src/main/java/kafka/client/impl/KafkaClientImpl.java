package kafka.client.impl;

import kafka.client.common.*;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by d.asadullin on 02.03.2016.
 */
public class KafkaClientImpl<T> implements KafkaClient<T> {
    private KafkaProducer<byte[],byte[]> producer;
    private KafkaConsumer<byte[],byte[]> consumer;
    private LinkedBlockingQueue<BatchEntry<T>> send;
    private LinkedBlockingQueue<T> receive;
    private List<KafkaListener<T>> listeners;
    private List<KafkaBatchListener<T>> batchListeners;
    private ExecutorService executorService;
    protected KafkaClientImpl(KafkaConfigBuilder<T> configBuilder){
        receive=new LinkedBlockingQueue<>(configBuilder.getBatchSize());
        send=new LinkedBlockingQueue<>();
        listeners=new CopyOnWriteArrayList<>();
        batchListeners=new CopyOnWriteArrayList<>();
        executorService= Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, ""+configBuilder.getTopic()+"KafkaWorker");
            }
        });
        Properties props=new Properties();
        if(Mode.Producer.equals(configBuilder.getMode())||Mode.All.equals(configBuilder.getMode())){
            props.put("bootstrap.servers", configBuilder.getServers());
            props.put("acks", "all");
            props.put("retries", 1);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            props.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
            producer=new KafkaProducer<byte[], byte[]>(props);
        }
        if(Mode.Consumer.equals(configBuilder.getMode())||Mode.All.equals(configBuilder.getMode())) {
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
            consumer=new KafkaConsumer<byte[], byte[]>(props);
            consumer.subscribe(Arrays.asList(configBuilder.getTopic()));
        }
    }
    @Override
    public void send(T object) {
        send.add(new BatchEntry<T>(object,null));
    }

    @Override
    public void send(List<T> objects) {
        send.addAll(objects.stream().map((a) -> new BatchEntry<T>(a, null)).collect(Collectors.toList()));
    }

    @Override
    public void send(T object, Callback callback) {
        send.add(new BatchEntry<T>(object,callback));
    }

    @Override
    public void sendBatch(List<BatchEntry<T>> objects) {
        send.addAll(objects);
    }

    @Override
    public T receive() throws InterruptedException {
        return receive.poll(10, TimeUnit.SECONDS);
    }

    @Override
    public List<T> receive(int count) {
        List<T> result=new ArrayList<>();
        receive.drainTo(result,count);
        return result;
    }

    @Override
    public void addListener(KafkaListener<T> listener) {
        listeners.add(listener);
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
}
