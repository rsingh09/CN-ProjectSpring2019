package bittorrent;

/**
 *
 * @author kaush
 * TO-DO populate all the attributes of Remote Peer Info
 */
public class Peers
{
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

	public int getPeerIndex() {
		return peerIndex;
	}

	public void setPeerIndex(int peerIndex) {
		this.peerIndex = peerIndex;
	}

	public boolean isFirstPeer(){
		return this.peerIndex == 1;
	}

	public int getPeerID() {
		return peerID;
	}

	public Peers(int peerID, String hostName, int listeningPort, boolean hasFile) {
		super();
		this.peerID = peerID;
		this.hostName = hostName;
		this.listeningPort = listeningPort;
		this.hasFile = hasFile;
	}

	public void setPeerID(int peerID) {
		this.peerID = peerID;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setListeningPort(int listeningPort) {
		this.listeningPort = listeningPort;
	}

	public void setHasFile(boolean hasFile) {
		this.hasFile = fileInfo == 1 ? true : false ;
	}

	public int getPeerID() {
		return peerID;
	}

	public String getHostName() {
		return hostName;
	}

	public int getListeningPort() {
		return listeningPort;
	}

	public boolean getHasFile() {
		return hasFile;
	}

}
