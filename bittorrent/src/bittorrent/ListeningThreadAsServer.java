package bittorrent;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author Sajid
 * Class that houses the logic to start listener thread 
 * Listens to requests from clients once the handshake message is sent
 */
public class ListeningThreadAsServer extends Thread {
    private ServerSocket servSocket;
    private String peerID;
    private Socket peerSocket;
    private RemotePeerHandlerAsClientThread clientThread;

	/*
	Constructor
	*/
    public ListeningThreadAsServer(ServerSocket listeningSocket, String peerID) {
        this.servSocket = listeningSocket;
        this.peerID = peerID;
    }


    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            peerSocket = servSocket.accept();
            clientThread = new RemotePeerHandlerAsClientThread(peerSocket, peerID);
            clientThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
