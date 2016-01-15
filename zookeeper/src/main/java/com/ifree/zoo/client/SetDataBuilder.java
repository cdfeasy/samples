package com.ifree.zoo.client;

import org.apache.curator.framework.CuratorFramework;

/**
 * Created by d.asadullin on 14.01.2016.
 */
public class SetDataBuilder {
    private ZooSerializer zooSerializer;
    private CuratorFramework client;
    private String path;
    protected byte[] bytes;
    protected Object object;
    protected int version=-1;

    public SetDataBuilder(String path,CuratorFramework client,ZooSerializer zooSerializer) {
        this.path = path;
        this.zooSerializer=zooSerializer;
        this.client=client;
    }

    public SetDataBuilder setZooSerializer(ZooSerializer zooSerializer) {
        this.zooSerializer = zooSerializer;
        return this;
    }

    public SetDataBuilder setBytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    public SetDataBuilder setObject(Object object) {
        this.object = object;
        return this;
    }

    public SetDataBuilder setVersion(int version) {
        this.version = version;
        return this;
    }
    public void set() throws Exception{
        if(bytes!=null){
        } else if(object!=null){
            bytes=zooSerializer.getBytes(object);
        } else{
            throw new Exception("Empty bytes and object fields in SetDataBuilder for path "+path);
        }
        if(version>=0){
            client.setData().withVersion(version).forPath(path,bytes);
        } else{
            client.setData().forPath(path,bytes);
        }
    }
}
