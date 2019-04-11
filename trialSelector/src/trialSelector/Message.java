package trialSelector;

import java.io.Serializable;

public class Message implements Serializable, PeerConstants {
	int PeerID;
	int messageLength;
	int MessageType;
	byte[] messagePayload;
	
	public Message(int peerID, int messageType) {
		super();
		PeerID = peerID;
		//this.messageLength = messageLength;
		MessageType = messageType;
		this.messagePayload = null;
	}

	public int getMessageLength() {
		return messageLength;
	}

	public void setMessageLength(int messageLength) {
		this.messageLength = messageLength;
	}

	public int getMessageType() {
		return MessageType;
	}

	public void setMessageType(int messageType) {
		MessageType = messageType;
	}

	public byte[] getMessagePayload() {
		return messagePayload;
	}

	public void setMessagePayload(byte[] messagePayload) {
		this.messagePayload = messagePayload;
	}

}
