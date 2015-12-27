package ru.cdf.zoo.listener;

import ru.cdf.zoo.ZPath;
import ru.cdf.zoo.ZooEvent;
import ru.cdf.zoo.client.ZooClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by dmitry on 20.12.2015.
 */
public class ScheduledProcessor extends AbstractListenerProcessor {
    private ZooClient client;
    private ScheduledExecutorService executorService;
    private Integer timeOut;
    private Map<String, ZPath> data = new HashMap<>();
    private ReentrantLock lock = new ReentrantLock();

    public ScheduledProcessor(ZooClient client, Integer timeOut) {
        this.client = client;
        this.timeOut = timeOut;

    }

    @Override
    public void start() throws Exception {
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(new CheckListeners(), 0, timeOut, TimeUnit.SECONDS);
    }

    @Override
    public void stop() throws IOException {
        if (executorService != null) {
            executorService.shutdown();
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
    }

    protected void initListener(String path) throws Exception {
        lock.lock();
        try {
            if (!data.containsKey(path)) {
                ZPath zPath = ZPath.getZPath(client, path, null);
                data.put(path, zPath);
            }
        } finally {
            lock.unlock();
        }
    }

    private void checkChanges(Map<String, ZPath> oldData, Map<String, ZPath> newData) throws Exception {
        for (String path : getIntListeners().keySet()) {
            List<ZooListener> listeners = getIntListeners().get(path);
            ZPath o = oldData.get(path);
            ZPath n = newData.get(path);
            diff(o, n, listeners, true);
            Map<String, ZPath> oldListTree = ZPath.toFlatMap(o);
            Map<String, ZPath> newListTree = ZPath.toFlatMap(n);
            oldListTree.remove(path);
            newListTree.remove(path);
            for (Map.Entry<String, ZPath> cur : oldListTree.entrySet()) {
                ZPath oldCur = cur.getValue();
                ZPath newCur = newListTree.get(cur.getKey());
                diff(oldCur, newCur, listeners, false);
                newListTree.remove(cur.getKey());
            }
            for (Map.Entry<String, ZPath> cur : newListTree.entrySet()) {
                ZPath oldCur = null;
                ZPath newCur = cur.getValue();
                diff(oldCur, newCur, listeners, false);
            }
        }
    }

    private void diff(ZPath oldValue, ZPath newValue, List<ZooListener> listeners, boolean isRoot) throws Exception {
        if (oldValue != null && newValue != null) {
            if ((oldValue.getVersion() != newValue.getVersion()) || (oldValue.getTime() != newValue.getTime())) {
                byte[] data = client.getData(newValue.getFullPath());
                listeners.forEach(new Consumer<ZooListener>() {
                    @Override
                    public void accept(ZooListener zooListener) {
                        try {
                            zooListener.onChange(newValue.getFullPath(), data, isRoot ? ZooEvent.NodeDataChanged : ZooEvent.NodeChildrenChanged);
                        } catch (Exception ex) {
                            logger.error("Listener exception", ex);
                        }
                    }
                });
            }
        } else if (oldValue == null && newValue != null) {
            byte[] data = client.getData(newValue.getFullPath());
            listeners.forEach(new Consumer<ZooListener>() {
                @Override
                public void accept(ZooListener zooListener) {
                    try {
                        zooListener.onCreate(newValue.getFullPath(), data, isRoot ? ZooEvent.NodeCreated : ZooEvent.NodeChildrenAdded);
                    } catch (Exception ex) {
                        logger.error("Listener exception", ex);
                    }
                }
            });
        } else if (oldValue != null && newValue == null) {
            listeners.forEach(new Consumer<ZooListener>() {
                @Override
                public void accept(ZooListener zooListener) {
                    try {
                        zooListener.onDelete(oldValue.getFullPath(), isRoot ? ZooEvent.NodeDeleted : ZooEvent.NodeChildrenDeleted);
                    } catch (Exception ex) {
                        logger.error("Listener exception", ex);
                    }
                }
            });
        } else if (oldValue == null && newValue == null) {

        }
    }


    private class CheckListeners implements Runnable {
        @Override
        public void run() {
            logger.info("Start process");
            Map<String, ZPath> newData = new HashMap<>();
            data.forEach(new Refresh(newData));
            try {
                checkChanges(data, newData);
                lock.lock();
                try {
                    data.clear();
                    data.putAll(newData);
                } finally {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("Check listener exception", e);
            }

        }
    }

    private class Refresh implements BiConsumer<String, ZPath> {
        private Map<String, ZPath> newData;

        public Refresh(Map<String, ZPath> to) {
            newData = to;
        }

        @Override
        public void accept(String s, ZPath zPath) {
            try {
                newData.put(s, ZPath.getZPath(client, s, null));
            } catch (Exception e) {
                logger.error("Unexpected error", e);
            }
        }
    }


}
