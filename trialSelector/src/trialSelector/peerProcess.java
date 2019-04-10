package trialSelector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class peerProcess {
	private static EchoServer server;
	//static BitTorrentLogger log = new BitTorrentLogger();

	public static void main(String args[]) {
		int currentPeerID = Integer.parseInt(args[0]);
		UtilityClass.currentPeerID = currentPeerID;

		// iterate to send connect requests to the servers.
		// Once accepting the connection, assign the socket channel to the selector

		ReadConfigurations.readPeerInfo();
		ReadConfigurations.readCommonConfig();
		/*
		 * // System.out.println("Common Properties are "); //
		 * System.out.println(CommonProperties.fileName); //
		 * System.out.println(CommonProperties.fileSize); //
		 * System.out.println(CommonProperties.numberOfPreferredNeighbors); //
		 * System.out.println(CommonProperties.optimisticUnchokingInterval); //
		 * System.out.println(CommonProperties.pieceSize); //
		 * System.out.println(CommonProperties.unchokingInterval); // //
		 * System.out.println("******************************"); // for(int id :
		 * UtilityClass.allPeerMap.keySet()) // { // PeerInfo p1 =
		 * UtilityClass.allPeerMap.get(id); // System.out.println(p1.fileInfo); //
		 * System.out.println(p1.hasFile); // System.out.println(p1.peerID); //
		 * System.out.println(p1.peerIndex); // System.out.println(p1.listeningPort); //
		 * System.out.println("******************************"); // }
		 */

		createServer(UtilityClass.getCurrentPeerInfo());
		for (int id : UtilityClass.allPeerMap.keySet()) {
			if (id == UtilityClass.currentPeerID) {
				break;
			} else {
				createSocketChannels(UtilityClass.allPeerMap.get(id));
				System.out.println("id : " + id);
			}
		}
	}


	private static void createServer(PeerInfo peerInfo) {
		try {
			// peerProcess.listeningSocket = new ServerSocket(peerProcess.LISTENING_PORT);
			server = new EchoServer();
			server.start();
			String msg = "Created Peer" + peerInfo.peerID;
			String lvl = "Info";
			//log.WriteToLog(peerInfo.peerID, msg, lvl);
//	            return 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createSocketChannels(PeerInfo peerInfo) {
		EchoClient client = new EchoClient(peerInfo);
		// client = SocketChannel.open(new InetSocketAddress(peerInfo.hostName,
		// peerInfo.listeningPort));
		ByteBuffer buffer = ByteBuffer.allocate(CommonProperties.pieceSize + 10);
		HandshakeMessage handshakeMsg = new HandshakeMessage(UtilityClass.currentPeerID);
		// ByteBuffer buf = transformObject(handshakeMsg);
		client.sendMessage(handshakeMsg);
		// peerInfo.socketBuffer = buffer;
		// client.write(buf);
		// buffer.clear();

	}
}
