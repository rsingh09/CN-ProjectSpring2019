package trialSelector;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class HandshakeMessage implements Serializable {
	private String header;
	private int peerID;
	private byte[] zeroBits = new byte[10];
	/*
	 * Constructor
	 */
	public HandshakeMessage(int currentPeerID) 
	{
		try {
			header = "P2PFILESHARINGPROJ";
			peerID = currentPeerID;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] getZeroBits() {
		return zeroBits;
	}
	public int getPeerID() {
		return peerID;
	}

}


