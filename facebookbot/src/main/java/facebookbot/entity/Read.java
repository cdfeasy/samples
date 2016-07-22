package facebookbot.entity;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Read {

    private Long watermark;
    private Long seq;

    /**
     * @return The watermark
     */
    public Long getWatermark() {
        return watermark;
    }

    /**
     * @param watermark The watermark
     */
    public void setWatermark(Long watermark) {
        this.watermark = watermark;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Read{");
        sb.append("watermark=").append(watermark);
        sb.append(", seq=").append(seq);
        sb.append('}');
        return sb.toString();
    }
}
