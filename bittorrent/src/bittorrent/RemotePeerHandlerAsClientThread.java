package bittorrent;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Class that houses the implementation to send requests to peers acting as host
 * Sends handshake message to the server listener threads that are running
 * @author kaush
 *
 */
public class RemotePeerHandlerAsClientThread extends Thread {

    private int remotePeerId;
    private String hostname;
    private int port;
    private int connectionType;

    //socket declaration
    private Socket clientSocket;
    private ObjectOutputStream out;         //stream write to the socket
    private ObjectInputStream in;          //stream read from the socket
	/*
	Constructor
	*/
    public RemotePeerHandlerAsClientThread(int peerID, String hostname, int port, int connectionType) {
        // TODO Auto-generated constructor stub
        this.remotePeerId = peerID;
        this.hostname = hostname;
        this.port = port;
        this.connectionType = connectionType;
    }
	/*
	Constructor
	*/
    public RemotePeerHandlerAsClientThread(Socket peerSocket, String peerID) {
        // TODO Auto-generated constructor stub
        this.remotePeerId = Integer.parseInt(peerID);
        this.clientSocket = peerSocket;
    }

    public int getRemotePeerId() {
        return remotePeerId;
    }

    public void setRemotePeerId(int remotePeerId) {
        this.remotePeerId = remotePeerId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(int connectionType) {
        this.connectionType = connectionType;
    }
	/*
	Function name : createSocketAndStreams
	Parameters passed: None
	Return: Void
	Create sockets and the streams
	*/
    private void createSocketAndStreams() {
        try {
            this.clientSocket = new Socket(getHostname(), getPort());
            this.out = new ObjectOutputStream(clientSocket.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        createSocketAndStreams();
        sendHandshakeMessage();

    }
	/*
	Function name : sendHandshakeMessage
	Parameters passed: None
	Return: Void
	Send hand shake messages
	*/
    void sendHandshakeMessage() {
        try {
            //stream write the message
            out.writeObject(new HandshakeMessage(Integer.toString(getRemotePeerId())));
            out.flush();
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }
	/*
	Function name : receiveHandShakeMessage
	Parameters passed: None
	Return: Void
	receives hand shake messages
	*/
    void receiveHandShakeMessage() {
//		byte[] receivedMsg = new byte[32];

        try {
//			in.read(receivedMsg);
            HandshakeMessage msg = (HandshakeMessage) in.readObject();
            System.out.println(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
