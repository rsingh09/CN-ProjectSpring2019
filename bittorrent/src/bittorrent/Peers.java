package bittorrent;

public class Peers {
	int peerID;
	String hostName;
	int listeningPort;
	boolean hasFile;

	public Peers() {

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
		this.hasFile = hasFile;
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
