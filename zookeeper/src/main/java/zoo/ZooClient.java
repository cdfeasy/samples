package zoo;

import org.apache.zookeeper.CreateMode;

/**
 * Created by d.asadullin on 23.11.2015.
 */
public interface ZooClient {
    void start() throws Exception;
    void stop();
    void registerListener(String path,ZooListener listener);
    void removeListener(ZooListener listener);
    String[] getChildren(String path);
    byte[] getData(String path) throws Exception;
    void createNode(String path,byte[] data,CreateMode mode,boolean createParentsIfNeeded) throws Exception;
    void setData(String path,byte[] data) throws Exception;
    void deleteNode(String path,boolean deleteChildrenIfNeeded) throws Exception;


}
