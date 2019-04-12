package trialSelector;

import java.io.Serializable;

public class Message implements Serializable, PeerConstants {
	int PeerID;
	int messageLength;
	MessageTypes msgType;
	byte[] messagePayload;

	public Message(int peerID, MessageTypes messageType) {
		super();
		PeerID = peerID;
		//this.messageLength = messageLength;
		msgType = messageType;
		this.messagePayload = null;
	}
	public Message(MessageTypes messageTypes){
		msgType = messageTypes;
	}

	public int getMessageLength() {
		return messageLength;
	}

	public void setMessageLength(int messageLength) {
		this.messageLength = messageLength;
	}

	public MessageTypes getMessageType() {
		return msgType;
	}

	public void setMessageType(MessageTypes messageType) {
		msgType = messageType;
	}

	public byte[] getMessagePayload() {
		return messagePayload;
	}

	public void setMessagePayload(byte[] messagePayload) {
		this.messagePayload = messagePayload;
	}

}
