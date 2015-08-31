package kafka;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.api.PartitionFetchInfo;
import kafka.common.TopicAndPartition;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndMetadata;
import kafka.message.MessageAndOffset;
import kafka.producer.KeyedMessage;
import scala.runtime.AbstractFunction1;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by d.asadullin on 25.08.2015.
 */
public class SampleConsumer {
    kafka.javaapi.consumer.SimpleConsumer consumer;
    private AtomicBoolean isStarted=new AtomicBoolean(true);
    private String topic;
    private AtomicLong send;
    private AtomicLong received;
    public SampleConsumer(String name,  AtomicLong received) throws Exception {
        Properties props = new Properties();
        props.put("zookeeper.connect", "172.27.14.10:2181");
        props.put("group.id", "test");
        props.put("consumer.timeout.ms", "50000");
        props.put("zookeeper.session.timeout.ms", "4000");
        props.put("zookeeper.sync.time.ms", "2000");
        props.put("auto.commit.enable", "false");
        topic=name;
        consumer = new SimpleConsumer("172.27.14.10", 9092, 10000, 1024, "test");
        this.received=received;


    }
    public void stop(){
        isStarted.set(false);
    }
    public void start() throws UnsupportedEncodingException {

        new Thread(new Runnable(){
            @Override
            public void run() {
                long offset = 0;
                long start=System.currentTimeMillis();
                long end=System.currentTimeMillis();
                while (isStarted.get()) {
//                    Map<TopicAndPartition,PartitionFetchInfo> map=new HashMap<>();
//                    map.put(new TopicAndPartition(topic,0),new PartitionFetchInfo(offset,100));
//                    FetchRequest request = new FetchRequest(0, "test", 1000, 0, map);
                    FetchRequest request=new FetchRequestBuilder().clientId("test").addFetch(topic,0,offset,10000).minBytes(0).maxWait(100000).build();
                    FetchResponse fetch = consumer.fetch(request);
                    for (MessageAndOffset test : fetch.messageSet(topic, 0)) {
                        received.incrementAndGet();
                        ByteBuffer payload = test.message().payload();
                        byte[] bytes = new byte[payload.limit()];
                        payload.get(bytes);
//                        try {
//                            System.out.println(new String(bytes, "UTF-8") + "/" + test.offset());
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }

                        offset=test.nextOffset();
//                        if(offset%1000==0){
//                            end=System.currentTimeMillis();
//                            System.out.println("concumer "+topic+" processed "+String.valueOf(offset)+"  time "+Long.toString(end-start));
//                            start=System.currentTimeMillis();
//                        }
                    }
                }
            }
        }).start();

    }
//        Map<MetricName, ? extends Metric> metrics = consumer.metrics();
//        for (Map.Entry<MetricName, ? extends Metric> entry:metrics.entrySet()){
//            System.out.println(entry.getKey()+"/"+entry.getValue());
//        }
//        Map<String, ConsumerRecords<String, String>> poll = consumer.poll(10000);
//        //OffsetMetadata commit = consumer.commit(true);
//        System.out.println(poll);
//
//        List<String> topics = Collections.singletonList("test");
//        TopicPartition topicPartition=new TopicPartition("test",1);
//        Map<TopicPartition,Long> map=new HashMap<>();
//        map.put(topicPartition,0l);
//      //  consumer.seek(map);
//    //    System.out.println(consumer.
//                 System.out.println(consumer.position(map.keySet()));
//        System.out.println(map.get(topicPartition));
//        System.out.println(topicPartition.toString());
    // consumer.close();

    public static void main(String[] args) throws Exception {
        SampleConsumer consumer = new SampleConsumer("test16",new AtomicLong());
        consumer.start();
    }
}
