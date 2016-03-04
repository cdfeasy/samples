package kafka.client;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by d.asadullin on 26.02.2016.
 */
public class ClientTest {
    @Test
    public void test() throws InterruptedException, ExecutionException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "test-b2b-dev-02.g01.i-free.ru:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 100000; i++) {
            producer.send(new ProducerRecord<String, String>(
                    "test3",
                    String.format("{\"type\":\"test\", \"t\":%.3f, \"k\":%d}", System.nanoTime() * 1e-9, i)));
            System.out.println("send");
        }
        producer.flush();
        Thread.sleep(10000);
        producer.close();
    }

    @Test
    public void test1() throws InterruptedException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "test-b2b-dev-02.g01.i-free.ru:9092");
        props.put("group.id", "test22");
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        //   props.put("fetch.min.bytes", "500");
        props.put("receive.buffer.bytes", "3000");
         props.put("max.partition.fetch.bytes", "3000");
        props.put("auto.offset.reset", "earliest");

        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

       consumer.subscribe(Arrays.asList("test3","test2"));
     //  System.out.println(consumer.partitionsFor("test3"));
       int cnt=0;
       label: for(int i=0;i<10;i++){
            ConsumerRecords<String, String> records = consumer.poll(3000);


            for (TopicPartition partition : records.partitions()) {
                List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
                long lastOffset=0;
                for (ConsumerRecord<String, String> record : partitionRecords) {
                    System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
                    cnt++;
                    lastOffset =  record.offset();
                    if(cnt>100){
                        consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
                        continue label;
                    }
                }
                consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
            }
        }


        // consumer.seekToBeginning(new TopicPartition("test1",0));
//        ConsumerRecords<String, String> records = consumer.poll(3000);
//
//        //consumer.seek();
//        int i = 0;
//        for (ConsumerRecord<String, String> record : records) {
//            System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
//
//        }
//        consumer.commitSync();

        consumer.close();
    }
}
