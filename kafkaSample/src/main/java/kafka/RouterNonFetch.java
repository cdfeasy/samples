package kafka;

import kafka.api.PartitionFetchInfo;
import kafka.common.TopicAndPartition;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.FetchRequest;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndMetadata;
import kafka.message.MessageAndOffset;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.kafka.common.errors.TimeoutException;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by d.asadullin on 27.08.2015.
 */
public class RouterNonFetch {
    ConsumerConnector consumer;
    Producer<String, String> producer;
    private AtomicBoolean isStarted = new AtomicBoolean(true);
    private String topic;
    private AtomicLong send;
    private AtomicLong received;
    ConsumerConfig consumerConfig;

    public RouterNonFetch(String name, AtomicLong send,  AtomicLong received) throws Exception {
        Properties props = new Properties();
        props.put("zookeeper.connect", "172.27.14.10:2181");
        props.put("group.id", "1");
        props.put("consumer.timeout.ms", "5000");
        props.put("zookeeper.session.timeout.ms", "4000");
        props.put("zookeeper.sync.time.ms", "2000");
        props.put("auto.commit.enable", "false");
        props.put("metadata.broker.list", "172.27.14.10:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("auto.offset.reset", "smallest");
        // props.put("partitioner.class", "example.producer.SimplePartitioner");
        props.put("request.required.acks", "1");
        topic = name;

        ProducerConfig config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);
        this.send=send;
        this.received=received;
        consumerConfig =new ConsumerConfig(props);
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(consumerConfig);


    }

    public void stop() {
        isStarted.set(false);
    }


    public void start() throws UnsupportedEncodingException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long offset = 0;
                    Map<String,Integer> counter=new HashMap<String, Integer>();
                    for(int i=0;i<100;i++){
                        String num=String.valueOf(i);
                        if(num.length()==1) num="0"+num;
                        num="test"+num;
                        counter.put(num,0);
                    }
                    long start=System.currentTimeMillis();
                    long end=System.currentTimeMillis();
                    long totalStart=System.currentTimeMillis();
                    Map<String, Integer> topicCountMap = new HashMap<>();
                    topicCountMap.put(topic, 1);


                    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
                    List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
                    ConsumerIterator<byte[], byte[]> iterator = streams.get(0).iterator();
                    while (isStarted.get()) {
                        try {
                            while (iterator.hasNext()) {
                                received.incrementAndGet();
                                MessageAndMetadata<byte[], byte[]> next = iterator.next();
                                //System.out.println(new String(bytes, "UTF-8") + "/" + test.offset());
                                String msg = new String(next.message(), "UTF-8");
                                String local_topic = "test" + msg.substring(msg.length() - 2);
                                KeyedMessage<String, String> data = new KeyedMessage<String, String>(local_topic, msg);
                                //System.out.println("send msg from "+topic +" to "+local_topic);
                                System.out.println("offset:"+next.offset());
                                producer.send(data);
                                send.incrementAndGet();
                                counter.put(local_topic, counter.get(local_topic) + 1);
                                offset = next.offset();
                                if (offset % 50 == 0) {
                                    end = System.currentTimeMillis();
                                    System.out.println(Long.toString(end - start) + "/" + offset + "/" + counter);
                                    start = System.currentTimeMillis();
                                    System.out.println(Long.toString(received.get()) + "/" + Long.toString(send.get()));
                                    long time = System.currentTimeMillis() - totalStart;
                                    System.out.println(new Double(received.get() / (double) time * 1000).longValue() + "/" + new Double(send.get() / (double) time * 1000).longValue());
                                    consumer.commitOffsets();
                                }


                            }
                        } catch (TimeoutException ex){
                            System.out.println(ex.getMessage());
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        RouterNonFetch consumer = new RouterNonFetch("mass",new AtomicLong(0),new AtomicLong(0));
        consumer.start();
    }
}
