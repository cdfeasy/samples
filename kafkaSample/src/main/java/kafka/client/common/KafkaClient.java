package kafka.client.common;

import kafka.client.Serializer;
import kafka.client.impl.KafkaClientImpl;
import kafka.client.impl.KafkaServiceImpl;
import kafka.client.impl.TopicReceiver;
import kafka.client.impl.TopicSender;
import kafka.client.serializer.GsonSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by d.asadullin on 29.09.2016.
 */
public interface KafkaClient<K,V> extends KafkaConsumerClient<K,V>,KafkaProducerClient<K,V> {
    String getTopic();
    Class<K> getKeyClass();
    Class<V> getValueClass();

    public static class ClientConfig<K,V> {
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
        private String finalName;

        public ClientConfig setTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public ClientConfig setKeyClazz(Class keyClazz) {
            this.keyClazz = keyClazz;
            return this;
        }

        public ClientConfig setValueClazz(Class valueClazz) {
            this.valueClazz = valueClazz;
            return this;
        }

        public ClientConfig setKeySerializer(Serializer keySerializer) {
            this.keySerializer = keySerializer;
            return this;
        }

        public ClientConfig setValueSerializer(Serializer valueSerializer) {
            this.valueSerializer = valueSerializer;
            return this;
        }

        public ClientConfig setMode(KafkaService.Mode mode) {
            this.mode = mode;
            return this;
        }

        public ClientConfig setPostfix(String postfix) {
            this.postfix = postfix;
            return this;
        }

        public ClientConfig setPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public ClientConfig setExecutorService(ScheduledExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public static <K,V> ClientConfig<K,V> get(String topic, Class<K> keyClazz, Class<V> valueClazz) throws Exception {
            if(topic==null&&byte[].class.equals(valueClazz)){
                throw new Exception("Cannot create client for unnamed byte[] objects");
            }
            if(topic==null){
                topic=valueClazz.getSimpleName();
            }
            ClientConfig<K,V> cb=new ClientConfig<>();
            cb.setKeyClazz(keyClazz).setValueClazz(valueClazz).setTopic(topic);
            return cb;
        }
        public static ClientConfig<byte[],byte[]> get(String topic) throws Exception {
            return get(topic, byte[].class, byte[].class);
        }

        public KafkaClientImpl<K,V> build(KafkaServiceImpl client,KafkaConfigBuilder configBuilder){
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
            return new KafkaClientImpl<>(receiver,sender,topic,keyClazz,valueClazz);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClientConfig that = (ClientConfig) o;

            if (!finalName.equals(that.finalName)) return false;
            if (!keyClazz.equals(that.keyClazz)) return false;
            if (mode != that.mode) return false;
            if (!valueClazz.equals(that.valueClazz)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = keyClazz.hashCode();
            result = 31 * result + valueClazz.hashCode();
            result = 31 * result + mode.hashCode();
            result = 31 * result + finalName.hashCode();
            return result;
        }

        public static void main(String[] args) throws Exception {
            ClientConfig<Map,String> builder= ClientConfig.<Map,String>get("bla", Map.class, String.class);
        }



    }

}
