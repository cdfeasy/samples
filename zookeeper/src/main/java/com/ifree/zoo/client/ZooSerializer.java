package com.ifree.zoo.client;


/**
 * Created by dmitry on 07.01.2016.
 */
public interface ZooSerializer {
    byte[] getBytes(Object object) throws Exception;
    <T>T fromBytes(byte[] bytes,Class<T> toClass) throws Exception;
}
