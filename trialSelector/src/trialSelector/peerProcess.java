package trialSelector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class peerProcess 
{
	public static void main(String args[])
	{
		int currentPeerID = Integer.parseInt(args[0]);
		UtilityClass.currentPeerID = currentPeerID;

		//iterate to send connect requests to the servers.
		//Once accepting the connection, assign the socket channel to the selector

		ReadConfigurations.readPeerInfo();
		ReadConfigurations.readCommonConfig();

		//		System.out.println("Common Properties are ");
		//		System.out.println(CommonProperties.fileName);
		//		System.out.println(CommonProperties.fileSize);
		//		System.out.println(CommonProperties.numberOfPreferredNeighbors);
		//		System.out.println(CommonProperties.optimisticUnchokingInterval);
		//		System.out.println(CommonProperties.pieceSize);
		//		System.out.println(CommonProperties.unchokingInterval);
		//		
		//		System.out.println("******************************");
		//		for(int id : UtilityClass.allPeerMap.keySet())
		//		{
		//			PeerInfo p1 = UtilityClass.allPeerMap.get(id);
		//			System.out.println(p1.fileInfo);
		//			System.out.println(p1.hasFile);
		//			System.out.println(p1.peerID);
		//			System.out.println(p1.peerIndex);
		//			System.out.println(p1.listeningPort);
		//			System.out.println("******************************");
		//		}

		for(int id : UtilityClass.allPeerMap.keySet())
		{
			if(id == UtilityClass.currentPeerID)
			{
				break;
			}
			else
			{
				createSocketChannels(UtilityClass.allPeerMap.get(id));
				System.out.println("id : " + id);
			}
		}	
	}

	private static void createSocketChannels(PeerInfo peerInfo) 
	{
		SocketChannel client = null;
		try 
		{
			client = SocketChannel.open(new InetSocketAddress(peerInfo.hostName,peerInfo.listeningPort));
			ByteBuffer buffer = ByteBuffer.allocate(CommonProperties.pieceSize + 10);
			peerInfo.socketChannel = client;
			HandshakeMessage handshakeMsg = new HandshakeMessage(UtilityClass.currentPeerID);
			ByteBuffer buf = transformObject(handshakeMsg);
			peerInfo.socketBuffer = buffer;
			client.write(buf);
			buffer.clear();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private static ByteBuffer transformObject(HandshakeMessage handshakeMsg) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(handshakeMsg);
		oos.flush();
		byte [] data = bos.toByteArray();
		ByteBuffer buf = ByteBuffer.wrap(data);
		return buf;
	}
}
