package kafka.client.impl;

import kafka.client.Serializer;
import kafka.client.common.KafkaConfigBuilder;
import kafka.client.serializer.GsonSerializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by d.asadullin on 03.03.2016.
 */
public class KafkaReceiveProcessor<T> implements Runnable {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private KafkaConsumer<byte[], byte[]> consumer;
    private int batchSize;
    private Deserializer<T> keyDeserializer;
    private Deserializer<T> valueDeserializer;
    private Class<T> clazz;
    private Integer cnt = 0;
    private AtomicInteger received=new AtomicInteger();
    private LinkedList<Map<TopicPartition, OffsetAndMetadata>> toPush = new LinkedList<>();
    private AtomicBoolean isRunning;
    private ConcurrentHashMap<TopicPartition,LinkedBlockingQueue<ConsumerRecord<byte[], byte[]>>> records=new ConcurrentHashMap<>();
    private ReentrantLock lock=new ReentrantLock();
    private Map<TopicPartition,Long> commitMap=new HashMap<>();

    public KafkaReceiveProcessor(AtomicBoolean isRunning, KafkaConsumer<byte[], byte[]> consumer, KafkaConfigBuilder configBuilder, Class clazz) {
        this.consumer = consumer;
        this.batchSize = configBuilder.getBatchSize();
        this.keyDeserializer=configBuilder.getKeyDeserializer();
        this.valueDeserializer=configBuilder.getValueDeserializer();
        this.clazz = clazz;
        this.isRunning=isRunning;
    }

    private void commit() {
        if ( commitMap.isEmpty()) {
            return;
        }
        for(Map.Entry<TopicPartition,Long> entry:commitMap.entrySet()) {
            if(entry.getValue()>0) {
                logger.debug("commit " + entry.getKey().topic()+"/"+entry.getKey().partition() + "/" + entry.getValue());
                consumer.commitSync(Collections.singletonMap(entry.getKey(), new OffsetAndMetadata(entry.getValue())));
            }
        }
        commitMap.clear();
    }
    public void check(){
        lock.lock();
        try {
            while (received.get() >= batchSize) {
                received.set(0);
                commit();
            }
        }finally {
            lock.unlock();
        }
    }

    private  void pollIfNeeded() throws InterruptedException {
        lock.lock();
        try {
            for (TopicPartition partition : records.keySet()) {
                if (records.get(partition).isEmpty()) {
                    records.remove(partition);
                }
            }
            if (records.isEmpty()) {
                ConsumerRecords<byte[], byte[]> poll = consumer.poll(1000);
                for (TopicPartition partition : poll.partitions()) {
                    records.put(partition, new LinkedBlockingQueue<>(poll.records(partition)));
                }
            }
        }finally {
            lock.unlock();
        }
        Thread.sleep(100);
    }

    public T receive(){
        if(!isRunning.get()){
            return null;
        }
        lock.lock();
        try {
            for (TopicPartition partition : records.keySet()) {
                ConsumerRecord<byte[], byte[]> rec = records.get(partition).poll();
                if (rec != null) {
                    try {
                        T obj = (T) valueDeserializer.deserialize(partition.topic(), rec.value());
                        commitMap.put(partition, rec.offset() + 1);
                        received.incrementAndGet();
                        return obj;
                    }catch (Exception ex){
                        logger.error("Cannot deserialize message",ex);
                    }
                }
            }
            return null;
        }finally {
            lock.unlock();
        }
    }
    public List<T> batchReceive(int count){
        List<T> list=new ArrayList<>();
        if(!isRunning.get()){
            return list;
        }
        for(int i=0;i<count;i++){
            T obj=receive();
            if(obj!=null) {
                list.add(obj);
            } else{
                return list;
            }
        }
        return list;
    }

    @Override
    public void run() {
        if(!isRunning.get()){
            return;
        }
        try {
            while (isRunning.get()) {
                pollIfNeeded();
                check();
            }
            commit();
            consumer.close();
        } catch (Exception ex) {
             logger.error("",ex);
        }
    }
}
