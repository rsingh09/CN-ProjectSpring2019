//package bittorrent;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Class that houses the implementation to send requests to peers acting as host
 * Sends handshake message to the server listener threads that are running
 *
 * @author kaush
 */
public class RemoteP2PThread extends Thread {

    private int remotePeerId;
    private String hostname;
    private int port;
    private int connectionType;

    private String selfID;

    //socket declaration
    private Socket clientSocket;
    private OutputStream outputStream;         //stream write to the socket
    private InputStream inputStream;          //stream read from the socket

    /*
    Constructor
    */
    public RemoteP2PThread(int peerID, String hostname, int port, int connectionType) {
        // TODO Auto-generated constructor stub
        this.remotePeerId = peerID;
        this.hostname = hostname;
        this.port = port;
        this.connectionType = connectionType;

        try {
            this.clientSocket = new Socket(this.hostname, this.port);
            this.outputStream = clientSocket.getOutputStream();
//            this.outputStream.flush();
            this.inputStream = clientSocket.getInputStream();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public RemoteP2PThread(Socket peerSocket, int connType, String selfID) {

        this.clientSocket = peerSocket;
        this.connectionType = connType;
        this.selfID = selfID;
        try {
            inputStream = peerSocket.getInputStream();
            outputStream = peerSocket.getOutputStream();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
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
//    private void createSocketAndStreams() {
//        try {
//            this.clientSocket = new Socket(getHostname(), getPort());
//            this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
//            this.outputStream.flush();
//            this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    @Override
    public void run() {

        try {

            boolean keepListening = true;

            if (this.connectionType == 1) {
                boolean isSuccess = sendHandshakeMessage();

                if (isSuccess) {
                    System.out.println("Handshake msg sent");


                    //now we'll keep listening for incoming messages
                    while (keepListening) {

                        // create a DataInputStream so we can read data from it.
                        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                        // read the list of messages from the socket
                        HandshakeMessage handshakeMessage = (HandshakeMessage) objectInputStream.readObject();

                        if (null != handshakeMessage && handshakeMessage.getHeader().equalsIgnoreCase("P2PFILESHARINGPROJ")) {
                            System.out.println(handshakeMessage.getPeerID());
                            break;
                        }

                    }
                } else {

                    System.out.println("Handshake msg failed, exiting...");
                    System.exit(1);

                }
            } else {
                while (keepListening) {
                    // create a DataInputStream so we can read data from it.
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                    // read the list of messages from the socket
                    HandshakeMessage msg = (HandshakeMessage) objectInputStream.readObject();

                    if (msg != null && msg.getHeader().equalsIgnoreCase("P2PFILESHARINGPROJ")) {
                        System.out.println(msg.getPeerID());
                        break;
                    }
                }

                boolean flag = sendHandshakeMessage();

                if (flag) {
                    System.out.println("Handshake ack been sent");
                } else {
                    System.out.println("Handshake ack sent failed");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    /*
    Function name : sendHandshakeMessage
    Parameters passed: None
    Return: Void
    Send hand shake messages
    */
    private boolean sendHandshakeMessage() {

        boolean flag = false;

        try {
            //stream write the message
            HandshakeMessage msg = new HandshakeMessage(Integer.toString(this.remotePeerId));
            System.out.println(msg.toString());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(msg);
            objectOutputStream.flush();
            outputStream.flush();
            flag = true;
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }

        return flag;
    }

}
