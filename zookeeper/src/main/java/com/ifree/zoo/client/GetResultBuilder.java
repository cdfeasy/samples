package com.ifree.zoo.client;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;

/**
 * Created by d.asadullin on 14.01.2016.
 */
public class GetResultBuilder {
    public static class Result<T>{
        private byte[] bytes;
        private T object;
        private Stat stat;
        public Result(byte[] bytes, T object, Stat stat) {
            this.bytes = bytes;
            this.object = object;
            this.stat = stat;
        }
        public byte[] getBytes() {
            return bytes;
        }
        public T getObject() {
            return object;
        }
        public Stat getStat() {
            return stat;
        }
    }
    private String path;
    private CuratorFramework client;
    private Class toClass;
    private ZooSerializer zooSerializer;

    public GetResultBuilder(String path,CuratorFramework client, ZooSerializer zooSerializer) {
        this.path = path;
        this.client=client;
        this.zooSerializer=zooSerializer;
    }

    public GetResultBuilder setZooSerializer(ZooSerializer zooSerializer) {
        this.zooSerializer = zooSerializer;
        return this;
    }

    public  <T>GetResultBuilder setToClass(Class<T> toClass) {
        this.toClass = toClass;
        return this;
    }
    public byte[] getBytes() throws Exception {
        Stat stat = client.checkExists().forPath(path);
        if (stat == null) {
            return null;
        }
        return client.getData().forPath(path);
    }
    public  <T>T getObject() throws Exception {
        if(toClass==null){
            throw new Exception("Empty toClass param in GetResultBuilder for path "+path);
        }
        return (T) getResult().getObject();
    }
    public Result getResult() throws Exception {
        Stat stat = client.checkExists().forPath(path);
        if (stat == null) {
            return null;
        }
        byte[] bytes=client.getData().forPath(path);
        if(toClass==null){
            return new Result(bytes, null, stat);
        }  else {
            return new Result(bytes, zooSerializer.fromBytes(bytes, toClass), stat);
        }
    }

}
