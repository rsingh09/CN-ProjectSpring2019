package bittorrent;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class PeerProcess {
	private static String peerID;
	Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
 	ObjectInputStream in;          //stream read from the socket
	String message;                //message send to the server
	String MESSAGE;                //capitalized message read from the server
	private static Hashtable<String, Peers> remotePeerInfo = new Hashtable<String, Peers>();
	private ServerSocket listeningSocket;
	private int LISTENING_PORT;
	private ListeningThreadAsServer listeningThread;
	private int peerProcessIndex;
	static BitTorrentLogger log = new BitTorrentLogger();
	
	/*public void Client() {
		
	}*/
	
	static void readPeerInfo() {
		File file = new File("PeerInfo.cfg");
		//List<Peers> peerList  = new ArrayList<Peers>();
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			int index = 1;
			while ((line = br.readLine())!=null)
			{
				String[] tokens = line.split(" ");
				remotePeerInfo.put(tokens[0], new Peers(Integer.parseInt(tokens[0]), 
						tokens[1], tokens[2], Integer.parseInt(tokens[3]), index));
			   index++;
			}
			br.close();
//			return peerList;
		} catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block Add in logger
			e.printStackTrace();
		} 
	}
	
	/*void run()
	{
		try{
			readPeerInfo();
			Set<String> keys = remotePeerInfo.keySet();
			Iterator<String> itr = keys.iterator();
					
			while(itr.hasNext()) {
				
			}
			//Read from PeerInfo and Create a thread for that
			//create a socket to connect to the server
			requestSocket = new Socket("localhost", 8000);
			System.out.println("Connected to localhost in port 8000");
			//initialize inputStream and outputStream
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			
			//get Input from standard input
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			while(true)
			{
				//read a sentence from the standard input
				//Message = Header + Bitset + PeerID
				HandshakeMessage handshakeObj = new HandshakeMessage("1001");
				
				//Send the sentence to the server
				sendMessage(message);
				//Receive the upperCase sentence from the server
				MESSAGE = (String)in.readObject();
				//show the message to the user
				System.out.println("Receive message: " + MESSAGE);
			}
		}
		catch (ConnectException e) {
    			System.err.println("Connection refused. You need to initiate a server first.");
		} 
		catch ( ClassNotFoundException e ) {
            		System.err.println("Class not found");
        	} 
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	//send a message to the output stream
	void sendMessage(String msg)
	{
		try{
			//stream write the message
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}*/
	//main method
	public static void main(String args[])
	{
		PeerProcess peerProcess = new PeerProcess();

		readPeerInfo();
		int count = 0;
		while(count < 2) {
		try {
			peerID = args[count];
			Set<String> keys = remotePeerInfo.keySet();
			Iterator<String> itr = keys.iterator();
//			Peers peerInfo = null;
			boolean flagForFirstPeer = false;
			while(itr.hasNext()) {
				Peers peerInfo = remotePeerInfo.get(itr.next());
				if(peerInfo.getPeerID() == Integer.parseInt(peerID)) {
					peerProcess.LISTENING_PORT = Integer.parseInt(peerInfo.getListeningPort());
					peerProcess.peerProcessIndex = peerInfo.getPeerIndex();
					
					if(peerInfo.isFirstPeer()) {
						flagForFirstPeer = true;
						break;
					}
				}
			}
			
			//Initialize Bitfield
			
			if(flagForFirstPeer) {
				String msg = "First peer listening on port 8008, peerID: " + peerID;
				String lvl = "Info";
				log.WriteToLog(msg, lvl);
				startServer(peerProcess, peerID);
				
			} else {
				//Create logs and directory for the new peer
				Iterator<String> itr1 = keys.iterator();
				while(itr1.hasNext()) {
					Peers peer = remotePeerInfo.get(itr.next());
					if(peerProcess.peerProcessIndex > peer.getPeerIndex()) {
						
						RemotePeerHandlerAsClientThread remotePeerThread = new RemotePeerHandlerAsClientThread(peer.getPeerID(), peer.getHostName(), Integer.parseInt(peer.getListeningPort()), 1);
						remotePeerThread.start();
						
					}
				}
				startServer(peerProcess, peerID);
			}
			
			count++;
		} catch(Exception e){
			
		}
		}
	}
	
	private static void startServer(PeerProcess peerProcess, String peerID ) {
		try {
			peerProcess.listeningSocket = new ServerSocket(peerProcess.LISTENING_PORT);
			peerProcess.listeningThread = new ListeningThreadAsServer(peerProcess.listeningSocket, peerID);
			peerProcess.listeningThread.start();
			String msg = "Created Peer" + peerID;
			String lvl = "Info";
			log.WriteToLog(msg, lvl);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
