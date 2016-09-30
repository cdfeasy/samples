package kafka.client.serializer;
import com.google.gson.Gson;
import kafka.client.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Created by dmitry on 07.01.2016.
 */
public class GsonSerializer<T> implements Serializer<T> {
    Gson gson=new Gson();
    private Class<T> toClass;
    public GsonSerializer(Class<T>toClass){
        this.toClass=toClass;
    }
    public byte[] getBytes(Object object) {
        return gson.toJson(object).getBytes(StandardCharsets.UTF_8);
    }

    public  T fromBytes(byte[] bytes, Class<T> toClass) {
        return gson.fromJson(new String(bytes,StandardCharsets.UTF_8),toClass);
    }
    @Override
    public T deserialize(byte[] data) {
        return fromBytes(data,toClass);
    }

    @Override
    public byte[] serialize(T data) {
        return getBytes(data);
    }

}
