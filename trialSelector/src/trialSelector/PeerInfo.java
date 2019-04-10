package trialSelector;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author kaush
 * TO-DO populate all the attributes of Remote Peer Info
 */
public class PeerInfo {
    int peerID;
    String hostName;
    int listeningPort;
    int fileInfo;
    boolean hasFile;
    int peerIndex;
    public SocketChannel socketChannel;
    public ByteBuffer socketBuffer;

	/*
		Constructor
	*/
    public PeerInfo(int id, String host, int port, int fileInfo, int lineNumber) {
        this.peerID = id;
        this.hostName = host;
        this.listeningPort = port;
        this.fileInfo = fileInfo;
        this.peerIndex = lineNumber;
    }
	/*
		Constructor
	*/
    public PeerInfo(int peerID, String hostName, int listeningPort, boolean hasFile) {
        super();
        this.peerID = peerID;
        this.hostName = hostName;
        this.listeningPort = listeningPort;
        this.hasFile = hasFile;
    }

    public int getPeerIndex() {
        return peerIndex;
    }

    public void setPeerIndex(int peerIndex) {
        this.peerIndex = peerIndex;
    }

    public boolean isFirstPeer() {
        return this.peerIndex == 1;
    }

    public int getPeerID() {
        return peerID;
    }

    public void setPeerID(int peerID) {
        this.peerID = peerID;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public void setListeningPort(int listeningPort) {
        this.listeningPort = listeningPort;
    }

    public boolean getHasFile() {
        return hasFile;
    }

    public void setHasFile(boolean hasFile) {
        this.hasFile = fileInfo == 1;
    }

}
