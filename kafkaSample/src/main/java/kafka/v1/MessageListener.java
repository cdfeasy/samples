package kafka.v1;

/**
 * Created by d.asadullin on 31.08.2015.
 */
public interface MessageListener {
    public boolean onMessage(String key,String message, long offset);
}
