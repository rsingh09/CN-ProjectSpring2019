package trialSelector;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.BitSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import static trialSelector.UtilityClass.*;

public class MessageHandler extends Thread implements PeerConstants {
	SocketChannel ch;

	public MessageHandler(SocketChannel keyChannel) {
		ch = keyChannel;
	}

	ConcurrentLinkedQueue<Object> messagesQueue = new ConcurrentLinkedQueue<Object>();

	public void run() {

		if (!messagesQueue.isEmpty()) {
			Object receivedMessage = messagesQueue.poll();
			System.out.println(messagesQueue.size());
			if (receivedMessage instanceof HandshakeMessage) {
				handleHandshakeMessage((HandshakeMessage) receivedMessage);

			} else if (receivedMessage instanceof Message) {
				switch (((Message) receivedMessage).getMessageType()) {
				case BITFIELD:
					System.out.println("Handle Bitfield Message");
					handleBitfieldMessage((Message) receivedMessage);
					break;

				case INTERESTED:
					handleInterestedMessage();
					break;

				}
			}
		}
	}

	private void handleHandshakeMessage(HandshakeMessage message) {
		try {
			//this.remotePeerID = (message).getPeerID();
			System.out.println(currentPeerID + " is polling for handshake messages ");
			System.out.println((message).getPeerID() + " is the peer ID I have to reply to");
			if (!allPeerMap.get(message.getPeerID()).isHandshakeSent) {
				HandshakeMessage reply = new HandshakeMessage(UtilityClass.currentPeerID);
				ch.write(transformObject(reply));
				allPeerMap.get(message.getPeerID()).isHandshakeSent = true;
				// Send Bitfield messsage only if my Bitset is not empty
				if (!allPeerMap.get(currentPeerID).bitfield.isEmpty()) {
					sendBitfieldMessage((message).getPeerID());

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleBitfieldMessage(Message message) {

		System.out.println(currentPeerID + " is handling for Bitfield messages ");
		if (message.messagePayload != null) {
			PeerInfo remotePeer = allPeerMap.get(message.PeerID);
			remotePeer.bitfield = ((BitSet) BitSet.valueOf(message.messagePayload).clone());
			allPeerMap.put(message.PeerID, remotePeer);
		}

		int splitParts = totalSplitParts;

		System.out.println("The number of split parts are " + splitParts);

		BitSet currentPeerBitset = new BitSet(splitParts);
		currentPeerBitset = (BitSet) allPeerMap.get(currentPeerID).bitfield.clone();

		BitSet peerBitset = new BitSet(splitParts);
		peerBitset = (BitSet) BitSet.valueOf(message.messagePayload).clone();

		currentPeerBitset.flip(0, totalSplitParts);
		currentPeerBitset.and(peerBitset);
		if (!currentPeerBitset.isEmpty()) {
			sendInterested(message.PeerID);
		} else {
			sendNotInterested(message.PeerID);
		}

	}

	private void handleInterestedMessage() {

	}

	private void sendBitfieldMessage(int remotePeerID) {
		try {
			System.out.println("Sending bitfield message from " + currentPeerID + " to " + remotePeerID);
			byte[] bitfieldByteArray = UtilityClass.allPeerMap.get(currentPeerID).bitfield.toByteArray();
			Message actualMessage = new Message(currentPeerID, BITFIELD);
			actualMessage.setMessageType(BITFIELD);
			actualMessage.messagePayload = bitfieldByteArray;
			ch.write(transformObject(actualMessage));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendInterested(int remotePeerID) {

		System.out.println(UtilityClass.currentPeerID + " sending Interested message to Peer " + remotePeerID);
		Message msg = new Message(UtilityClass.currentPeerID, INTERESTED);
		try {
			ch.write(UtilityClass.transformObject(msg));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// SendData(ch, msgByte);
	}

	private void sendNotInterested(int remotePeerID) {

		System.out.println(UtilityClass.currentPeerID + " sending Not-Interested message to Peer " + remotePeerID);
		Message msg = new Message(UtilityClass.currentPeerID, NOT_INTERESTED);
		try {
			ch.write(UtilityClass.transformObject(msg));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// SendData(ch, msgByte);
	}

}
