//package bittorrent;
//
//import java.util.*;
//
//public class PeersProcessHandler {
//	static Utilities utl = new Utilities();
//	static BitTorrentLogger log = new BitTorrentLogger();
//	public static void main(String[] args) {
//
//		List<Peers> peers = new ArrayList<Peers>();
//		peers = utl.ReadPeerConfig();
//
//		for (Peers p : peers) {
//			String msg = "Created Peer" + Integer.toString(p.getPeerID());
//			String lvl = "Info";
//
//			log.WriteToLog(p.peerID, msg, lvl);
//			//log.
//		}
//	}
//}
