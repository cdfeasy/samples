package kafka.client.common;

import kafka.client.Serializer;
import kafka.client.impl.KafkaServiceImpl;
import kafka.client.impl.KafkaClientImpl;
import kafka.client.impl.TopicReceiver;
import kafka.client.impl.TopicSender;
import kafka.client.serializer.GsonSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by d.asadullin on 28.09.2016.
 */
public class ClientBuilder<K,V> {
    private String topic;
    private Class keyClazz;
    private Class valueClazz;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;
    private KafkaService.Mode mode;
    private String postfix;
    private String prefix;
    private KafkaConfigBuilder configBuilder;
    private ScheduledExecutorService executorService;
    private KafkaServiceImpl client;

    public ClientBuilder setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public ClientBuilder setKeyClazz(Class keyClazz) {
        this.keyClazz = keyClazz;
        return this;
    }

    public ClientBuilder setValueClazz(Class valueClazz) {
        this.valueClazz = valueClazz;
        return this;
    }

    public ClientBuilder setKeySerializer(Serializer keySerializer) {
        this.keySerializer = keySerializer;
        return this;
    }

    public ClientBuilder setValueSerializer(Serializer valueSerializer) {
        this.valueSerializer = valueSerializer;
        return this;
    }

    public ClientBuilder setMode(KafkaService.Mode mode) {
        this.mode = mode;
        return this;
    }

    public ClientBuilder setPostfix(String postfix) {
        this.postfix = postfix;
        return this;
    }

    public ClientBuilder setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public ClientBuilder setExecutorService(ScheduledExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }
    private static Class<?> getClass(Class type) {
        Type[] genericInterfaces = type.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericInterface;
                Type[] actualTypeArguments = pt.getActualTypeArguments();

                for (Type actualTypeArgument : actualTypeArguments) {
                    if (actualTypeArgument instanceof Class) {
                        return (Class) actualTypeArgument;
                    }
                }
            }
        }
        return null;
    }

    public static <K,V> ClientBuilder<K,V> getBuilder(String topic,Class<K> keyClazz,Class<V> valueClazz,KafkaConfigBuilder configBuilder,KafkaServiceImpl client) throws Exception {
        if(topic==null&&byte[].class.equals(valueClazz)){
            throw new Exception("Cannot create client for unnamed byte[] objects");
        }
        if(topic==null){
           topic=valueClazz.getSimpleName();
        }
        ClientBuilder<K,V>  cb=new ClientBuilder<> ();
        cb.setKeyClazz(keyClazz).setValueClazz(valueClazz).setTopic(topic);
        cb.client=client;
        cb.configBuilder=configBuilder;
        return cb;
    }
    public static ClientBuilder<byte[],byte[]> getBuilder(String topic,KafkaConfigBuilder configBuilder,KafkaServiceImpl client) throws Exception {
        return getBuilder(topic,byte[].class,byte[].class,configBuilder,client);
    }

    public KafkaClientImpl<K,V> build(){
        if(keySerializer==null){
            setKeySerializer(new GsonSerializer<>(keyClazz));
        }
        if(valueSerializer==null){
            setKeySerializer(new GsonSerializer<>(valueClazz));
        }
        if(executorService==null){
            executorService= Executors.newSingleThreadScheduledExecutor();
        }
        if(prefix!=null)
            topic=topic+prefix;
        if(postfix!=null)
            topic=topic+postfix;
        TopicSender<K,V> sender;
        TopicReceiver<K,V> receiver=null;
        if(KafkaService.Mode.All.equals(mode)|| KafkaService.Mode.Consumer.equals(mode)){
            receiver=new TopicReceiver<>(executorService,configBuilder,client,client.getIsRunning());
        }
        sender=new TopicSender<>(topic,executorService,keySerializer,valueSerializer,client.getSend(),client.getIsRunning());
        return new KafkaClientImpl<>(receiver,sender);
    }

    public static void main(String[] args) throws Exception {
        ClientBuilder<Map,String> builder=ClientBuilder.<Map,String>getBuilder("bla",Map.class,String.class,null,null);
    }

}
