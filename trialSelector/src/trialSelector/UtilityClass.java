package trialSelector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.util.BitSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UtilityClass
{
	public static ConcurrentHashMap<Integer, PeerInfo> allPeerMap = new ConcurrentHashMap<Integer,PeerInfo>();
	public static ConcurrentHashMap<Integer, BitSet> bitField = new ConcurrentHashMap<Integer,BitSet>();
	//public static ConcurrentHashMap<Integer, PeerInfo> intersetedPeers = new ConcurrentHashMap<Integer,PeerInfo>();
	public static CopyOnWriteArrayList<Integer> intersetedPeers = new CopyOnWriteArrayList<Integer>();
	public static CopyOnWriteArrayList<Integer> unChokedPeers = new CopyOnWriteArrayList<Integer>();
	public static CopyOnWriteArrayList<Integer> kNeighbours = new CopyOnWriteArrayList<Integer>();
	public static CopyOnWriteArrayList<Integer> requestedPieces = new CopyOnWriteArrayList<Integer>();
	public static Selector selectorP2P;
	public static int currentPeerID;
	public static int totalSplitParts;
	public static PeerInfo getCurrentPeerInfo()
	{
		return allPeerMap.get(currentPeerID);
	}
	synchronized public static Object ReadFromBuffer (byte[] buffer) throws ClassNotFoundException, IOException
	{
		//byte[] arr = new byte[buffer.remaining()];
		ByteArrayInputStream in = new ByteArrayInputStream(buffer);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return is.readObject();
	}
	public static ByteBuffer transformObject(HandshakeMessage handshakeMsg) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(handshakeMsg);
		oos.flush();
		byte[] data = bos.toByteArray();
		ByteBuffer buf = ByteBuffer.wrap(data);
		return buf;
	}

	public static ByteBuffer transformObject(Message actualMessage) throws IOException {
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

}
