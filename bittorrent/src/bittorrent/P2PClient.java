
//package bittorrent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class P2PClient {

	private static SocketChannel client;

	public static P2PClient start() {
		if (instance == null)
			instance = new P2PClient();

		return instance;
	}

	// private static ByteBuffer buffer;
	private static P2PClient instance;
	private static ByteBuffer buffer;

	public P2PClient() {
		try {
			client = SocketChannel.open(new InetSocketAddress("localhost", 6008));
			buffer = ByteBuffer.allocate(256);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String sendHandshake(String msg) {
		HandshakeMessage handshakeMessage = new HandshakeMessage("Peer2");
		String message = handshakeMessage.getHeader() + handshakeMessage.getZeroBits() + handshakeMessage.getPeerID();
		buffer = ByteBuffer.wrap(message.getBytes());
		String response = null;
		try {
			while (true) {
				client.write(buffer);
				buffer.clear();
				client.read(buffer);
				response = new String(buffer.array()).trim();
				System.out.println("response=" + response);
				buffer.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;

	}

}
