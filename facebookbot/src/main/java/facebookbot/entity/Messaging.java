package facebookbot.entity;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Messaging {

    private Sender sender;
    private Recipient recipient;
    private Long timestamp;
    private Message message;
    private Postback postback;
    private Read read;

    /**
     * @return The sender
     */
    public Sender getSender() {
        return sender;
    }

    /**
     * @param sender The sender
     */
    public void setSender(Sender sender) {
        this.sender = sender;
    }

    /**
     * @return The recipient
     */
    public Recipient getRecipient() {
        return recipient;
    }

    /**
     * @param recipient The recipient
     */
    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    /**
     * @return The timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp The timestamp
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return The message
     */
    public Message getMessage() {
        return message;
    }

    /**
     * @param message The message
     */
    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * @return The read
     */
    public Read getRead() {
        return read;
    }

    /**
     * @param read The read
     */
    public void setRead(Read read) {
        this.read = read;
    }

    public Postback getPostback() {
        return postback;
    }

    public void setPostback(Postback postback) {
        this.postback = postback;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Messaging{");
        sb.append("sender=").append(sender);
        sb.append(", recipient=").append(recipient);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", message=").append(message);
        sb.append(", postback=").append(postback);
        sb.append(", read=").append(read);
        sb.append('}');
        return sb.toString();
    }
}
