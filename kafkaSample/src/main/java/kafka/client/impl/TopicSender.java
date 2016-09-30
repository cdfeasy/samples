package kafka.client.impl;

import kafka.client.Serializer;
import kafka.client.common.KafkaEntry;
import kafka.client.common.KafkaProducerClient;
import kafka.client.impl.callback.EmptyCallback;
import kafka.client.impl.callback.ListenerWrapper;
import kafka.client.impl.callback.NoSendCallback;
import org.apache.kafka.clients.producer.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Created by d.asadullin on 27.09.2016.
 */
public class TopicSender<K, V> implements KafkaProducerClient<K, V> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private AtomicBoolean isRunning;
    private LinkedBlockingQueue<KafkaEntry<byte[], byte[]>> send;
    private Serializer keySerializer;
    private Serializer valueSerializer;
    private ExecutorService onMessageExecutor;
    private String topic;

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

    public TopicSender(String topic, ExecutorService onMessageExecutor, Serializer keySerializer, Serializer valueSerializer, LinkedBlockingQueue<KafkaEntry<byte[], byte[]>> send, AtomicBoolean isRunning) {
        this.isRunning = isRunning;
        this.send = send;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.onMessageExecutor = onMessageExecutor;
        this.topic = topic;
    }

    @Override
    public void send(V object) throws Exception {
        NoSendCallback callback = new NoSendCallback();
        byte[] obj = valueSerializer.serialize(object);
        send.add(new KafkaEntry<byte[], byte[]>(obj, new ListenerWrapper(callback, onMessageExecutor, logger)));
        Exception ex = callback.get();
        if (ex != null) {
            throw ex;
        }
    }

    @Override
    public void send(List<V> objects) throws Exception {
        send.addAll(objects.stream().map((a) -> valueSerializer.serialize( a)).map((a) -> new KafkaEntry<byte[], byte[]>(a, new EmptyCallback())).collect(Collectors.toList()));
    }

    //TODO create cache of listeners
    @Override
    public void send(V object, Callback callback) {
        byte[] obj = valueSerializer.serialize( object);
        send.add(new KafkaEntry<byte[], byte[]>(obj, new ListenerWrapper(callback, onMessageExecutor, logger)));
    }

    @Override
    public void send(K key, V object, Callback callback) {
        byte[] k = keySerializer.serialize( key);
        byte[] obj = valueSerializer.serialize( object);
        send.add(new KafkaEntry<byte[], byte[]>(k, obj, new ListenerWrapper(callback, onMessageExecutor, logger)));
    }

    @Override
    public void sendBatch(List<KafkaEntry<K, V>> objects) {
        for (KafkaEntry<K, V> entry : objects) {
            byte[] obj = valueSerializer.serialize( entry.getObject());
            if (entry.getKey() != null) {
                byte[] k = keySerializer.serialize( entry.getKey());
                send.add(new KafkaEntry<>(k, obj, new ListenerWrapper(entry.getCallback(), onMessageExecutor, logger)));
            } else {
                send.add(new KafkaEntry<>(obj, new ListenerWrapper(entry.getCallback(), onMessageExecutor, logger))) ;
            }
        }
    }

    @Override
    public void close() throws Exception {

    }
}
