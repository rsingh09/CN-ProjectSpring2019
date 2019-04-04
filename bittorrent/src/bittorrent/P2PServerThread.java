package bittorrent;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author Sajid
 * Class that houses the logic to start listener thread 
 * Listens to requests from clients once the handshake message is sent
 */
public class P2PServerThread extends Thread {
    private ServerSocket listeningSocketInServer;
    private String peerID;
    private Socket peerSocket;
    private RemoteP2PThread clientThread;

	/*
	Constructor
	*/
    public P2PServerThread(ServerSocket listeningSocket, String peerID) {
        this.listeningSocketInServer = listeningSocket;
        this.peerID = peerID;
    }


    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            peerSocket = listeningSocketInServer.accept();
            //starts a client thread in a passive mode, to send/receive new messages from other peers
            this.clientThread = new RemoteP2PThread(peerSocket, 0, peerID );
            PeerProcess.addClientThreads(clientThread);
            System.out.println("Assigning the client handler for the connection");
            clientThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
