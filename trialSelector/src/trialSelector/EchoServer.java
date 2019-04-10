package trialSelector;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EchoServer extends Thread{

	public void run() {

		try {
			UtilityClass.selectorP2P = Selector.open();
			ServerSocketChannel serverSocket = ServerSocketChannel.open();

			serverSocket.bind(new InetSocketAddress(UtilityClass.allPeerMap.get(UtilityClass.currentPeerID).hostName,
					UtilityClass.allPeerMap.get(UtilityClass.currentPeerID).listeningPort));

			serverSocket.configureBlocking(false);
			serverSocket.register(UtilityClass.selectorP2P, SelectionKey.OP_ACCEPT);
			ByteBuffer buffer = ByteBuffer.allocate(CommonProperties.pieceSize + 10);
			System.out.println("Peer " + UtilityClass.currentPeerID+" listening at: " + UtilityClass.allPeerMap.get(UtilityClass.currentPeerID).listeningPort);
			while (true) {
				UtilityClass.selectorP2P.select();
				Set<SelectionKey> selectedKeys = UtilityClass.selectorP2P.selectedKeys();
				Iterator<SelectionKey> iter = selectedKeys.iterator();
				while (iter.hasNext()) {

					SelectionKey key = iter.next();

					if (key.isAcceptable()) {
						register(UtilityClass.selectorP2P, serverSocket);
					}

					if (key.isReadable()) {
						System.out.println("MsgRecieved");
						answerWithEcho(buffer, key);
					}
					iter.remove();
				}
			}
		} catch (ClosedChannelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void answerWithEcho(ByteBuffer buffer, SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		client.read(buffer);
		// Here we will deploy the message to the concurrent linked queue.
		buffer.flip();
		client.write(buffer);
		buffer.clear();
	}

	private static void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
		SocketChannel client = serverSocket.accept();
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_READ);
	}

//	public static Process start() throws IOException, InterruptedException {
//		String javaHome = System.getProperty("java.home");
//		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
//		String classpath = System.getProperty("java.class.path");
//		String className = EchoServer.class.getCanonicalName();
//
//		ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);
//
//		return builder.start();
//	}
}