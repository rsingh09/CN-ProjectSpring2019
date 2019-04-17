package trialSelector;

//import jdk.jshell.execution.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;
//import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Level;

public class peerProcess {
	private static EchoServer server;
    private static ScheduledExecutorService scheduler;
//    private static BitTorrentLogger bitTorrentLogger = BitTorrentLogger.getInstance();
    private static BitTorrentLogger bitTorrentLogger = new BitTorrentLogger();

	public static void main(String args[]) {
		int currentPeerID = Integer.parseInt(args[0]);
		UtilityClass.currentPeerID = currentPeerID;

		// iterate to send connect requests to the servers.
		// Once accepting the connection, assign the socket channel to the selector

		ReadConfigurations.readPeerInfo();
		ReadConfigurations.readCommonConfig();

		String directoryPath = System.getProperty("user.dir") + File.separator + "peer_" + currentPeerID;
		//System.out.println("Current Directory" + directoryPath);
		//System.out.println(directoryPath);
		File directory = new File(directoryPath);
		if(!directory.exists()){
			directory.mkdirs();
		}

        if(UtilityClass.allPeerMap.get(currentPeerID).hasFile == 1){
        	System.out.println("Peer " + currentPeerID + " has the complete file ");
			try{
				Splitter.splitFileToPieces(CommonProperties.fileName, CommonProperties.pieceSize, currentPeerID);
			} catch(Exception e){
				e.printStackTrace();
			}
		}

		int totalSplits = CommonProperties.fileSize/CommonProperties.pieceSize;
		long remainingBytes = CommonProperties.fileSize % CommonProperties.pieceSize;

		UtilityClass.totalSplitParts = remainingBytes > 0 ? totalSplits +1 : totalSplits;

		//Initialize my own bitfield
		for (Integer peerID : UtilityClass.allPeerMap.keySet())
		{
			PeerInfo infoObj = UtilityClass.allPeerMap.get(peerID);
			if(infoObj.hasFile == 1){

				for(int i=0 ; i < UtilityClass.totalSplitParts; i++){
					infoObj.bitfield.set(i);
				}
			}
		};

		createServer(UtilityClass.getCurrentPeerInfo());
		
		for (int id : UtilityClass.allPeerMap.keySet()) {
			if (id == UtilityClass.currentPeerID) {
				break;
				//continue;
			} else {
				System.out.println("Multiple id : " + id);
				createSocketChannels(UtilityClass.allPeerMap.get(id));
				System.out.println("id : " + id);
			}
		}

        scheduler = Executors.newScheduledThreadPool(2);
        //StartShutdownProcess();
        //determineKPreferredNeighborsAndSendUnchoke(CommonProperties.getUnchokingInterval());
        sendUnchokeMessageToOptimisticallyUnchokedPeer(CommonProperties.optimisticUnchokingInterval);
	}


	private static void createServer(PeerInfo peerInfo) {
		try {
			server = new EchoServer();
			server.start();
			String msg = "Created Peer" + peerInfo.peerID;
			String lvl = "Info";
			bitTorrentLogger.log(msg, Level.INFO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void createSocketChannels(PeerInfo peerInfo) {
		//EchoClient client = new EchoClient();
		try {
			UtilityClass.sendTcpRequest(peerInfo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//client.start();
		//client.sendMessage(handshakeMsg);
	}

/*	public static File createDirectory(String directoryPath) throws IOException {
		File dir = new File(directoryPath);
		if (dir.exists()) {
			return dir;
		}
		if (dir.mkdirs()) {
			return dir;
		}
		throw new IOException("Failed to create directory '" + dir.getAbsolutePath() + "' for an unknown reason.");
	}*/

    private static void sendUnchokeMessageToOptimisticallyUnchokedPeer(int optimisticUnchokingInterval) {
        final Runnable DetermineOptimisticallyUnchokedPeer = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("I will optimistically unchoke now");
                    int k = CommonProperties.getNumberOfPreferredNeighbors();

                    ArrayList<PeerInfo> interestedPeers = new ArrayList<>();
                    bitTorrentLogger.log("Size of interestedPeers list inside sendUnchokeMessageToOptimisticallyUnchokedPeer method: " + UtilityClass.intersetedPeers.size(), Level.WARNING);
                    for (int id : UtilityClass.intersetedPeers) {
                        //System.out.println("The id is : " + id);
                        interestedPeers.add(UtilityClass.allPeerMap.get(id));
                    }

                if (interestedPeers.size() > 0 && interestedPeers.size() > k) { // commenting out for test purpose
//                    if (true) {

//                        Random rand = new Random();

                        // Generate random integers in range 0 to 999
//                        int randomNum = rand.nextInt(interestedPeers.size());
                        int randomNum;
                        if (interestedPeers.size() > k + 1) {
                            randomNum = ThreadLocalRandom.current().nextInt(k + 1, interestedPeers.size());
                        } else {
                            randomNum = ThreadLocalRandom.current().nextInt(interestedPeers.size(), k + 1);
                            return;
                        }
//                    int randomNum = ThreadLocalRandom.current().nextInt(k + 1, interestedPeers.size());
                        System.out.println("Random num is " + randomNum);
                        if (randomNum < interestedPeers.size()) {
                            PeerInfo peerPara = interestedPeers.get(randomNum);

                            if (peerPara.peerState != PeerState.UNCHOKED) {
                                Message sendMessage = new Message(UtilityClass.currentPeerID, PeerConstants.PEER_UNCHOKED);
                                bitTorrentLogger.log("Peer " + UtilityClass.currentPeerID +
                                        "has the optimistically unchoked neighbor" + peerPara.peerID, Level.INFO);
                                try {
//							peerPara.peerHandler.peerObjectOutStream.writeObject(sendMessage);
                                    peerPara.peerSocketChannel.write(UtilityClass.transformObject(sendMessage));

                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
//                    bitTorrentLogger.log(e.getMessage(), Level.SEVERE);
                }
            }
        };
        scheduler.scheduleAtFixedRate(DetermineOptimisticallyUnchokedPeer, optimisticUnchokingInterval,
                optimisticUnchokingInterval, TimeUnit.SECONDS);
    }

    private static void determineKPreferredNeighborsAndSendUnchoke(int unchokingInterval) {
        final Runnable kPreferredNeighbors = new Runnable() {
            @Override
            public void run() {
                System.out.println("I will determine k preferred neighbors now");

                int kPreferredNeighbors = CommonProperties.getNumberOfPreferredNeighbors();
                int count = 0;
                Random rand = new Random();


                ArrayList<PeerInfo> interestedPeers = new ArrayList<>();
                String preferredNeighbors = "";

                for (int id : UtilityClass.intersetedPeers) {
                    System.out.println("The id is : " + id);
                    preferredNeighbors = preferredNeighbors + id + ", ";
                    interestedPeers.add(UtilityClass.allPeerMap.get(id));
                }

                bitTorrentLogger.log("Peer " + UtilityClass.currentPeerID + " chooses the following as the preferred neighbors " + preferredNeighbors, Level.INFO);

                Iterator<PeerInfo> it = interestedPeers.iterator();
                if (interestedPeers.size() > 0) {
                    System.out.println("Calculating the k preferred neighbors");

                    if (UtilityClass.allPeerMap.get(UtilityClass.currentPeerID).hasFile == 1)//check for complete file
                    {
                        int rnd = rand.nextInt(kPreferredNeighbors);
                        System.out.println("Random number : " + rnd);
                        UtilityClass.kNeighbours.add(interestedPeers.get(rnd).peerID);

                        PeerInfo sendPeer = interestedPeers.get(rnd);
                        Message sendMessage = new Message(UtilityClass.currentPeerID, PeerConstants.PEER_UNCHOKED);

                        if (sendPeer.peerState != PeerState.UNCHOKED) {
                            try {
                                System.out.println(sendPeer.peerID + "is getting unchoke now");
                                sendPeer.peerSocketChannel.write(UtilityClass.transformObject(sendMessage));
//								sendPeer.peerHandler.peerObjectOutStream.writeObject(sendMessage);
//								sendPeer.peerHandler.peerObjectOutStream.flush();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } else {
                        interestedPeers.sort(new Comparator<PeerInfo>() {
                            @Override
                            public int compare(PeerInfo o1, PeerInfo o2) {
                                if (o1.downloadRate == o2.downloadRate) {
                                    return rand.nextInt(2);
                                }
                                return (o1.downloadRate - o2.downloadRate);    //sort in decreasing order
                            }
                        });

                        while (interestedPeers.size() > kPreferredNeighbors && count < kPreferredNeighbors) {
                            UtilityClass.kNeighbours.clear();       //check
                            PeerInfo peerSelected = it.next();
                            UtilityClass.kNeighbours.add(peerSelected.peerID);
                            count++;
                            if (peerSelected.peerState == PeerState.CHOKED) {
                                Message sendMessage = new Message(UtilityClass.currentPeerID, PeerConstants.UNCHOKE);

                                try {
                                    peerSelected.peerSocketChannel.write(UtilityClass.transformObject(sendMessage));
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        };

        try {
            final ScheduledFuture<?> kNeighborDeterminerHandle =
                    scheduler.scheduleAtFixedRate(kPreferredNeighbors, 5, 5, TimeUnit.SECONDS);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
