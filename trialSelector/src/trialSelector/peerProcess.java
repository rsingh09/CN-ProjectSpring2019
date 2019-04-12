package trialSelector;

import jdk.jshell.execution.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.BitSet;

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

		String directoryPath = System.getProperty("user.dir") + File.separator + "peer_" + currentPeerID;
		//System.out.println("Current Directory" + directoryPath);
		//System.out.println(directoryPath);
		File directory = new File(directoryPath);
		if(!directory.exists()){
			directory.mkdirs();
		}

        if(UtilityClass.allPeerMap.get(currentPeerID).hasFile == 1){
        	System.out.println("Peer " + currentPeerID + " has the complete file ");
			try{
				Splitter.splitFileToPieces(CommonProperties.fileName, CommonProperties.fileSize, currentPeerID);
			} catch(Exception e){
				e.printStackTrace();
			}
		}

		int totalSplits = CommonProperties.fileSize/CommonProperties.pieceSize;
		long remainingBytes = CommonProperties.fileSize % CommonProperties.pieceSize;

		UtilityClass.totalSplitParts = remainingBytes > 0 ? totalSplits +1 : totalSplits;

		//Initialize my own bitfield
		UtilityClass.allPeerMap.keySet().forEach(peerEntry -> {
			PeerInfo infoObj = UtilityClass.allPeerMap.get(peerEntry);
			if(infoObj.hasFile == 1){

				for(int i=0 ; i < UtilityClass.totalSplitParts; i++){
					infoObj.bitfield.set(i);

				}
			}
		});


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
	
	private static void determineKPreferredNeighbour()
	{
		
	}
	
	private static void determineOptimisticallyUnchoke()
	{
		
	}

	private static void createSocketChannels(PeerInfo peerInfo) {
		EchoClient client = new EchoClient(peerInfo);
		// client = SocketChannel.open(new InetSocketAddress(peerInfo.hostName,
		// peerInfo.listeningPort));
		//ByteBuffer buffer = ByteBuffer.allocate(CommonProperties.pieceSize + 10);
		//if 1001's handshake is not completed

		HandshakeMessage handshakeMsg = new HandshakeMessage(UtilityClass.currentPeerID);
		// ByteBuffer buf = transformObject(handshakeMsg);

		UtilityClass.allPeerMap.get(peerInfo.peerID).isHandshakeSent = true;
		client.sendMessage(handshakeMsg);

		// peerInfo.socketBuffer = buffer;
		// client.write(buf);
		// buffer.clear();

	}

/*	public static File createDirectory(String directoryPath) throws IOException {
		File dir = new File(directoryPath);
		if (dir.exists()) {
			return dir;
		}
		if (dir.mkdirs()) {
			return dir;
		}
		throw new IOException("Failed to create directory '" + dir.getAbsolutePath() + "' for an unknown reason.");
	}*/
}
