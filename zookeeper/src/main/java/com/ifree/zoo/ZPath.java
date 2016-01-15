package com.ifree.zoo;

import com.ifree.zoo.client.ZooClient;
import org.apache.zookeeper.data.Stat;

import java.util.*;

/**
 * Created by dmitry on 20.12.2015.
 */
public class ZPath {
    private String name;
    private String fullPath;
    private ZPath parent;
    private List<ZPath> children;
    private long version;
    private long time;

    public ZPath(String name, String fullPath, ZPath parent) {
        this.name = name;
        this.fullPath = fullPath;
        this.parent = parent;
    }

    public static ZPath getZPath(String path) {
        String[] parts = path.split("/");
        ZPath cur = null;
        String fullName = "";
        for (int i = 0; i < parts.length; i++) {
            fullName=parts[i].length()>0?fullName+"/"+parts[i]:"";
            ZPath newPath = new ZPath(parts[i],fullName, cur);
            cur = newPath;
        }
        return cur;
    }
    public static ZPath getZPath(ZooClient client, String path,ZPath parent) throws Exception {
        String[] parts = path.split("/");
        String name=parts.length>=1?parts[parts.length-1]:"";
        Stat stat=client.getStat(path);
        if(stat==null){
            return null;
        }
        ZPath cur = new ZPath(name,path,parent);
        cur.version=stat.getVersion();
        cur.time=stat.getMtime();
        List<ZPath> ch = new ArrayList<>(stat.getNumChildren());
        if(stat.getNumChildren()>0) {
            List<String> children = client.getChildren(path).get();
            for (String s : children) {
                ZPath child = getZPath(client, (path.length() > 1 ? path + "/" + s : "/" + s), cur);
                if (child != null) {
                    ch.add(child);
                }
            }
        }
        cur.children=ch;
        return cur;
    }


    public static void main(String[] args){
        ZPath path=ZPath.getZPath("/a/b");
        System.out.println(path);

    }

    public ZPath findZpath(String path){
        if(path.length()<fullPath.length()){
            return null;
        }
        if(path.equals(this.getFullPath())){
            return this;
        }
        for(ZPath p:getChildren()){
            ZPath find=p.findZpath(path);
            if(find!=null){
                return find;
            }
        }
        return null;
    }
    public static Map<String,ZPath> toFlatMap(ZPath path){
        List<ZPath> list=new ArrayList<>();
        if(path==null){
            return new HashMap<>();
        }
        list.add(path);
        int cur=0;
        int newCur=1;
        while (true){
            newCur=list.size();
            for(int i=cur;i<newCur;i++) {
                addAllChildrens(list,list.get(i));
            }
            if(list.size()==newCur){
                break;
            }
            cur=newCur;
        }
        Collections.sort(list, new Comparator<ZPath>() {
            @Override
            public int compare(ZPath o1, ZPath o2) {
                return o1.getFullPath().compareTo(o2.getFullPath());
            }
        });
        Map<String,ZPath> map=new TreeMap<>();
        list.forEach((zpath)->map.put(zpath.getFullPath(),zpath));
        return map;
    }
    private static void addAllChildrens(List<ZPath> list,ZPath p){
        if(p.children!=null){
            list.addAll(p.getChildren());
        }
    }

    public String getName() {
        return name;
    }

    public String getFullPath() {
        return fullPath;
    }

    public ZPath getParent() {
        return parent;
    }

    public List<ZPath> getChildren() {
        return children;
    }

    public long getVersion() {
        return version;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "ZPath{" +
                "name='" + name + '\'' +
                ", fullPath='" + fullPath + '\'' +
                ", parent=" + parent +
                ", version=" + version +
                ", children count[" + (children!=null?children.size():0) +"]"+
                '}';
    }

    public String toTree() {
        StringBuilder sb=new StringBuilder();
        sb.append("[");
        if(children!=null) {
            for (ZPath path : children) {
               sb.append(path.toTree());
            }
        }
        sb.append("]");
        return "ZPath{" +
                " fullPath='" + fullPath + '\'' +
                ", version=" + version +
                ", children=" + sb.toString() +
                '}';
    }
}
