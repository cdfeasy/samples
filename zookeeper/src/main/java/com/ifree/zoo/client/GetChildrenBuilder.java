package com.ifree.zoo.client;

import org.apache.curator.framework.CuratorFramework;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by d.asadullin on 13.01.2016.
 */
public class GetChildrenBuilder {
    private String path;
    private CuratorFramework client;
    private boolean fullPath=false;
    private Comparator<String> comparator;
    private String prefix;
    public GetChildrenBuilder(String path,CuratorFramework client){
        this.path = path;this.client=client;
    }
    public GetChildrenBuilder setFullPath(boolean fullPath) {
        this.fullPath = fullPath;
        return this;
    }

    public GetChildrenBuilder setComparator(Comparator<String> comparator) {
        this.comparator = comparator;
        return this;
    }

    public GetChildrenBuilder setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String getPath() {
        return path;
    }

    public boolean isFullPath() {
        return fullPath;
    }

    public Comparator<String> getComparator() {
        return comparator;
    }

    public String getPrefix() {
        return prefix;
    }

    public List<String> get() throws Exception{
        List<String> result = client.getChildren().forPath(path);
        if (getPrefix() != null) {
            result = result.stream().filter((a) -> a.startsWith(getPrefix())).map((s) -> isFullPath() ? (path + "/" + s) : s).collect(Collectors.toList());
        }
        if (getComparator() != null) {
            Collections.sort(result, getComparator());
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GetChildrenBuilder{");
        sb.append("path='").append(path).append('\'');
        sb.append(", fullPath=").append(fullPath);
        sb.append(", comparator=").append(comparator);
        sb.append(", prefix='").append(prefix).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
