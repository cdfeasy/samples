package kafka.client.serializer;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Created by dmitry on 07.03.2016.
 */
public class BasicSerializer implements Serializer, Deserializer {
    @Override
    public Object deserialize(String topic, byte[] data) {
        if(data==null){
            return null;
        }
        return new String(data, StandardCharsets.UTF_8);
    }

    @Override
    public void configure(Map configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, Object data) {
        if (data == null) {
            return new byte[]{};
        } else {
            return data.toString().getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public void close() {

    }
}
