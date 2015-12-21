package ru.cdf.zoo;

/**
 * Created by dmitry on 20.12.2015.
 */
public class ZPath {
    private String name;
    private String fullPath;
    private ZPath parent;

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

    public static void main(String[] args){
        ZPath path=ZPath.getZPath("/a/b");
        System.out.println(path);

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

    @Override
    public String toString() {
        return "ZPath{" +
                "name='" + name + '\'' +
                ", fullPath='" + fullPath + '\'' +
                ", parent=" + parent +
                '}';
    }
}
