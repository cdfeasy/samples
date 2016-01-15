package com.ifree.zoo.listener;

import java.io.IOException;

/**
 * Created by dmitry on 20.12.2015.
 */
public interface ListenerProcessor {
    void start() throws Exception;
    void stop() throws IOException;
    void registerListener(String path, ZooListener listener, boolean checkChildren);
    void removeListener(ZooListener listener);
}
