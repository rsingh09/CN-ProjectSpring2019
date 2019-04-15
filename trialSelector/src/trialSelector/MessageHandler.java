package trialSelector;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import static trialSelector.UtilityClass.*;

public class MessageHandler extends Thread implements PeerConstants {
	private static BitTorrentLogger logger = BitTorrentLogger.getInstance();
	private SocketChannel socketChannel;
	private ByteBuffer buffer;
	ConcurrentLinkedQueue<Object> messagesQueue = new ConcurrentLinkedQueue<Object>();

	public MessageHandler(SocketChannel keyChannel, ByteBuffer buffer) {
		socketChannel = keyChannel;
		this.buffer = buffer;
	}

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
						logger.log(e.getMessage(), Level.SEVERE);
					}
				}
			}
		}
	}

	private void handleUnchokeMessage(int remotePeerID) {
//		System.out.println("Adding" + remotePeerID + " to " + currentPeerID + "Unchoked list");
//		UtilityClass.unChokedPeers.add(remotePeerID);
		logger.log("Size of interestedPeers list after handling unchoke msg: " + UtilityClass.unChokedPeers.size(),
				Level.WARNING);
		sendRequestMessage(remotePeerID);

	}

	private void sendRequestMessage(int remotePeerId) {
		int noOfsplits = totalSplitParts;
		PeerInfo currentPeer = allPeerMap.get(currentPeerID);

		BitSet currentPeerBitset = new BitSet(noOfsplits);
		currentPeerBitset = (BitSet) currentPeer.bitfield.clone();

		logger.log("Current peer " + currentPeer.peerID + " bitset is " + currentPeerBitset, Level.INFO);

		BitSet remotePeerBitset = new BitSet(noOfsplits);
		remotePeerBitset = (BitSet) allPeerMap.get(remotePeerId).bitfield.clone();

		currentPeerBitset.flip(0, noOfsplits);

		currentPeerBitset.and(remotePeerBitset);

		logger.log("After flipping and AND is : " + currentPeerBitset, Level.INFO);

		logger.log(currentPeerID + " is sending the request message now", Level.INFO);

		Random rand = new Random();

		if (!currentPeerBitset.isEmpty()) {
			while (true) {
				int randIndex = rand.nextInt(noOfsplits);
				if (currentPeerBitset.get(randIndex)) {
					logger.log("The index of the piece I am requesting is " + randIndex, Level.INFO);
					requestedPieces.add(randIndex);
					byte[] payload = ByteBuffer.allocate(4).putInt(randIndex).array();
					System.out.println(ByteBuffer.wrap(payload).getInt());

					Message msg = new Message(UtilityClass.currentPeerID, REQUEST);
					msg.messagePayload = payload;

					try {
						buffer = transformObject(msg);
						writeToChannel();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.log(e.getMessage(), Level.SEVERE);
					}

					break;
				}
			}
		}
	}

	private void handleChokeMessage(int remotePeerID) {
		// TODO Auto-generated method stub
		System.out.println("Removing" + remotePeerID + " to " + currentPeerID + "Interested list");
		UtilityClass.unChokedPeers.remove(remotePeerID);
	}

	private void handleHandshakeMessage(HandshakeMessage message) {
		try {
			// this.remotePeerID = (message).getPeerID();
			System.out.println(currentPeerID + " sending handshake to:  " + (message).getPeerID());
//            System.out.println((message).getPeerID() + " is the peer ID I have to reply to");
			if (!allPeerMap.get(message.getPeerID()).isHandshakeSent) {
				allPeerMap.get(message.getPeerID()).peerSocketChannel = socketChannel;
				HandshakeMessage reply = new HandshakeMessage(UtilityClass.currentPeerID);
				buffer = transformObject(reply);
				writeToChannel();
				allPeerMap.get(message.getPeerID()).isHandshakeSent = true;
				// Send Bitfield messsage only if my Bitset is not empty
				if (!allPeerMap.get(currentPeerID).bitfield.isEmpty()) {
					System.out.println(currentPeerID + " sending bitfield to:  " + (message).getPeerID());
					sendBitfieldMessage((message).getPeerID());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleBitfieldMessage(Message message) {

		System.out.println(currentPeerID + " is handling for Bitfield messages from: " + message.PeerID);
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
			System.out.println(currentPeerID + "doesn't have these parts control to interested to: " + message.PeerID);
			sendInterested(message.PeerID);
		} else {
			// System.out.println("we are here because we don't want to be there");
			System.out.println(currentPeerID + "have these parts control to not - interested" + message.PeerID);
			sendNotInterested(message.PeerID);
		}

	}

	private void handleInterestedMessage(Integer remotePeerID) {
		logger.log("Adding" + remotePeerID + " to " + currentPeerID + "Interested list", Level.INFO);
		System.out.println("Adding" + remotePeerID + " to " + currentPeerID + "Interested list");
		UtilityClass.intersetedPeers.add(remotePeerID);
		logger.log("Sending unchoke msg to peer: " + remotePeerID, Level.INFO);
		allPeerMap.get(remotePeerID).peerState = PeerState.UNCHOKED;
		sendUnchokeMessage(remotePeerID);
		unChokedPeers.add(remotePeerID);

	}

	private void handleNotInterestedMessage(Integer remotePeerID) {
		logger.log("Removing " + remotePeerID + " from " + currentPeerID + "' s interested list", Level.INFO);
		System.out.println("Removing " + remotePeerID + " from " + currentPeerID + "' s interested list");
		UtilityClass.intersetedPeers.remove(remotePeerID);
	}

	private void sendBitfieldMessage(int remotePeerID) {
		try {
			logger.log("Sending bitfield message from " + currentPeerID + " to " + remotePeerID, Level.INFO);
			byte[] bitfieldByteArray = UtilityClass.allPeerMap.get(currentPeerID).bitfield.toByteArray();
			Message message = new Message(currentPeerID, BITFIELD);
			message.messagePayload = bitfieldByteArray;
			buffer = transformObject(message);
			writeToChannel();

		} catch (Exception e) {
			logger.log(e.getMessage(), Level.SEVERE);
		}
	}

	private void sendInterested(int remotePeerID) {

		// System.out.println(UtilityClass.currentPeerID + " sending Interested message
		// to Peer " + remotePeerID);
		Message msg = new Message(UtilityClass.currentPeerID, INTERESTED);
		try {
			System.out.println(currentPeerID + "sending inetrested to: " + remotePeerID);
			buffer = transformObject(msg);
			writeToChannel();
		} catch (IOException e) {
			logger.log(e.getMessage(), Level.SEVERE);
		}
	}

	private void sendUnchokeMessage(int remotePeerID) {

		logger.log(UtilityClass.currentPeerID + " sending unchoke message to Peer " + remotePeerID, Level.INFO);
		System.out.println(currentPeerID + " sending unchoke message to Peer " + remotePeerID);
		Message msg = new Message(UtilityClass.currentPeerID, UNCHOKE);
		try {
			buffer = transformObject(msg);
			writeToChannel();
		} catch (IOException e) {
			logger.log(e.getMessage(), Level.SEVERE);
		}
	}

	private void sendNotInterested(int remotePeerID) {

		System.out.println(UtilityClass.currentPeerID + " sending Not-Interested message to Peer " + remotePeerID);
		Message msg = new Message(UtilityClass.currentPeerID, NOT_INTERESTED);
		try {
			buffer = UtilityClass.transformObject(msg);
			writeToChannel();
		} catch (IOException e) {
			logger.log(e.getMessage(), Level.SEVERE);
		}
	}

	private void handleHaveMessage(Message receivedMsg) {

		if (receivedMsg.messagePayload != null) {
			byte[] payload = receivedMsg.messagePayload;
			ByteArrayInputStream inputStream = new ByteArrayInputStream(payload);
			inputStream.read(payload, 0, 4);
			int index = ByteBuffer.wrap(payload).getInt();
			UtilityClass.allPeerMap.get(receivedMsg.PeerID).bitfield.set(index);
			if (UtilityClass.getCurrentPeerInfo().bitfield.get(index)) {
				System.out.println(receivedMsg.PeerID + " has nothing I don't got");
				sendNotInterested(receivedMsg.PeerID);
			} else {
				System.out.println(receivedMsg.PeerID + " has an interesting piece, yummm yummm");
				sendInterested(receivedMsg.PeerID);
			}
		} else {
			try {
				throw new BitTorrentExceptions("Something wrong in have message, payload is missing");
			} catch (BitTorrentExceptions e) {
				logger.log(e.getMessage(), Level.SEVERE);
			}
		}
	}

	private void handleRequestMessage(Message recievedMsg) {
		// TODO Auto-generated method stub
		System.out.println("Handling request message from: " + recievedMsg.PeerID);
		if (recievedMsg.messagePayload != null) {
			byte[] payload = recievedMsg.messagePayload;
			ByteArrayInputStream inputStream = new ByteArrayInputStream(payload);
			inputStream.read(payload, 0, 4);
			int index = ByteBuffer.wrap(recievedMsg.messagePayload).getInt();
			sendPieceMessage(index, recievedMsg.PeerID);
		} else {
			try {
				throw new BitTorrentExceptions("Something wrong in request message, payload is missing");
			} catch (BitTorrentExceptions e) {
				logger.log(e.getMessage(), Level.SEVERE);
			}
		}
	}

	private void handlePieceMessage(Message receivedMsg) {

		if (receivedMsg.messagePayload != null) {
			System.out.println(currentPeerID + " is handling piece message from " + receivedMsg.PeerID);
			byte[] payload = receivedMsg.messagePayload;
			byte[] pieceIndex = new byte[4];
			System.out.println(currentPeerID + "Handling piece message from: " + receivedMsg.PeerID);
			ByteArrayInputStream bufferInput = new ByteArrayInputStream(payload);
			bufferInput.read(pieceIndex, 0, 4);
			int in = ByteBuffer.wrap(pieceIndex).getInt();
			// this should move after writing is finished
			

			byte[] part = Arrays.copyOfRange(payload, 4, payload.length - 4);
			FileOutputStream fileOutputStream;

			try {
				fileOutputStream = new FileOutputStream(System.getProperty("user.dir") + File.separator + "peer_"
						+ UtilityClass.currentPeerID + File.separator + in + ".dat");
				fileOutputStream.write(part);
				fileOutputStream.close();
				allPeerMap.get(currentPeerID).bitfield.set(in);
				allPeerMap.get(receivedMsg.PeerID).bitfield.set(in);
			} catch (FileNotFoundException e) {
				logger.log(e.getMessage(), Level.SEVERE);
			} catch (IOException e) {
				logger.log(e.getMessage(), Level.SEVERE);
			}
			for(Integer peer : UtilityClass.allPeerMap.keySet())
            {
			//allPeerMap.keySet().forEach(peer -> {
				BitSet peerBitfield = allPeerMap.get(peer).bitfield;
				if (!peerBitfield.get(in) && allPeerMap.get(peer).isHandshakeSent) {
					// Send Have message
					byte[] havePayload = ByteBuffer.allocate(4).putInt(in).array();
					Message message = new Message(peer, HAVE);
					message.messagePayload = havePayload;

					try {
						buffer = UtilityClass.transformObject(message);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					writeToChannel();
//                    try {
//                        socketChannel.write(UtilityClass.transformObject(message));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

				}
			}//);
		}
	}

	private void sendPieceMessage(int pieceIndex, int remotePeerID) {
		System.out.println(currentPeerID + " Sending piece to :" + remotePeerID);
		String directoryPath = System.getProperty("user.dir") + File.separator + "peer_" + currentPeerID
				+ File.separator + "Part" + pieceIndex + FILE_SUFFIX;
		System.out.println(directoryPath);
		byte[] bytes1 = ByteBuffer.allocate(4).putInt(pieceIndex).array();
		File file = new File(directoryPath);
		//byte[] bytes2 = new byte[(int) file.length() + 4];
		byte[] bytes2 = new byte[CommonProperties.pieceSize + 4];

		FileInputStream fis;
		try {

			fis = new FileInputStream(file);
			fis.read(bytes2);
			fis.close();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(bytes1);
			baos.write(bytes2);
			byte[] payload = baos.toByteArray();
			Message sendMessage = new Message(remotePeerID, PIECE);
			sendMessage.setMessagePayload(payload);
			buffer = transformObject(sendMessage);
			buffer.clear();
			socketChannel.write(buffer);
			//writeToChannel();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	synchronized private void writeToChannel() {
		try {
			socketChannel.write(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buffer.clear();
		buffer.flip();
	}

}
