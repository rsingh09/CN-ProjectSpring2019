package trialSelector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.util.concurrent.ConcurrentHashMap;

public class UtilityClass 
{
	public static ConcurrentHashMap<Integer, PeerInfo> allPeerMap = new ConcurrentHashMap<Integer,PeerInfo>();
	public static Selector selectorP2P;
	public static int currentPeerID;
	
	public static Object ReadFromBuffer (byte[] buffer) throws ClassNotFoundException, IOException
	{
		//byte[] arr = new byte[buffer.remaining()];
		System.out.println("1");
		ByteArrayInputStream in = new ByteArrayInputStream(buffer);
		System.out.println("2");
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
