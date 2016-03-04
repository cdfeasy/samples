package kafka.client.impl;

import kafka.client.serializer.GsonSerializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by d.asadullin on 03.03.2016.
 */
public class KafkaReceiveProcessor<T> implements Runnable {
    private KafkaConsumer<byte[], byte[]> consumer;
    private LinkedBlockingQueue<T> receive;
    private int batchSize;
    private GsonSerializer serializer;
    private Class<T> clazz;
    private Integer cnt = 0;
    private LinkedList<Map<TopicPartition, OffsetAndMetadata>> toPush = new LinkedList<>();

    public KafkaReceiveProcessor(KafkaConsumer<byte[], byte[]> consumer, LinkedBlockingQueue<T> receive, int batchSize, GsonSerializer serializer, Class clazz) {
        this.consumer = consumer;
        this.receive = receive;
        this.batchSize = batchSize;
        this.serializer = serializer;
        this.clazz = clazz;
    }

    private void commit(Map<TopicPartition, OffsetAndMetadata> map) {
        if (map.isEmpty()) {
            return;
        }
        consumer.commitSync(map);
    }
    private void check(){
        if(toPush.size()*batchSize+cnt-receive.size()>batchSize ){
            commit(toPush.poll());
        }
    }

    private boolean addPack(Map<TopicPartition, OffsetAndMetadata> map) {
        toPush.push(new HashMap<>(map));
        map.clear();
        return toPush.size()==3;
    }

    @Override
    public void run() {
        try {
            while (toPush.size()<3) {
                Map<TopicPartition, OffsetAndMetadata> map = new HashMap<>();
                ConsumerRecords<byte[], byte[]> records = consumer.poll(1000);
                for (TopicPartition partition : records.partitions()) {
                    List<ConsumerRecord<byte[], byte[]>> partitionRecords = records.records(partition);
                    for (ConsumerRecord<byte[], byte[]> record : partitionRecords) {
                        T obj = serializer.fromBytes(record.value(), clazz);
                        receive.put(obj);
                        if (cnt++>= batchSize) {
                            map.put(partition, new OffsetAndMetadata(record.offset() + 1));
                            cnt-=batchSize;
                            if(addPack(map)){

                            }
                        }
                    }
                    if (partitionRecords.size() > 0) {
                        map.put(partition, new OffsetAndMetadata(partitionRecords.get(partitionRecords.size() - 1).offset() + 1));
                    }
                }
            }
        } catch (Exception ex) {

        }
    }
}
