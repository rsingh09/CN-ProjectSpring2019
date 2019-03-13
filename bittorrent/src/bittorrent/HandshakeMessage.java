package bittorrent;

import java.io.Serializable;
import java.util.BitSet;

public class HandshakeMessage implements Serializable{
	private byte[] header = new byte[18];
	
	private byte[] peerID = new byte[4];
	
	private byte[] zeroBits = new byte[10];
	
	private String headerMessage = "P2PFILESHARINGPROJ";
	
	
	
	public HandshakeMessage(String id) {
		try {
			header = headerMessage.getBytes("UTF-8");
			zeroBits = "0000000000".getBytes("UTF-8");
			this.peerID = id.getBytes("UTF-8");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String getHeader() {
		return headerMessage;
	}
	
	public byte[] getZeroBits() {
		return zeroBits;
	}
	
	public byte[] getPeerID() {
		return peerID;
	}
//	public void setPeerID(int peerID) {
//		this.peerID = peerID;
//	}

}
