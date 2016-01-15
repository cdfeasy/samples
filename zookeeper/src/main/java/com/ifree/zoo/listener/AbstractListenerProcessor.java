package com.ifree.zoo.listener;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ifree.zoo.ZPath;
import com.ifree.zoo.ZooEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmitry on 20.12.2015.
 */
public abstract class AbstractListenerProcessor implements ListenerProcessor {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, List<ZooListenerWrapper>> intListeners = new HashMap<>();

    protected void checkListener(String path, PathChildrenCacheEvent event) {
        ZPath zPath = ZPath.getZPath(path);
        boolean isNode = true;
        while (zPath != null) {
            if (intListeners.containsKey(zPath.getFullPath())) {
                processListenerForPath(intListeners.get(zPath.getFullPath()), event, isNode);
            }
            isNode = false;
            zPath = zPath.getParent();
        }
    }

    protected Map<String, List<ZooListenerWrapper>> getIntListeners() {
        return intListeners;
    }

    protected void processListenerForPath(List<ZooListenerWrapper> listeners, PathChildrenCacheEvent event, Boolean isNode) {
        for (ZooListenerWrapper l : listeners) {
            try {
                if(!isNode&&!l.isCheckChildren()){
                    continue;
                }
                if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                    l.onChange(event.getData().getPath(), event.getData().getData(), isNode ? ZooEvent.NodeCreated : ZooEvent.NodeChildrenAdded);
                } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                    l.onDelete(event.getData().getPath(), isNode ? ZooEvent.NodeDeleted : ZooEvent.NodeChildrenDeleted);
                } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                    l.onChange(event.getData().getPath(), event.getData().getData(), isNode ? ZooEvent.NodeDataChanged : ZooEvent.NodeChildrenChanged);
                }
            } catch (Exception ex) {
                logger.error("Exception in listener", ex);
            }
        }
    }

    @Override
    public void registerListener(String path, ZooListener listener, boolean checkChildren) {
        if ("/".equals(path)) {
            path = "";
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 2);
        }
        if (intListeners.containsKey(path)) {
            intListeners.get(path).add(new ZooListenerWrapper(listener,checkChildren));
        } else {
            List<ZooListenerWrapper> l = new ArrayList<>();
            l.add(new ZooListenerWrapper(listener,checkChildren));
            intListeners.put(path, l);
            try {
                initListener(path);
            } catch (Exception e) {
                logger.error("cannot init listener", e);
            }
        }
    }

    @Override
    public void removeListener(ZooListener listener) {
        ZooListenerWrapper wrapper=new ZooListenerWrapper(listener,false);
        intListeners.forEach((a, b) -> {
            if (b.contains(wrapper)) b.remove(wrapper);
        });
    }

    protected void initListener(String path) throws Exception {

    }
}
