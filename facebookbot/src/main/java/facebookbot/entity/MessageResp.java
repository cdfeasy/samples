package facebookbot.entity;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@Generated("org.jsonschema2pojo")
public class MessageResp {

    private String object;
    private List<Entry> entry = new ArrayList<Entry>();

    /**
     * @return The object
     */
    public String getObject() {
        return object;
    }

    /**
     * @param object The object
     */
    public void setObject(String object) {
        this.object = object;
    }

    /**
     * @return The entry
     */
    public List<Entry> getEntry() {
        return entry;
    }

    /**
     * @param entry The entry
     */
    public void setEntry(List<Entry> entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageResp{");
        sb.append("object='").append(object).append('\'');
        sb.append(", entry=").append(entry);
        sb.append('}');
        return sb.toString();
    }
}
