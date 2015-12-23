package ru.cdf.zoo;

import org.apache.zookeeper.data.Stat;
import ru.cdf.zoo.client.ZooClient;

import java.util.List;

/**
 * Created by dmitry on 20.12.2015.
 */
public class ZPath {
    private String name;
    private String fullPath;
    private ZPath parent;
    private ZPath[] children;
    private long version;

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
        ZPath cur = new ZPath(name,path,parent);
        cur.version=stat.getVersion();
        List<String> children=client.getChildren(path);
        ZPath[] ch=new ZPath[children.size()];
        int i=0;
        for(String s:children){
            ZPath child=getZPath(client,(path.length()>1?path+"/"+s:"/"+s),cur);
            ch[i]=child;
            i++;
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

    public String getName() {
        return name;
    }

    public String getFullPath() {
        return fullPath;
    }

    public ZPath getParent() {
        return parent;
    }

    public ZPath[] getChildren() {
        return children;
    }

    public long getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "ZPath{" +
                "name='" + name + '\'' +
                ", fullPath='" + fullPath + '\'' +
                ", parent=" + parent +
                ", version=" + version +
                ", children count[" + (children!=null?children.length:0) +"]"+
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
