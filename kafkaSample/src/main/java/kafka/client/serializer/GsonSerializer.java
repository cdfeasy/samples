package kafka.client.serializer;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

/**
 * Created by dmitry on 07.01.2016.
 */
public class GsonSerializer {
    Gson gson=new Gson();
    public byte[] getBytes(Object object) {
        return gson.toJson(object).getBytes(StandardCharsets.UTF_8);
    }

    public <T> T fromBytes(byte[] bytes, Class<T> toClass) {
        return gson.fromJson(new String(bytes,StandardCharsets.UTF_8),toClass);
    }
}
