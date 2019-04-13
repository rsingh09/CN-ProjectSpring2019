package trialSelector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class EchoClient extends Thread{
	private SocketChannel client;
	private ByteBuffer buffer;
	private PeerInfo peerInfo;
	// private static EchoClient instance;

	public EchoClient(PeerInfo peerInfoObj) {
		try {
			peerInfo = peerInfoObj;
			client = SocketChannel.open(new InetSocketAddress(peerInfoObj.hostName, peerInfoObj.listeningPort));
			peerInfoObj.peerSocketChannel = client;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buffer = ByteBuffer.allocate(CommonProperties.pieceSize + 10);
		// TODO Auto-generated constructor stub
	}
	public void run()
	{
		HandshakeMessage handshakeMsg = new HandshakeMessage(UtilityClass.currentPeerID);
		// ByteBuffer buf = transformObject(handshakeMsg);

		UtilityClass.allPeerMap.get(peerInfo.peerID).isHandshakeSent = true;
		sendMessage(handshakeMsg);
	}
	public String sendMessage(HandshakeMessage msg) {
		String response = null;
		try {
			while (true) {
				buffer = UtilityClass.transformObject(msg);
				client.write(buffer);
				buffer.clear();

				byte[] bytes = null;
				bytes = buffer.array();

				client.read(buffer);
				Object obj = UtilityClass.ReadFromBuffer(bytes);
				MessageHandler messageHandler = new MessageHandler(client, buffer);
				messageHandler.messagesQueue.add(obj);
				messageHandler.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return response;

	}
}
