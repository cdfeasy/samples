package zoo;

import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.Watcher;

/**
 * Created by d.asadullin on 23.11.2015.
 */
public interface ZooListener {
    boolean onDelete();
    boolean onCreate(byte[] data);
    boolean onChildChanged(String path,byte[] data,Watcher.Event.EventType type);
    boolean onChange(byte[] data);
}
