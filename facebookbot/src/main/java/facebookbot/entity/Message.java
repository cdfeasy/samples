package facebookbot.entity;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Message {
    private String mid;
    private Long seq;
    private String text;

    /**
     * @return The mid
     */
    public String getMid() {
        return mid;
    }

    /**
     * @param mid The mid
     */
    public void setMid(String mid) {
        this.mid = mid;
    }

    /**
     * @return The seq
     */
    public Long getSeq() {
        return seq;
    }

    /**
     * @param seq The seq
     */
    public void setSeq(Long seq) {
        this.seq = seq;
    }

    /**
     * @return The text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text The text
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("mid='").append(mid).append('\'');
        sb.append(", seq=").append(seq);
        sb.append(", text='").append(text).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
