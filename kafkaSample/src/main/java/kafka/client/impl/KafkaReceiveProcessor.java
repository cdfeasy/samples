package kafka.client.impl;

import kafka.client.common.KafkaConfigBuilder;
import kafka.client.common.KafkaEntry;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by d.asadullin on 03.03.2016.
 */
public class KafkaReceiveProcessor<K,V> implements Runnable {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private KafkaConsumer<byte[], byte[]> consumer;
    private int batchSize;
    private Deserializer<K> keyDeserializer;
    private Deserializer<V> valueDeserializer;
    private Integer cnt = 0;
    private AtomicInteger received=new AtomicInteger();
    private LinkedList<Map<TopicPartition, OffsetAndMetadata>> toPush = new LinkedList<>();
    private AtomicBoolean isRunning;
    private ConcurrentHashMap<TopicPartition,LinkedBlockingQueue<ConsumerRecord<byte[], byte[]>>> records=new ConcurrentHashMap<>();
    private ReentrantReadWriteLock lock=new ReentrantReadWriteLock();
    private Map<TopicPartition,Long> commitMap=new HashMap<>();
    private long lastCommit;

    public KafkaReceiveProcessor(AtomicBoolean isRunning, KafkaConsumer<byte[], byte[]> consumer, KafkaConfigBuilder configBuilder) {
        this.consumer = consumer;
        this.batchSize = configBuilder.getBatchSize();
        this.keyDeserializer=configBuilder.getKeyDeserializer();
        this.valueDeserializer=configBuilder.getValueDeserializer();
        this.isRunning=isRunning;
    }

    public void commit() {
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
        lock.readLock().lock();
        try {
            while (received.get() >= batchSize) {
                received.set(0);
                commit();
            }
        }finally {
            lock.readLock().unlock();
        }
    }

    private  void pollIfNeeded() throws InterruptedException {
        lock.writeLock().lock();
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
            lock.writeLock().unlock();
        }
    }

    public KafkaEntry<K,V> receive(){
        if(!isRunning.get()){
            return null;
        }
        List<KafkaEntry<K,V>> list=batchReceive(1);
        return list.size()==0?null:list.get(0);
    }
    public List<KafkaEntry<K,V>> batchReceive(int count){
        List<KafkaEntry<K,V>> list=new ArrayList<>();
        if(!isRunning.get()){
            return list;
        }
        lock.readLock().lock();
        try {
            int cnt=0;
            for (TopicPartition partition : records.keySet()) {
                ConsumerRecord<byte[], byte[]> rec = records.get(partition).poll();
                if (rec != null) {
                    try {
                        K key =  keyDeserializer.deserialize(partition.topic(),rec.key());
                        V obj =  valueDeserializer.deserialize(partition.topic(), rec.value());
                        commitMap.put(partition, rec.offset() + 1);
                        received.incrementAndGet();
                        list.add(new KafkaEntry<K, V>(key,obj));
                        if(cnt++==count){
                           break;
                        }
                    }catch (Exception ex){
                        logger.error("Cannot deserialize message",ex);
                    }
                }
            }
        }finally {
            lock.readLock().unlock();
        }
        return list;
    }

    public List<V> batchReceiveValue(int count){
        if(!isRunning.get()){
            return new ArrayList<>();
        }
        List<KafkaEntry<K,V>> rec=batchReceive(count);
        return rec.stream().map((r)->r.getObject()).collect(Collectors.toList());
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
