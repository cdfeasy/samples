package com.ifree.zoo.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by dmitry on 20.12.2015.
 */
public class RealTimeProcessor extends AbstractListenerProcessor {
    private CuratorFramework client;
    private PathChildrenCache pathcache;
    private PathChildrenCacheListener listener;
    private AtomicBoolean isStarted=new AtomicBoolean(false);
    private Map<String, PathChildrenCache> listener2s = new HashMap<String, PathChildrenCache>();
    public RealTimeProcessor(CuratorFramework client){
        this.client=client;
        initRealTimeListener();
    }
    private void initRealTimeListener() {
        pathcache = new PathChildrenCache(client, "/", true);
        listener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                if(!isStarted.get()){
                    return;
                }
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
    }


    @Override
    public void start() throws Exception {
        isStarted.set(true);
        pathcache.getListenable().addListener(listener);
        pathcache.start();
    }

    @Override
    public void stop() throws IOException {
        isStarted.set(false);
        pathcache.getListenable().removeListener(listener);
        for(PathChildrenCache cache:listener2s.values()){
            try {
                cache.close();
            }catch (Exception ex){
                //
            }
        }
        listener2s.clear();
        pathcache.close();
    }
}
