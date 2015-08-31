package kafka.v1;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndMetadata;
import org.apache.kafka.common.errors.TimeoutException;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by d.asadullin on 31.08.2015.
 */
public class KafkaListenerConsumer {
    private ConsumerConnector consumer;
    private Producer<String, String> producer;
    private AtomicBoolean isStarted = new AtomicBoolean(true);
    private String topic;
    private AtomicLong received;
    private ConsumerConfig consumerConfig;
    private MessageListener listener;

    public KafkaListenerConsumer(String topicName, String group, AtomicLong received,boolean fromStart) throws Exception {
        Properties props = new Properties();
        props.put("zookeeper.connect", "172.27.14.10:2181");
        props.put("group.id", group);
        props.put("consumer.timeout.ms", "5000");
        props.put("zookeeper.session.timeout.ms", "4000");
        props.put("zookeeper.sync.time.ms", "2000");
        props.put("auto.commit.enable", "false");
        props.put("metadata.broker.list", "172.27.14.10:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        if(fromStart) {
            props.put("auto.offset.reset", "smallest");
        }
        // props.put("partitioner.class", "example.producer.SimplePartitioner");
        props.put("request.required.acks", "1");
        topic = topicName;
        this.received = received;
        consumerConfig = new ConsumerConfig(props);
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(consumerConfig);
    }

    public void stop() {
        isStarted.set(false);
    }

    public void addListener(MessageListener listener) {
        this.listener = listener;
    }


    public void start() throws UnsupportedEncodingException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long offset = 0;
                    Map<String, Integer> topicCountMap = new HashMap<>();
                    topicCountMap.put(topic, 1);
                    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
                    List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
                    ConsumerIterator<byte[], byte[]> iterator = streams.get(0).iterator();
                    long lastCommit=0;
                    while (isStarted.get()) {
                        try {
                            consumer.commitOffsets();
                            while (iterator.hasNext()&&isStarted.get() ) {
                                received.incrementAndGet();
                                MessageAndMetadata<byte[], byte[]> next = iterator.next();
                                String msg = new String(next.message(), "UTF-8");
                                String key = null;
                                if (next.key() != null) {
                                    key = new String(next.key(), "UTF-8");
                                }
                                try {
                                    if (listener != null)
                                        listener.onMessage(key, msg,next.offset());
                                } catch (Exception ex) {
                                    //nope
                                }
                                if(next.offset()%50==0) {
                                    lastCommit=next.offset();
                                    consumer.commitOffsets();
                                }
                            }
                        } catch (ConsumerTimeoutException ex) {
                            consumer.commitOffsets();
                        }
                    }
                } catch (Exception ex) {
                    if(consumer!=null){
                        try {
                            consumer.commitOffsets();
                        }catch (Exception ex1){
                            //nope
                        }
                    }
                    ex.printStackTrace();
                }
                if(consumer!=null){
                    consumer.shutdown();
                }
            }
        }).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String name="mass228";
        KafkaListenerConsumer consumer = new KafkaListenerConsumer(name, "g3", new AtomicLong(0),false);
        MessageListener listener1=new MessageListener() {
            @Override
            public boolean onMessage(String key, String message,long offset) {
                System.out.println(Long.toString(offset)+"/"+message);
                return true;
            }
        } ;
        consumer.addListener(listener1);
        consumer.start();
        KafkaProducer sampleProducer=new KafkaProducer(new AtomicLong(0));
        sampleProducer.sendMessage(name, "aaa5", "blabla"+new Random().nextInt());
        sampleProducer.sendMessage(name, "aaa1", "blabla"+new Random().nextInt());
        sampleProducer.sendMessage(name, "aaa2", "blabla"+new Random().nextInt());
        sampleProducer.sendMessage(name, "aaa3", "blabla"+new Random().nextInt());
        sampleProducer.stop();

        Thread.sleep(1000);
        consumer.stop();
    }
}
