
/*
*Stores the message information
*/
public class Message {

    int messageLength;
    MessageTypes MessageType;
    String messagePayload;

    public int getMessageLength() {
        return messageLength;
    }

    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    public MessageTypes getMessageType() {
        return MessageType;
    }

    public void setMessageType(MessageTypes messageType) {
        MessageType = messageType;
    }

    public String getMessagePayload() {
        return messagePayload;
    }

    public void setMessagePayload(String messagePayload) {
        this.messagePayload = messagePayload;
    }
    

}
