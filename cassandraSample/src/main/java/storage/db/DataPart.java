package storage.db;

/**
 * Created by d.asadullin on 31.08.2016.
 */
public class DataPart {
    private byte[] hash;
    private Integer partId;
    private byte[]  data;

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
