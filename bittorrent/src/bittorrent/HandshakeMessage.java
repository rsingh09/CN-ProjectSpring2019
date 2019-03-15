package bittorrent;

/**
 * Class that populates the handshake message that is sent once the connection is established
 */
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class HandshakeMessage implements Serializable {
    private byte[] header = new byte[18];

    private byte[] peerID = new byte[4];

    private byte[] zeroBits = new byte[10];

    private String headerMessage = "P2PFILESHARINGPROJ";

	/*
	Constructor
	*/
    public HandshakeMessage(String id) {
        try {
            header = headerMessage.getBytes(StandardCharsets.UTF_8);
            zeroBits = "0000000000".getBytes(StandardCharsets.UTF_8);
            this.peerID = id.getBytes(StandardCharsets.UTF_8);

        } catch (Exception e) {
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
