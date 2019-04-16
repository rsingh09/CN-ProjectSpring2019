package trialSelector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class EchoClient extends Thread {
	private ByteBuffer buffer;
	private PeerInfo peerInfo;
	// private static EchoClient instance;

	public EchoClient() {
//		try {
//			peerInfo = peerInfoObj;
//			SocketChannel client;
//			client = SocketChannel.open(new InetSocketAddress(peerInfoObj.hostName, peerInfoObj.listeningPort));
//			MessageHandler messageHandler=new MessageHandler(client);
//			messageHandler.start();
//			UtilityClass.chanelMessageHandlerMap.put(client,messageHandler);
//			
//			// peerInfoObj.peerSocketChannel = client;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		buffer = ByteBuffer.allocate(CommonProperties.pieceSize + 10);
		// TODO Auto-generated constructor stub
	}

	public void run() {
//		HandshakeMessage handshakeMsg = new HandshakeMessage(UtilityClass.currentPeerID);
//		// ByteBuffer buf = transformObject(handshakeMsg);
//
//		UtilityClass.allPeerMap.get(peerInfo.peerID).isHandshakeSent = true;
//		sendMessage(handshakeMsg);
	}

//	public String sendMessage(HandshakeMessage msg) {
//		String response = null;
//		MessageHandler messageHandler = new MessageHandler(client, buffer);
//		try {
//			byte[] b = UtilityClass.transformObject(msg);
//			buffer.wrap(b, 0, b.length);
//			// buffer.allocate(CommonProperties.pieceSize + 10);
//			client.write(buffer);
//			while (true) {
//				buffer.clear();
//				byte[] bytes = new byte[buffer.remaining()];
//				// byte[] bytes = null;
//
//				client.read(buffer);
//				// buffer.get(bytes);
//				bytes = buffer.array();
//				Object obj = UtilityClass.ReadFromBuffer(bytes);
//				if (!messageHandler.isAlive()) {
//					messageHandler.start();
//					messageHandler.messagesQueue.add(obj);
//				} else
//					messageHandler.messagesQueue.add(obj);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		return response;
//	}
}
