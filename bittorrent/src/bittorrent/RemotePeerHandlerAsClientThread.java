package bittorrent;

import java.io.*;
import java.net.InetAddress;
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
    private ObjectInputStream in;
    
    boolean handshakeReceived = false;//stream read from the socket
	/*
	Constructor
	*/
    public RemotePeerHandlerAsClientThread(int peerID, String hostname, int port,
    		int connectionType,Socket peerSocket,ObjectOutputStream obStream,
    		ObjectInputStream inStream) {
        // TODO Auto-generated constructor stub
        this.remotePeerId = peerID;
        this.hostname = hostname;
        this.port = port;
        this.connectionType = connectionType;
        out = obStream;
        in = inStream;
        clientSocket = peerSocket;  
    }
	/*
	Constructor
	*/
    public RemotePeerHandlerAsClientThread(Socket peerSocket, String peerID,ObjectOutputStream obStream,
    		ObjectInputStream inStream) 
    {
        // TODO Auto-generated constructor stub
        remotePeerId = Integer.parseInt(peerID);
        out = obStream;
        in = inStream;
        clientSocket = peerSocket;
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
//        	InetAddress address = InetAddress.getByName(hostname); 
//            clientSocket = new Socket(address, port);
//            out = new ObjectOutputStream(clientSocket.getOutputStream());
//            in = new ObjectInputStream(clientSocket.getInputStream());
//            
//            HandshakeMessage handshake = new HandshakeMessage(Integer.toString(remotePeerId));
//            out.writeObject(handshake);
//            out.flush();
//            
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//    

    @Override
    public void run() {   	
    	System.out.println("Listening to the messages");
    	try
    	{
    		while(true)
    		{
    			Object ob = in.readObject();
    			if(ob instanceof HandshakeMessage)
    			{
    				HandshakeMessage hs = (HandshakeMessage) ob;
    				System.out.println("Received a handshake message");
    				String peerId = new String(hs.getPeerID());
    				HandshakeMessage handshake = new HandshakeMessage(peerId);
					
					out.writeObject(handshake);
					out.flush();
    			}
    			else
    			{
    				
    			}
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		try {
				in.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}

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
            out.writeObject(new HandshakeMessage(Integer.toString(remotePeerId)));
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
