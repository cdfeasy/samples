package facebookbot.entity;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@Generated("org.jsonschema2pojo")
public class Entry {
    private String id;
    private Long time;
    private List<Messaging> messaging = new ArrayList<Messaging>();

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The time
     */
    public Long getTime() {
        return time;
    }

    /**
     * @param time The time
     */
    public void setTime(Long time) {
        this.time = time;
    }

    /**
     * @return The messaging
     */
    public List<Messaging> getMessaging() {
        return messaging;
    }

    /**
     * @param messaging The messaging
     */
    public void setMessaging(List<Messaging> messaging) {
        this.messaging = messaging;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Entry{");
        sb.append("id='").append(id).append('\'');
        sb.append(", time=").append(time);
        sb.append(", messaging=").append(messaging);
        sb.append('}');
        return sb.toString();
    }
}
