package facebookbot.entity;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Postback {
    private String payload;

    /**
     * @return The payload
     */
    public String getPayload() {
        return payload;
    }

    /**
     * @param payload The payload
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "{\"Postback\":{" +
                "\"payload\":" + getPayload() != null ? "\"" + getPayload() + "\"" : "null" +
                "}}";
    }
}
