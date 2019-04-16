package trialSelector;

public interface PeerConstants {
	//Message types
	public static final int CHOKE = 0;
	public static final int UNCHOKE = 1;
	public static final int INTERESTED = 2;
	public static final int NOT_INTERESTED =3;
	public static final int HAVE = 4;
	public static final int BITFIELD = 5;
	public static final int REQUEST = 6;
	public static final int PIECE = 7;
	public static final int SENDHAVE = 8;
	//Peer states
	public static final int PEER_CHOKED = 0;
	public static final int PEER_UNCHOKED = 1;
	public static final int PEER_INTERESTED = 2;
	public static final int PEER_NOT_INTERESTED =3;
	public static final int PEER_HAVE = 4;
	//other constants
	public static final String FILE_SUFFIX = ".dat";

}
