package ru.cdf.zoo.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmitry on 20.12.2015.
 */
public class RealTimeProcessor extends AbstractListenerProcessor {
    private CuratorFramework client;
    private PathChildrenCache pathcache;
    public RealTimeProcessor(CuratorFramework client){
        this.client=client;
        initRealTimeListener();
    }
    private void initRealTimeListener() {
        pathcache = new PathChildrenCache(client, "/", true);
        PathChildrenCacheListener listner1 = new PathChildrenCacheListener() {
            Map<String, PathChildrenCache> listener2s = new HashMap<String, PathChildrenCache>();
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                String path = event.getData().getPath();
                checkListener(path,event);
                if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                    // System.out.println("added "+path);
                    PathChildrenCache cache2 = new PathChildrenCache(client, path, true);
                    cache2.getListenable().addListener(this);
                    cache2.start();
                    listener2s.put(path, cache2);
                } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                    // System.out.println("deleted "+path);
                    PathChildrenCache cache2 = listener2s.remove(path);
                    if (cache2 != null) cache2.close();
                } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED){
                    // System.out.println("changed "+path);

                }
            }
        };
        pathcache.getListenable().addListener(listner1);
    }


    @Override
    public void start() throws Exception {
        pathcache.start();
    }

    @Override
    public void stop() throws IOException {
        pathcache.close();
    }
}
