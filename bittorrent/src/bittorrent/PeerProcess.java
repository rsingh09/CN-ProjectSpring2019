package bittorrent;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/*
Handle major peer functionality
*/
public class PeerProcess {
	static BitTorrentLogger log = new BitTorrentLogger();
	private static String peerID;
	private static HashMap<String, Peers> remotePeerInfo = new LinkedHashMap<String, Peers>();
	Socket requestSocket; // socket connect to the server
	ObjectOutputStream out; // stream write to the socket
	ObjectInputStream in; // stream read from the socket
	String message; // message send to the server
	String MESSAGE; // capitalized message read from the server
	private ServerSocket listeningSocket;
	private int LISTENING_PORT;
	private ListeningThreadAsServer listeningThread;
	private int peerProcessIndex;

	/*
	 * public void Client() {
	 * 
	 * }
	 */
	/*
	 * Function name : readPeerInfo Parameters passed: None Return: Void Reads the
	 * peer info from the cfg file
	 */
	static void readPeerInfo() {
		File file = new File("PeerInfo.cfg");
		// List<Peers> peerList = new ArrayList<Peers>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			int index = 1;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(" ");
				remotePeerInfo.put(tokens[0], new Peers(Integer.parseInt(tokens[0]), tokens[1], tokens[2],
						Integer.parseInt(tokens[3]), index));
				index++;
			}
			br.close();
//			return peerList;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block Add in logger
			e.printStackTrace();
		}
	}

	/*
	 * Function name : main Parameters passed: String[] args Return: Void Main
	 * function
	 */
	// main method
	public static void main(String[] args) {
		PeerProcess peerProcess = new PeerProcess();

		readPeerInfo();
//        int count = 0;
//        while (count < 2) {
		try {
			// reading peerID from command line
			peerID = args[0];
			boolean flagForFirstPeer = false;
			for (String st : remotePeerInfo.keySet()) {
				Peers peerInfo = remotePeerInfo.get(st);
				if (peerInfo.getPeerID() == Integer.parseInt(peerID)) {
					peerProcess.LISTENING_PORT = Integer.parseInt(peerInfo.getListeningPort());
					peerProcess.peerProcessIndex = peerInfo.getPeerIndex();

					if (peerInfo.isFirstPeer()) {
						flagForFirstPeer = true;
						break;
					}
				}
			}

			// Initialize Bitfield

			if (flagForFirstPeer) {
				String msg = "First peer listening on port 8008, peerID: " + peerID;
				String lvl = "Info";
				log.WriteToLog(msg, lvl);
				startServer(peerProcess, peerID);

			} else {
				// Create logs and directory for the new peer
				for (String st : remotePeerInfo.keySet()) {
					Peers peer = remotePeerInfo.get(st);
					if (peerProcess.peerProcessIndex > peer.getPeerIndex())
					{
						System.out.println("Port number"+ peer.getPeerIndex());
						InetAddress IPAddress = InetAddress.getByName("localhost");
						Socket socket = new Socket(IPAddress,Integer.parseInt(peer.getListeningPort()));
						ObjectOutputStream obStream = new ObjectOutputStream(socket.getOutputStream());
						ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
						
						HandshakeMessage handshake = new HandshakeMessage(peerID);
						
						obStream.writeObject(handshake);
						obStream.flush();
						
						RemotePeerHandlerAsClientThread remotePeerThread = new RemotePeerHandlerAsClientThread(
								peer.getPeerID(), peer.getHostName(),
								Integer.parseInt(peer.getListeningPort()), 1,socket, obStream,inStream);
						remotePeerThread.start();

					}
				}
				startServer(peerProcess, peerID);
			}

//                count++;
		} catch (Exception e) {
			e.printStackTrace();
		}
//        }
	}

	/*
	 * Function name : startServer Parameters passed: PeerProcess, peerid Return:
	 * int Starts the server
	 */
	private static int startServer(PeerProcess peerProcess, String peerID) {
		try {
			peerProcess.listeningSocket = new ServerSocket(peerProcess.LISTENING_PORT);
			peerProcess.listeningThread = new ListeningThreadAsServer(peerProcess.listeningSocket, peerID);
			peerProcess.listeningThread.start();
			String msg = "Created Peer" + peerID;
			String lvl = "Info";
			log.WriteToLog(msg, lvl);
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

}
