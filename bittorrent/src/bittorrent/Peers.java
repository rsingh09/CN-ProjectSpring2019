package bittorrent;

/**
 * @author kaush
 * TO-DO populate all the attributes of Remote Peer Info
 */
public class Peers {
    int peerID;
    String hostName;
    String listeningPort;
    int fileInfo;
    boolean hasFile;
    int peerIndex;

//	public Peers(int id, String host, String port, boolean hasFile, int lineNumber) {
//		this.peerID = id;
//		this.hostName = host;
//		this.listeningPort = port;
//		this.hasFile = hasFile;
//		this.peerIndex = lineNumber;
//	}

    public Peers(int id, String host, String port, int fileInfo, int lineNumber) {
        this.peerID = id;
        this.hostName = host;
        this.listeningPort = port;
        this.fileInfo = fileInfo;
        this.peerIndex = lineNumber;
    }

    public Peers(int peerID, String hostName, String listeningPort, boolean hasFile) {
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

    public String getListeningPort() {
        return listeningPort;
    }

//	public int getPeerID() {
//		return peerID;
//	}

    public void setListeningPort(String listeningPort) {
        this.listeningPort = listeningPort;
    }

    public boolean getHasFile() {
        return hasFile;
    }

    public void setHasFile(boolean hasFile) {
        this.hasFile = fileInfo == 1;
    }

}
