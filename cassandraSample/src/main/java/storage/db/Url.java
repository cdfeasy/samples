package storage.db;

import java.util.Date;

/**
 * Created by d.asadullin on 31.08.2016.
 */
public class Url {
    private byte[] url;
    private byte[] hash;
    private Date created;

    public byte[] getUrl() {
        return url;
    }

    public void setUrl(byte[] url) {
        this.url = url;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
