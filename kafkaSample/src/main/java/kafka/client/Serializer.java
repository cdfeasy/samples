package kafka.client;

/**
 * Created by d.asadullin on 29.02.2016.
 */
public interface Serializer<T> {
    byte[] serialize(T object);
    T deserialize(byte[] data);
}
