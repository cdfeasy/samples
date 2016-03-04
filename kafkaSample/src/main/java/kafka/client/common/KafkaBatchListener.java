package kafka.client.common;

import java.util.List;

/**
 * Created by d.asadullin on 02.03.2016.
 */
public interface KafkaBatchListener<T> {
    void onMessages(List<T> messages);
}
