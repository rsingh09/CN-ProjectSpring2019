package bittorrent;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
            
            System.out.println("A new client is connected to the server " + peerSocket);
            ObjectOutputStream obOutStream = new ObjectOutputStream(peerSocket.getOutputStream());
            ObjectInputStream obInStream = new ObjectInputStream(peerSocket.getInputStream());
            
            System.out.println("Assigning the client handler for the connection");
            
            clientThread = new RemotePeerHandlerAsClientThread(peerSocket, peerID,obOutStream, obInStream);
            clientThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
