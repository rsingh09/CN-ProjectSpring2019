package trialSelector;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class UtilityClass {
	public static ConcurrentHashMap<Integer, PeerInfo> allPeerMap = new ConcurrentHashMap<Integer, PeerInfo>();
	public static ConcurrentHashMap<Integer, BitSet> bitField = new ConcurrentHashMap<Integer, BitSet>();
	public static ConcurrentHashMap<Integer, MessageHandler> channelMessageHandlerMap = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, Integer> channelKeyHandler = new ConcurrentHashMap<>();
	// public static ConcurrentHashMap<Integer, PeerInfo> intersetedPeers = new
	// ConcurrentHashMap<Integer,PeerInfo>();
	public static CopyOnWriteArrayList<Integer> intersetedPeers = new CopyOnWriteArrayList<Integer>();
	public static CopyOnWriteArrayList<Integer> unChokedPeers = new CopyOnWriteArrayList<Integer>();
	public static CopyOnWriteArrayList<Integer> kNeighbours = new CopyOnWriteArrayList<Integer>();
	public static CopyOnWriteArrayList<Integer> requestedPieces = new CopyOnWriteArrayList<Integer>();
	public static Selector selectorP2P;
	public static int currentPeerID;
	public static int totalSplitParts;
	

	public static PeerInfo getCurrentPeerInfo() {
		return allPeerMap.get(currentPeerID);
	}

	synchronized public static Object ReadFromBuffer(byte[] buffer) throws ClassNotFoundException, IOException {
		// byte[] arr = new byte[buffer.remaining()];
		ByteArrayInputStream in = new ByteArrayInputStream(buffer);
		ObjectInputStream is = new ObjectInputStream(in);
		Object obj = is.readObject();
		is.close();
		return obj;
	}

	synchronized public static ByteBuffer transformObject(HandshakeMessage handshakeMsg) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(handshakeMsg);
		oos.flush();
		byte[] data = bos.toByteArray();
		ByteBuffer buf = null;
		return buf.wrap(data);
		// return bos.toByteArray();
	}

	public static void sendTcpRequest(PeerInfo peer) throws IOException {
		BitTorrentLogger logger = new BitTorrentLogger();
		SocketChannel client = null;
		try {

			client = SocketChannel.open(new InetSocketAddress(peer.hostName, peer.listeningPort));
			client.configureBlocking(false);
			if (UtilityClass.selectorP2P == null) {
				UtilityClass.selectorP2P = Selector.open();
			}
			client.register(UtilityClass.selectorP2P, SelectionKey.OP_READ);
			UtilityClass.allPeerMap.get(peer.peerID).peerSocketChannel = client;


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		HandshakeMessage handshakeMsg = new HandshakeMessage(UtilityClass.currentPeerID);
		UtilityClass.allPeerMap.get(peer.peerID).isHandshakeSent = true;
		ByteBuffer buffer = UtilityClass.transformObject(handshakeMsg);
		MessageHandler messageHandler = new MessageHandler(client);
		messageHandler.start();
		logger.log("Peer "+ UtilityClass.currentPeerID + " makes a TCP connection with "+ peer.peerID, Level.INFO);
		UtilityClass.channelMessageHandlerMap.put(peer.peerID, messageHandler);
		// buffer.wrap(b, 0, b.length);
		client.write(buffer);
		buffer.clear();
		//buffer.flip();

	}

	synchronized public static ByteBuffer transformObject(Message actualMessage) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(actualMessage);
		oos.flush();
		byte[] data = bos.toByteArray();
		ByteBuffer buf = ByteBuffer.wrap(data);
		return buf;
	}

	public static ByteBuffer transformObject(String msg) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(msg);
		oos.flush();
		byte[] data = bos.toByteArray();
		ByteBuffer buf = ByteBuffer.wrap(data);
		return buf;
	}

	public static void mergeSplitFiles(){
		try {
			ArrayList<byte[]> list = new ArrayList<byte[]>();
			int i = 0;
			System.out.println("Total Split Parts" + totalSplitParts);
			while (i < totalSplitParts) {
				String peerDirectory = System.getProperty("user.dir") + File.separator + "peer_" + currentPeerID + File.separator + i + ".dat";
				File f = new File(peerDirectory);
				FileInputStream inputStream;
				byte[] bytes = new byte[(int) f.length()];

				inputStream = new FileInputStream(f);
				inputStream.read(bytes);
				list.add(i, bytes);
				inputStream.close();
				i++;
			}
			OutputStream outputStream = new FileOutputStream(System.getProperty("user.dir") + File.separator + "peer_" + currentPeerID + File.separator + "Eden.jpg");
			System.out.println("Correct directory for the file being split" + currentPeerID);
			int j = 0;
			while(j < 8)
			{
				outputStream.write(list.get(j));
				j++;
			}
			outputStream.close();
		} catch(FileNotFoundException ex){
			ex.printStackTrace();
		} catch(IOException ex){
			ex.printStackTrace();
		}
	}
}
