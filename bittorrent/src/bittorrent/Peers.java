package bittorrent;

public class Peers 
{
	int peerID;
	String hostName;
	String listeningPort;
	boolean hasFile;
	
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
	public void setListeningPort(String listeningPort) {
		this.listeningPort = listeningPort;
	}
	public boolean getHasFile() {
		return hasFile;
	}
	public void setHasFile(boolean hasFile) {
		this.hasFile = hasFile;
	}
}
 
