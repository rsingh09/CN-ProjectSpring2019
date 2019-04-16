package trialSelector;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.BitSet;

/**
 * @author kaush
 * TO-DO populate all the attributes of Remote Peer Info
 */
enum PeerState{
    HAVE,
    UNCHOKED,
    CHOKED,
    INTERESTED,
    BITFIELD
}

public class PeerInfo {
    int peerID;
    String hostName;
    int listeningPort;
    int fileInfo;
    int hasFile;
    int peerIndex;
//    int peerState;
    PeerState peerState;
    Boolean isHandshakeSent;
    int downloadRate = 0;
    public BitSet bitfield;
    public SocketChannel peerSocketChannel;
    public static MessageHandler messageHandler;
    int numberOfPieces;




    /*
        Constructor
    */
    public PeerInfo(int id, String host, int port, int hasFile, int peerIndex) {
        this.peerID = id;
        this.hostName = host;
        this.listeningPort = port;
        this.hasFile = hasFile;
        this.peerIndex = peerIndex;
        this.isHandshakeSent = false;
        bitfield = new BitSet(UtilityClass.totalSplitParts);
    }

}
