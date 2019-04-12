package trialSelector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class EchoClient {
	private static SocketChannel client;
	private static ByteBuffer buffer;
	// private static EchoClient instance;

	public EchoClient(PeerInfo peerInfoObj) {
		try {
			client = SocketChannel.open(new InetSocketAddress(peerInfoObj.hostName, peerInfoObj.listeningPort));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buffer = ByteBuffer.allocate(CommonProperties.pieceSize + 10);
		// TODO Auto-generated constructor stub
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
//            buffer.clear();
//            buffer.flip();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return response;

	}
}
