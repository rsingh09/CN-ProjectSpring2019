package trialSelector;

import java.nio.channels.Selector;
import java.util.concurrent.ConcurrentHashMap;

public class UtilityClass 
{
	public static ConcurrentHashMap<Integer, PeerInfo> allPeerMap = new ConcurrentHashMap<Integer,PeerInfo>();
	public static Selector selectorP2P;
	public static int currentPeerID;
	
}
