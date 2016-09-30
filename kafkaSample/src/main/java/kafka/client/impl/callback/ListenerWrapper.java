package kafka.client.impl.callback;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;

/**
 * Created by dmitry on 27.09.2016.
 */
public class ListenerWrapper implements Callback {
    private ExecutorService ex;
    private Callback callback;
    private Logger logger;

    public ListenerWrapper(Callback callback, ExecutorService ex, Logger logger) {
        this.ex = ex;
        this.callback = callback;
        this.logger = logger;
    }

    @Override
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        ex.submit(() -> {
            try {
                callback.onCompletion(metadata, exception);
            }catch (Exception ex){
                logger.error("Listener " + callback.toString() + " error", ex);
            }
        });
    }
}
