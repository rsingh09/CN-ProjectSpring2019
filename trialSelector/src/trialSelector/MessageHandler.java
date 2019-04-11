package trialSelector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

import static trialSelector.UtilityClass.transformObject;

public class MessageHandler extends Thread implements PeerConstants {
	SocketChannel ch;
	int remotePeerID;
	public MessageHandler(SocketChannel keyChannel) {
		ch = keyChannel;
	}

	ConcurrentLinkedQueue<Object> messagesQueue = new ConcurrentLinkedQueue<Object>();

	public void run() {

		if (!messagesQueue.isEmpty()) {
			Object receivedMessage = messagesQueue.poll();
			System.out.println(receivedMessage instanceof HandshakeMessage);

			if (receivedMessage instanceof HandshakeMessage) {
				System.out.println("Message Handler Starts polling for messages");
				handleHandshakeMessage((HandshakeMessage) receivedMessage);
			} else if (receivedMessage instanceof Message) {
				System.out.println("Listening to the Messages now");
				handleMessage((Message) receivedMessage);
			}

		}
	}

	private void handleMessage(Message receivedMessage) {
		switch (receivedMessage.MessageType) {
		case CHOKE:
			// Change peer state
			UtilityClass.getCurrentPeerInfo().peerState = PEER_CHOKED;
			break;
		case UNCHOKE:
			UtilityClass.getCurrentPeerInfo().peerState = PEER_UNCHOKED;
			// Change peer state
		case INTERESTED:
			handleInterested();
			break;
		case NOT_INTERESTED:
			handleNotInterested();
		case HAVE:
			handleHave();
			break;
		case BITFIELD:
			handleBitfield();
		case REQUEST:
			handleRequest();
			break;
		case PIECE:
			handlePiece();
		}
	}

	private void handlePiece() {
		// TODO Auto-generated method stub
		//send after request message

	}

	private void handleRequest() {
		// TODO Auto-generated method stub
		//send after unchoke message

	}

	private void handleBitfield() {
		// TODO Auto-generated method stub

	}

	private void handleHave() {
		// TODO Auto-generated method stub

	}

	private void handleNotInterested() {
		// TODO Auto-generated method stub

	}

	private void handleInterested() {
		// TODO Auto-generated method stub

	}

	private void handleHandshakeMessage(HandshakeMessage message) {
		UtilityClass.allPeerMap.get(message.getPeerID()).isHandshakeCompleted = true;
		HandshakeMessage reply = new HandshakeMessage(UtilityClass.currentPeerID);
		try {
			System.out.println("Peer ID " + message.getPeerID());
			System.out.println("Client Socket Channel should be the same I printed before" + ch);
			ch.write(transformObject(reply));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendUnChoke() {
        //create choke unchoke thread
		System.out.println(UtilityClass.currentPeerID + " sending UNCHOKE message to Peer " + remotePeerID);
		Message msg = new Message(UtilityClass.currentPeerID, UNCHOKE);
		try {
			ch.write(UtilityClass.transformObject(msg));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// SendData(ch, msgByte);
	}

	private void sendChoke() {
		//send from choke/unchoke thread
		System.out.println(UtilityClass.currentPeerID + " sending CHOKE message to Peer " + remotePeerID);
		Message d = new Message(UtilityClass.currentPeerID, CHOKE);
		try {
			ch.write(UtilityClass.transformObject(d));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void sendInterested() {
            //send after receiving bitfield and checking if you don't have that any piece or after receiving have message vice versa
		System.out.println(UtilityClass.currentPeerID + " sending UNCHOKE message to Peer " + remotePeerID);
		Message msg = new Message(UtilityClass.currentPeerID, INTERESTED);
		try {
			ch.write(UtilityClass.transformObject(msg));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// SendData(ch, msgByte);
	}

	private void sendNotInterested() {
		//after receiving have message
		System.out.println(UtilityClass.currentPeerID + " sending CHOKE message to Peer " + remotePeerID);
		Message msg = new Message(UtilityClass.currentPeerID, NOT_INTERESTED);
		try {
			ch.write(UtilityClass.transformObject(msg));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
