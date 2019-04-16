package trialSelector;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
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

	public static void mergeSplitFiles(){
		try {
			ArrayList<byte[]> list = new ArrayList<byte[]>();
			int i = 0;

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
			int j = 0;
			while(j < 9)
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
