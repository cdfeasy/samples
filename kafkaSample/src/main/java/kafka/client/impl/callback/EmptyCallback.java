package kafka.client.impl.callback;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
/**
 * Created by d.asadullin on 27.09.2016.
 */
public class EmptyCallback implements Callback {
    @Override
    public void onCompletion(RecordMetadata metadata, Exception exception) {

    }
}
