//package bittorrent;
//
//import java.net.*;
//import java.io.*;
//import java.nio.*;
//import java.nio.channels.*;
//import java.util.*;
//
//public class Server {
//	ServerSocket server = null;
//
//	public void run() {
//		int peerID = PeersProcessHandler.current.getListeningPort();
//		try {
//
//			// initialize Input and Output streams
//			server = new ServerSocket(peerID);
//
//			while (true) {
//				// receive the message sent from the client
//				Socket s = null;
//				s = server.accept();
//				ObjectInputStream in = new ObjectInputStream(s.getInputStream());
//				ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
//				out.flush();
//
//			}
//		} catch (IOException ioException) {
//			System.out.println("Disconnect with Peer: " + Integer.toString(peerID));
//		} finally {
//			// Close connections
//			try {
//				server.close();
//
//			} catch (IOException ioException) {
//				System.out.println("Disconnect with Peer: " + peerID);
//			}
//		}
//	}
//
//}
