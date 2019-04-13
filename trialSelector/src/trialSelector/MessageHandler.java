package trialSelector;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import static trialSelector.UtilityClass.*;

public class MessageHandler extends Thread implements PeerConstants {
	SocketChannel ch;
	ByteBuffer buffer;

	public MessageHandler(SocketChannel keyChannel,ByteBuffer buffer) {
		ch = keyChannel;
		this.buffer = buffer;
	}

	ConcurrentLinkedQueue<Object> messagesQueue = new ConcurrentLinkedQueue<Object>();

	public void run() {

		if (!messagesQueue.isEmpty()) {
			Object receivedMessage = messagesQueue.poll();
			System.out.println(messagesQueue.size());
			if (receivedMessage instanceof HandshakeMessage) {
				handleHandshakeMessage((HandshakeMessage) receivedMessage);

			} else if (receivedMessage instanceof Message) {
				Message msg = (Message) receivedMessage;
				switch (msg.getMessageType()) {
				case BITFIELD:
					System.out.println("Handle Bitfield Message");
					handleBitfieldMessage(msg);
					break;
				case INTERESTED:
					handleInterestedMessage(msg.PeerID);
					break;
				case NOT_INTERESTED:
					handleNotInterestedMessage(msg.PeerID);
					break;
				case CHOKE:
					handleChokeMessage(msg.PeerID);
					break;
				case UNCHOKE:
					handleUnchokeMessage(msg.PeerID);
					break;
				case HAVE:
					handleHaveMessage(msg);
					break;
				case REQUEST:
					handleRequestMessage(msg);
					break;
				case PIECE:
					handlePieceMessage(msg);
					break;
				default:
					try {
						throw new BitTorrentExceptions("Invalid message type");
					} catch (BitTorrentExceptions e) {
						// TODO Auto-generated catch block replace with log file
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void handleUnchokeMessage(int remotePeerID) {
		// TODO Auto-generated method stub
		System.out.println("Adding" + remotePeerID + " to " + currentPeerID + "Unchoked list");
		UtilityClass.unChokedPeers.add(remotePeerID);

	}

	private void handleChokeMessage(int remotePeerID) {
		// TODO Auto-generated method stub
		System.out.println("Removing" + remotePeerID + " to " + currentPeerID + "Interested list");
		UtilityClass.unChokedPeers.remove(remotePeerID);
	}

	private void handleHandshakeMessage(HandshakeMessage message) {
		try {
			// this.remotePeerID = (message).getPeerID();
			System.out.println(currentPeerID + " is polling for handshake messages ");
			System.out.println((message).getPeerID() + " is the peer ID I have to reply to");
			if (!allPeerMap.get(message.getPeerID()).isHandshakeSent) {
				allPeerMap.get(message.getPeerID()).peerSocketChannel = ch;
				HandshakeMessage reply = new HandshakeMessage(UtilityClass.currentPeerID);
				buffer = transformObject(reply);
				writeToChannel();
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
			System.out.println("we are here");
			sendInterested(message.PeerID);
		} else {
			System.out.println("we are here because we don't want to be there");
			sendNotInterested(message.PeerID);
		}

	}

	private void handleInterestedMessage(Integer remotePeerID) {
		System.out.println("Adding" + remotePeerID + " to " + currentPeerID + "Interested list");
		UtilityClass.intersetedPeers.add(remotePeerID);

	}

	private void handleNotInterestedMessage(Integer remotePeerID) {
		System.out.println("Removing" + remotePeerID + " to " + currentPeerID + "Interested list");
		UtilityClass.intersetedPeers.remove(remotePeerID);

	}

	private void sendBitfieldMessage(int remotePeerID) {
		try {
			System.out.println("Sending bitfield message from " + currentPeerID + " to " + remotePeerID);
			byte[] bitfieldByteArray = UtilityClass.allPeerMap.get(currentPeerID).bitfield.toByteArray();
			Message actualMessage = new Message(currentPeerID, BITFIELD);
			actualMessage.setMessageType(BITFIELD);
			actualMessage.messagePayload = bitfieldByteArray;
			buffer = transformObject(actualMessage);
			writeToChannel();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendInterested(int remotePeerID) {

		System.out.println(UtilityClass.currentPeerID + " sending Interested message to Peer " + remotePeerID);
		Message msg = new Message(UtilityClass.currentPeerID, INTERESTED);
		try {
			System.out.println("got till here");
			buffer = transformObject(msg);
			writeToChannel();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendNotInterested(int remotePeerID) {

		System.out.println(UtilityClass.currentPeerID + " sending Not-Interested message to Peer " + remotePeerID);
		Message msg = new Message(UtilityClass.currentPeerID, NOT_INTERESTED);
		try {
			buffer = UtilityClass.transformObject(msg);
			writeToChannel();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleHaveMessage(Message recievedMsg) {
		// TODO Auto-generated method stub

		if (recievedMsg.messagePayload != null) {
			byte[] payload = recievedMsg.messagePayload;
			ByteArrayInputStream inputStream = new ByteArrayInputStream(payload);
			inputStream.read(payload,0,4);
			PeerInfo remotePeer = allPeerMap.get(recievedMsg.PeerID);
			int index = ByteBuffer.wrap(payload).getInt();
			UtilityClass.allPeerMap.get(recievedMsg.PeerID).bitfield.set(index);
			if(UtilityClass.getCurrentPeerInfo().bitfield.get(index) == true)
			{
				System.out.println(recievedMsg.PeerID + " has nothing I don't got");
				sendNotInterested(recievedMsg.PeerID);	
			}
			else
			{
				System.out.println(recievedMsg.PeerID + " has an interesting piece, yummm yummm");
				sendInterested(recievedMsg.PeerID);			
			}
		}
		else 
		{
			try {
				throw new BitTorrentExceptions("Something wrong in have message, payload is missing");
			} catch (BitTorrentExceptions e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void handleRequestMessage(Message recievedMsg) {
		// TODO Auto-generated method stub

		if (recievedMsg.messagePayload != null) {
			byte[] payload = recievedMsg.messagePayload;
			ByteArrayInputStream inputStream = new ByteArrayInputStream(payload);
			inputStream.read(payload,0,4);
		}
		else 
		{
			try {
				throw new BitTorrentExceptions("Something wrong in request message, payload is missing");
			} catch (BitTorrentExceptions e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void handlePieceMessage(Message receivedMsg) {
		//
		if (receivedMsg.messagePayload != null) {
			byte[] payload = receivedMsg.messagePayload;
			byte[] pieceIndex = new byte[4];

			ByteArrayInputStream bufferInput = new ByteArrayInputStream(payload);
			bufferInput.read(pieceIndex,0,4);

			int in = ByteBuffer.wrap(pieceIndex).getInt();
			allPeerMap.get(currentPeerID).bitfield.set(in);
			allPeerMap.get(receivedMsg.PeerID).bitfield.set(in);

			byte[] part = Arrays.copyOfRange(payload, 4, payload.length - 4);
			FileOutputStream fileOutputStream;

			try {
				fileOutputStream = new FileOutputStream(System.getProperty("user.dir") + File.separator + "peer_" + UtilityClass.currentPeerID + File.separator + in + ".splitPart");
				fileOutputStream.write(part);
				fileOutputStream.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			//TO-DO - logging, once the choking unchoking works
			allPeerMap.keySet().forEach(peer-> {
				BitSet peerBitfield = allPeerMap.get(peer).bitfield;
				if(!peerBitfield.get(in)){
					//Send Have message
					byte[] havePayload = ByteBuffer.allocate(4).putInt(in).array();
					Message message = new Message(peer, HAVE);
					message.messagePayload = havePayload;
					try {
						ch.write(UtilityClass.transformObject(message));
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			});

		}
	}

	private void writeToChannel()
	{
		try {
			ch.write(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buffer.clear();
		buffer.flip();
	}

}
