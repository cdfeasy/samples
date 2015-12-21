package ru.cdf.zoo.listener;

import ru.cdf.zoo.ZooEvent;

/**
 * Created by d.asadullin on 23.11.2015.
 */
public interface ZooListener {
    boolean onDelete(String path,ZooEvent type);
    boolean onCreate(String path,byte[] data,ZooEvent type);
    boolean onChildChanged(String path,byte[] data,ZooEvent type);
    boolean onChange(String path,byte[] data,ZooEvent type);
}
