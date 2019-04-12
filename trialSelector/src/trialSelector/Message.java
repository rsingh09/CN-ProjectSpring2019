package trialSelector;

import java.io.Serializable;

public class Message implements Serializable, PeerConstants {
	int PeerID;
	int messageLength;
	int msgType;
	byte[] messagePayload;

	public Message(int peerID, int messageType) {
		super();
		PeerID = peerID;
		//this.messageLength = messageLength;
		msgType = messageType;
		this.messagePayload = null;
	}
	public Message(int messageTypes){
		msgType = messageTypes;
	}

	public int getMessageLength() {
		return messageLength;
	}

	public void setMessageLength(int messageLength) {
		this.messageLength = messageLength;
	}

	public int getMessageType() {
		return msgType;
	}

	public void setMessageType(int messageType) {
		msgType = messageType;
	}

	public byte[] getMessagePayload() {
		return messagePayload;
	}

	public void setMessagePayload(byte[] messagePayload) {
		this.messagePayload = messagePayload;
	}

}
