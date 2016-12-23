package kafka.client;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by d.asadullin on 26.02.2016.
 */
public class ClientTest {

    EmbeddedServer embeddedServer;
    @Before
    public void before() throws IOException, InterruptedException {
        Properties kafkaProperties=new Properties();
        kafkaProperties.load(Class.class.getResourceAsStream("/kafkalocal.properties"));
        Properties zkProperties=new Properties();
        zkProperties.load(Class.class.getResourceAsStream("/zklocal.properties"));;
        embeddedServer=new EmbeddedServer(kafkaProperties,zkProperties);
    }

    @After
    public void after(){
        embeddedServer.stop();
    }

    @Test
    public void test() throws InterruptedException, ExecutionException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 10000; i++) {
            producer.send(new ProducerRecord<String, String>(
                    "test3",
                    String.format("{\"type\":\"test\", \"t\":%.3f, \"k\":%d}", System.nanoTime() * 1e-9, i)), new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    System.out.println("send"+metadata.offset());
                }
            });
          //  System.out.println("send");
        }
        producer.flush();
        Thread.sleep(1000);
        producer.close();
    }

    @Test
    public void test1() throws InterruptedException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "test-b2b-dev-02.g01.i-free.ru:9092");
        props.put("group.id", "test100");
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

        consumer.subscribe(Arrays.asList("__consumer_offsets"));
     //   System.out.println(consumer.);

//          System.out.println(consumer.partitionsFor("test3"));
//        int cnt = 0;
//        label:
//        for (int i = 0; i < 10; i++) {
//            ConsumerRecords<String, String> records = consumer.poll(3000);
//
//
//            for (TopicPartition partition : records.partitions()) {
//                List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
//                long lastOffset = 0;
//                for (ConsumerRecord<String, String> record : partitionRecords) {
//                    System.out.printf("%d offset = %d, key = %s, value = %s\n",i, record.offset(), record.key(), record.value());
//                    cnt++;
//                    lastOffset = record.offset();
//                    if (cnt > 100) {
//                        consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
//                        cnt=0;
//                        continue label;
//                    }
//                }
//                consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
//            }
//        }


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


//    @Test
//    public void test3() throws Exception {
//        int cnt=0;
//        while (true) {
//            KafkaConfigBuilder kafkaConfigBuilder = new KafkaConfigBuilder().setServers("test-b2b-dev-02.g01.i-free.ru:9092").
//                    setTopic("test3").setGroupId("test72").setBatchSize(300).setMode(KafkaClient.Mode.All);
//            KafkaClientImpl<String> kafkaClient = new KafkaClientImpl<String>(kafkaConfigBuilder);
//            kafkaClient.start();
//            for (int i = 0; i < 1000; i++) {
//                for (String s : kafkaClient.receive(100)) {
//                    System.out.println(s);
//                    cnt++;
//                }
//                Thread.sleep(100);
//            }
//
//            kafkaClient.close();
//            System.out.println("cnt=" + cnt);
//        }


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


// //   }
// @Test
// public void test5() throws Exception {
//      Arrays.stream(new int[]{1,2,3,4}).flatMap()
// }

    @Test
    public void test4() throws Exception {
        int cnt=0;
//        while (true) {
//            KafkaConfigBuilder<String,String> kafkaConfigBuilder = new KafkaConfigBuilder<>().setServers("test-b2b-dev-02.g01.i-free.ru:9092").
//                    setTopic("test3").setGroupId("test74").setBatchSize(300).setMode(KafkaClient.Mode.All);
//           // kafkaConfigBuilder.setValueDeserializer(new ByteArrayDeserializer());
//            KafkaClient<String,String> kafkaClient = new KafkaClientImpl<>(kafkaConfigBuilder);
//            kafkaClient.start();
//            for (int i = 0; i < 1000; i++) {
//                for (KafkaEntry<String,String> s : kafkaClient.receiveEntries(100)) {
//                    System.out.println(s.getKey()+"/"+s.getObject());
//                    cnt++;
//                }
//                Thread.sleep(100);
//            }
//
//            kafkaClient.close();
//            System.out.println("cnt=" + cnt);
//        }


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


    }
}
