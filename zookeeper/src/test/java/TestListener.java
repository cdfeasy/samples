import com.ifree.zoo.ZooEvent;
import com.ifree.zoo.listener.ZooListener;

/**
 * Created by dmitry on 27.12.2015.
 */
public class TestListener implements ZooListener {
    @Override
    public boolean onDelete(String path, ZooEvent type) {
        System.out.println(type.name()+"/"+path);
        return false;
    }

    @Override
    public boolean onChange(String path, byte[] data, ZooEvent type) {
        System.out.println(type.name()+"/"+path);
        return false;
    }
}
