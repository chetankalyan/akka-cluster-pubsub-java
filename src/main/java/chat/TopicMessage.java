package chat;

/**
 * Created by chetan.k on 5/1/15.
 */
public class TopicMessage {
    public enum MessageType{
        SUBSCRIBE,
        UNSUBSCRIBE
    }
    private MessageType messageType;
    private String chatId;

    public TopicMessage(MessageType messageType, String chatId) {
        this.messageType = messageType;
        this.chatId = chatId;
    }

    public TopicMessage() {
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TopicMessage)) {
            return false;
        }

        TopicMessage that = (TopicMessage) o;

        if (messageType != that.messageType) {
            return false;
        }
        return !(chatId != null ? !chatId.equals(that.chatId) : that.chatId != null);

    }

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this)
                .append("messageType", messageType)
                .append("chatId", chatId)
                .toString();
    }

    @Override
    public int hashCode() {
        int result = messageType != null ? messageType.hashCode() : 0;
        result = 31 * result + (chatId != null ? chatId.hashCode() : 0);
        return result;
    }
}
