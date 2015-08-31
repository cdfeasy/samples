package kafka;

import kafka.api.PartitionFetchInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.FetchRequest;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndOffset;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by d.asadullin on 25.08.2015.
 */
public class Router {
    kafka.javaapi.consumer.SimpleConsumer consumer;
    Producer<String, String> producer;
    private AtomicBoolean isStarted = new AtomicBoolean(true);
    private String topic;
    private AtomicLong send;
    private AtomicLong received;

    public Router(String name, AtomicLong send,  AtomicLong received) throws Exception {
        Properties props = new Properties();
        props.put("zookeeper.connect", "172.27.14.10:2181");
        props.put("group.id", "test");
        props.put("consumer.timeout.ms", "5000");
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.enable", "false");
        props.put("metadata.broker.list", "172.27.14.10:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put(" auto.offset.reset", "0");

        // props.put("partitioner.class", "example.producer.SimplePartitioner");
        props.put("request.required.acks", "1");
        topic = name;
        consumer = new SimpleConsumer("172.27.14.10", 9092, 1000, 1024*1024, "test");
        ProducerConfig config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);
        this.send=send;
        this.received=received;


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
                    while (isStarted.get()) {
                        Map<TopicAndPartition, PartitionFetchInfo> map = new HashMap<>();
                        map.put(new TopicAndPartition(topic, 0), new PartitionFetchInfo(offset, 100000));
                        FetchRequest request = new FetchRequest(0, "test", 1000, 0, map);
                        FetchResponse fetch = consumer.fetch(request);
                        for (MessageAndOffset test : fetch.messageSet(topic, 0)) {
                            ByteBuffer payload = test.message().payload();
                            received.incrementAndGet();
                            byte[] bytes = new byte[payload.limit()];
                            payload.get(bytes);
                            //System.out.println(new String(bytes, "UTF-8") + "/" + test.offset());
                            String msg = new String(bytes, "UTF-8");
                            String local_topic = "test" + msg.substring(msg.length() - 2);
                            KeyedMessage<String, String> data = new KeyedMessage<String, String>(local_topic, msg);
                            //System.out.println("send msg from "+topic +" to "+local_topic);
                            producer.send(data);
                            send.incrementAndGet();
                            counter.put(local_topic,counter.get(local_topic)+1);
                            offset = test.nextOffset();
                            if(offset%5000==0){
                                end=System.currentTimeMillis();
                                System.out.println(Long.toString(end-start)+"/"+offset+"/"+counter);
                                start=System.currentTimeMillis();
                                System.out.println(Long.toString(received.get())+"/"+Long.toString(send.get()));
                                long time=System.currentTimeMillis()-totalStart;
                                System.out.println(new Double(received.get()/(double)time*1000).longValue()+"/"+new Double(send.get()/(double)time*1000).longValue());
                            }


                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        Router consumer = new Router("mass",new AtomicLong(0),new AtomicLong(0));
        consumer.start();
    }
}