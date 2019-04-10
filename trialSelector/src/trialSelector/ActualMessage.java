package trialSelector;

import java.io.Serializable;

public class ActualMessage implements Serializable 
{
	    int messageLength;
	    MessageTypes MessageType;
	    byte[] messagePayload;

	    public int getMessageLength() 
	    {
	        return messageLength;
	    }

	    public void setMessageLength(int messageLength)
	    {
	        this.messageLength = messageLength;
	    }

	    public MessageTypes getMessageType() 
	    {
	        return MessageType;
	    }

	    public void setMessageType(MessageTypes messageType)
	    {
	        MessageType = messageType;
	    }

	    public byte[] getMessagePayload() 
	    {
	        return messagePayload;
	    }

	    public void setMessagePayload(byte[] messagePayload) 
	    {
	        this.messagePayload = messagePayload;
	    }
	    
}
