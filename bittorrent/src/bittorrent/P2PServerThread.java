
//package bittorrent;

import java.net.InetSocketAddress;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/**
 * 
 * @author Sajid Class that houses the logic to start listener thread Listens to
 *         requests from clients once the handshake message is sent
 */
public class P2PServerThread extends Thread {
	//private ServerSocket listeningSocketInServer;
	private int listeningPort;
	private String peerID;
	private Socket peerSocket;
	private RemoteP2PThread clientThread;
	/*
	 * Constructor
	 */
	public P2PServerThread(int listeningPort, String peerID) {
		this.listeningPort = listeningPort;
		this.peerID = peerID;
	}

	public void run() {
		Selector selector;
		try {
			selector = Selector.open();
			ServerSocketChannel serverSocket = ServerSocketChannel.open();
			serverSocket.bind(new InetSocketAddress("localhost", 6008));
			serverSocket.configureBlocking(false);
			serverSocket.register(selector, SelectionKey.OP_ACCEPT);
			ByteBuffer buffer = ByteBuffer.allocate(15000);
			while (true) {
				selector.select();
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iter = selectedKeys.iterator();
				while (iter.hasNext()) {

					SelectionKey key = iter.next();

					if (key.isAcceptable()) {
						register(selector, serverSocket);
					}

					if (key.isReadable()) {
						//answerWithEcho(buffer, key);//form message object and transfer to message handler
						System.out.print("message received");
					}
					iter.remove();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {

		SocketChannel client = serverSocket.accept();
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_READ);
	}
}
