package bittorrent;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RemotePeerHandlerAsClientThread extends Thread {

    private int remotePeerId;
    private String hostname;
    private int port;
    private int connectionType;

    //socket declaration
    private Socket clientSocket;
    private ObjectOutputStream out;         //stream write to the socket
    private ObjectInputStream in;          //stream read from the socket

    public RemotePeerHandlerAsClientThread(int peerID, String hostname, int port, int connectionType) {
        // TODO Auto-generated constructor stub
        this.remotePeerId = peerID;
        this.hostname = hostname;
        this.port = port;
        this.connectionType = connectionType;
    }

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

    private void createSocketAndStreams() {
        try {
            this.clientSocket = new Socket(getHostname(), getPort());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
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

    void sendHandshakeMessage() {
        try {
            //stream write the message
            out.writeObject(new HandshakeMessage(Integer.toString(getRemotePeerId())));
            out.flush();
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }

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
