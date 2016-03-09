package kafka.client.serializer;
import com.google.gson.Gson;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Created by dmitry on 07.01.2016.
 */
public class GsonSerializer<T> implements Serializer<T>,Deserializer<T>{
    public static String CLASS_NAME="kafka.client.deserializer.class";
    Gson gson=new Gson();
    private Class<T> toClass;
    public GsonSerializer(Class<T>toClass){
        this.toClass=toClass;
    }
    public GsonSerializer(){
    }

    public byte[] getBytes(Object object) {
        return gson.toJson(object).getBytes(StandardCharsets.UTF_8);
    }

    public  T fromBytes(byte[] bytes, Class<T> toClass) {
        return gson.fromJson(new String(bytes,StandardCharsets.UTF_8),toClass);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        return fromBytes(data,toClass);
    }

    @Override
    public void configure(Map configs, boolean isKey) {
        if(toClass==null) {
            toClass = (Class<T>) configs.get(CLASS_NAME);
        }
    }

    @Override
    public byte[] serialize(String topic, T data) {
        return getBytes(data);
    }

    @Override
    public void close() {

    }
}
