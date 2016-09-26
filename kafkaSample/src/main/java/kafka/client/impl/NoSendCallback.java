package kafka.client.impl;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by dmitry on 25.09.2016.
 */
public class NoSendCallback implements Callback {
    public Semaphore semaphore=new Semaphore(1);
    private Exception ex;
    public NoSendCallback() throws InterruptedException {
        semaphore.acquire();
    }


    @Override
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        semaphore.release();
        ex=exception;
    }
    public Exception get() throws ExecutionException, InterruptedException {
        if(semaphore.tryAcquire(60,TimeUnit.SECONDS)) {
            return ex;
        }else {
            return new Exception("Send timeout exceeded");
        }
    }
}
