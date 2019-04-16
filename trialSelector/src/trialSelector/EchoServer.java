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


public class EchoServer extends Thread 
{

	private static BitTorrentLogger logger = BitTorrentLogger.getInstance();

	public void run() {

		try {
			if(UtilityClass.selectorP2P == null)
			{
				UtilityClass.selectorP2P = Selector.open();
			}
			ServerSocketChannel serverSocket = ServerSocketChannel.open();

			serverSocket.bind(new InetSocketAddress(UtilityClass.allPeerMap.get(UtilityClass.currentPeerID).hostName,
					UtilityClass.allPeerMap.get(UtilityClass.currentPeerID).listeningPort));

			serverSocket.configureBlocking(false);
			serverSocket.register(UtilityClass.selectorP2P, SelectionKey.OP_ACCEPT);

			System.out.println("Peer " + UtilityClass.currentPeerID + " listening at: "
					+ UtilityClass.allPeerMap.get(UtilityClass.currentPeerID).listeningPort);

			while (true) {
				UtilityClass.selectorP2P.select();
				Set<SelectionKey> selectedKeys = UtilityClass.selectorP2P.selectedKeys();
				Iterator<SelectionKey> iter = selectedKeys.iterator();
				while (iter.hasNext()) {

					SelectionKey key = iter.next();

					if (key.isAcceptable()) {
						register(UtilityClass.selectorP2P, serverSocket, key);
					}
					if (key.isReadable()) {
						//System.out.println("MsgRecieved");
						answerWithEcho(key);
						//System.out.println(key.toString());
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

	private static void answerWithEcho(SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(CommonProperties.pieceSize + 10);
		// client.
		client.read(buffer);
		byte[] b = buffer.array();
		try {
			Object obj = UtilityClass.ReadFromBuffer(b);
			if (obj instanceof HandshakeMessage)
			{
				buffer.flip();
				buffer.clear();
				HandshakeMessage hm=(HandshakeMessage)obj;
				int peerid=hm.getPeerID();
				if(!UtilityClass.channelMessageHandlerMap.contains(peerid)) {
					MessageHandler messageHandler = new MessageHandler(client);
					messageHandler.start();
					UtilityClass.allPeerMap.get(peerid).peerSocketChannel = client;
					UtilityClass.channelMessageHandlerMap.put(peerid, messageHandler);
					UtilityClass.channelKeyHandler.put(key.toString(),peerid);
					messageHandler.messagesQueue.add(obj);
				}
			}
			else if (obj instanceof Message)
			{
				Message hm=(Message)obj;
				int peerid=hm.PeerID;
				UtilityClass.channelMessageHandlerMap.get(peerid).messagesQueue.add(obj);
			}
//			if (UtilityClass.channelMessageHandlerMap.contains(key)) {
//				UtilityClass.channelMessageHandlerMap.get(key).messagesQueue.add(buffer);
//			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void register(Selector selector, ServerSocketChannel serverSocket, SelectionKey key)
			throws IOException {
		SocketChannel client = serverSocket.accept();
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_READ);

	}

}
