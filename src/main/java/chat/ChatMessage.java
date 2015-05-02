package chat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Created by chetan.k on 5/1/15.
 */
public class ChatMessage implements Serializable{
    private String sender;
    private String chatId;
    private String message;
    private long timestamp;

    public ChatMessage(String sender, String chatId, String message) {
        this.sender = sender;
        this.chatId = chatId;
        this.message = message;
        this.timestamp = System.nanoTime();
    }

    public ChatMessage() {
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ChatMessage)) {
            return false;
        }

        ChatMessage that = (ChatMessage) o;

        return new EqualsBuilder()
                .append(timestamp, that.timestamp)
                .append(sender, that.sender)
                .append(chatId, that.chatId)
                .append(message, that.message)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(sender)
                .append(chatId)
                .append(message)
                .append(timestamp)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("sender", sender)
                .append("chatId", chatId)
                .append("message", message)
                .append("timestamp", timestamp)
                .toString();
    }
}
