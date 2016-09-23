package storage.db;

import java.util.Date;

/**
 * Created by d.asadullin on 12.09.2016.
 */
public class UrlCall {
    private byte[] url;
    private Date created;
    private String env;

    public byte[] getUrl() {
        return url;
    }

    public void setUrl(byte[] url) {
        this.url = url;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }
}
