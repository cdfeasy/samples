package com.ifree.zoo.listener;

import com.ifree.zoo.ZooEvent;

/**
 * Created by d.asadullin on 23.11.2015.
 */
public interface ZooListener {
    boolean onDelete(String path,ZooEvent type);
    boolean onChange(String path,byte[] data,ZooEvent type);
}
