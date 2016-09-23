package storage.db;

import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by d.asadullin on 31.08.2016.
 */
public class KFile {
    private byte[] hash;
    private String filename;
    private byte[]  data;
    private Integer parts;
    private Integer type ;
    private Integer size;
    private Date created;

    public KFile(byte[] hash, String filename, byte[] data, Integer parts, Integer type, Integer size, Date created) {
        this.hash = hash;
        this.filename = filename;
        this.data = data;
        this.parts = parts;
        this.type = type;
        this.size = size;
        this.created = created;
    }

    public KFile() {
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Integer getParts() {
        return parts;
    }

    public void setParts(Integer parts) {
        this.parts = parts;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "{\"File\":{" +
                "\"hash\":" + (DatatypeConverter.printHexBinary(hash)) +
                ", \"filename\":" + (getFilename() != null ? "\"" + getFilename() + "\"" : "null") +
                ", \"parts\":" + (getParts() != null ? "\"" + getParts() + "\"" : "null") +
                ", \"type\":" + (getType() != null ? "\"" + getType() + "\"" : "null") +
                ", \"size\":" + (getSize() != null ? "\"" + getSize() + "\"" : "null") +
                ", \"created\":" + (getCreated() != null ? "\"" + getCreated() + "\"" : "null") +
                "}}";
    }
}
