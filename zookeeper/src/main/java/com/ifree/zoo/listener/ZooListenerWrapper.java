package com.ifree.zoo.listener;

import com.ifree.zoo.ZooEvent;

/**
 * Created by d.asadullin on 12.01.2016.
 */
public class ZooListenerWrapper implements ZooListener {
    private boolean checkChildren;
    private ZooListener zooListener;
    public ZooListenerWrapper(ZooListener listener,boolean checkChildren){
        this.zooListener=listener;
        this.checkChildren=checkChildren;
    }

    public boolean isCheckChildren() {
        return checkChildren;
    }

    @Override
    public boolean onDelete(String path, ZooEvent type) {
        return zooListener.onDelete(path,type);
    }

    @Override
    public boolean onChange(String path, byte[] data, ZooEvent type) {
        return zooListener.onChange(path,data,type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ZooListenerWrapper)) return false;

        ZooListenerWrapper wrapper = (ZooListenerWrapper) o;

        if (!zooListener.equals(wrapper.zooListener)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return zooListener.hashCode();
    }
}
