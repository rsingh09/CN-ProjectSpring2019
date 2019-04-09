
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
	Selector selector;
	private RemoteP2PThread clientThread;
	/*
	 * Constructor
	 */
	public P2PServerThread(int listeningPort, String peerID) {
		this.listeningPort = listeningPort;
		this.peerID = peerID;
	}

	public void run() {
		try {
			selector = Selector.open();
			ServerSocketChannel serverSocket = ServerSocketChannel.open();
			serverSocket.bind(new InetSocketAddress("localhost", listeningPort));
			serverSocket.configureBlocking(false);
			serverSocket.register(selector, SelectionKey.OP_ACCEPT);
			ByteBuffer buffer = ByteBuffer.allocate(15000);
			System.out.println("Print 1");
			while (true) {
				selector.select();
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iter = selectedKeys.iterator();
				while (iter.hasNext()) {

					SelectionKey key = iter.next();

					if (key.isAcceptable()) {
						System.out.println("Print 2");
						register(selector, serverSocket);
					}

					if (key.isReadable()) {
						answerWithEcho(buffer, key);//form message object and transfer to message handler
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

	 private static void answerWithEcho(ByteBuffer buffer, SelectionKey key)
		      throws IOException {
		  
		        SocketChannel client = (SocketChannel) key.channel();
		        client.read(buffer);	 
		        buffer.flip();
		        client.write(buffer);
		        buffer.clear();
		    }
	private static void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {

		SocketChannel client = serverSocket.accept();
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_READ);
	}
}
