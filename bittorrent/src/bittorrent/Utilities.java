//package bittorrent;
//import java.io.*;
//import java.util.*; 
//public class Utilities {
//	
//	private BufferedReader br;
//	private BitTorrentLogger logFile;
//	private static Hashtable<String, Peers> remotePeerInfo = new Hashtable<String, Peers>();
//	
//	public List<Peers> ReadPeerConfig() {
//		File file = new File("PeerInfo.cfg");
//		List<Peers> peerList  = new ArrayList<Peers>();
//		try 
//		{
//			br = new BufferedReader(new FileReader(file));
////			String line;
//			while ((line = br.readLine())!=null)
//			{
//				String[] listValues = line.split(" ");
//				Peers tempPeer = new Peers();
//				if (listValues.length == 4)
//				{
//					tempPeer.setPeerID(Integer.parseInt(listValues[0]));
//					tempPeer.setHostName(listValues[1]);
//					tempPeer.setListeningPort(listValues[2]);
//					tempPeer.setHasFile(Boolean.parseBoolean(listValues[3]));
//					peerList.add(tempPeer);
//				}
//				else
//				{
//					throw new BitTorrentExceptions("Config contains junk values");
//				}					
//			}
//			br.close();
//			return peerList;
//		} catch (FileNotFoundException e) 
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block Add in logger
//			e.printStackTrace();
//		} catch (BitTorrentExceptions e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//}
