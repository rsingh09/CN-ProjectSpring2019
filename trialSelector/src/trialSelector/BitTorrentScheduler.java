package trialSelector;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;

/**
 * Author: sajid
 * Date : 4/16/19
 * Time : 11:22 PM
 * Project Name: trialSelector
 */
public class BitTorrentScheduler {

    private static ScheduledExecutorService executorService;

    private static BitTorrentLogger bitTorrentLogger = BitTorrentLogger.getInstance();

    public BitTorrentScheduler(){}

    public void configureAndRunScheduler(){
        executorService = Executors.newScheduledThreadPool(2);
        //StartShutdownProcess();
        determineKPreferredNeighborsAndSendUnchoke(CommonProperties.getUnchokingInterval());
        sendUnchokeMessageToOptimisticallyUnchokedPeer(CommonProperties.optimisticUnchokingInterval);
    }



    private static void sendUnchokeMessageToOptimisticallyUnchokedPeer(int optimisticUnchokingInterval) {
        final Runnable iWillOptimisticallyUnchokeNow = new Runnable() {
            @Override
            public void run() {
                try {
//                    System.out.println("I will optimistically unchoke now");
                    int k = CommonProperties.getNumberOfPreferredNeighbors();

                    ArrayList<PeerInfo> interestedPeers = new ArrayList<>();
                    bitTorrentLogger.log("Size of interestedPeers list inside sendUnchokeMessageToOptimisticallyUnchokedPeer method: " + UtilityClass.intersetedPeers.size(), Level.WARNING);
                    for (int id : UtilityClass.intersetedPeers) {
                        System.out.println("The id is : " + id);
                        interestedPeers.add(UtilityClass.allPeerMap.get(id));
                    }

                    if (interestedPeers.size() > 0 && interestedPeers.size() > k) {

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

                                UtilityClass.kNeighbours.add(interestedPeers.get(randomNum).peerID);

                                PeerInfo peerToSendUnchoke = interestedPeers.get(randomNum);
                                Message unchokeMessage = new Message(UtilityClass.currentPeerID, PeerConstants.PEER_UNCHOKED);
                                bitTorrentLogger.log("Peer [" + UtilityClass.currentPeerID +
                                        "] has optimistically unchoked neighbor " + peerPara.peerID, Level.INFO);
                                sendChokeOrUnchokeMsg(peerToSendUnchoke, unchokeMessage);
                            }
                        }
                    }
                }catch (Exception e){
                    bitTorrentLogger.log(e.getMessage(), Level.SEVERE);
                }
            }


        };
        executorService.scheduleAtFixedRate(iWillOptimisticallyUnchokeNow, optimisticUnchokingInterval,
                optimisticUnchokingInterval, TimeUnit.SECONDS);
    }

    private static void determineKPreferredNeighborsAndSendUnchoke(int unchokingInterval) {
        final Runnable calculatingKPreferredNeighbors = () -> {
            try{
//            System.out.println("I will determine k preferred neighbors now");

            int kPreferredNeighbors = CommonProperties.getNumberOfPreferredNeighbors();
            int count = 0;
            Random rand = new Random();
            boolean randomSelection = false;


            ArrayList<PeerInfo> interestedPeersList = new ArrayList<>();
            String preferredNeighbors = "";

            for (int id : UtilityClass.intersetedPeers) {
                System.out.println("The id is : " + id);
                preferredNeighbors = preferredNeighbors + id + ", ";
                interestedPeersList.add(UtilityClass.allPeerMap.get(id));
            }

            bitTorrentLogger.log("Peer [" + UtilityClass.currentPeerID + "] chooses the following as the preferred neighbors " + preferredNeighbors, Level.INFO);

            Iterator<PeerInfo> it = interestedPeersList.iterator();

            if (interestedPeersList.size() > 0) {
                System.out.println("Calculating the k preferred neighbors");

                if (UtilityClass.allPeerMap.get(UtilityClass.currentPeerID).hasFile == 1)//check for complete file
                {
                    randomSelection = true;

//                    int rnd = rand.nextInt(kPreferredNeighbors);
//                    System.out.println("Random number : " + rnd);
//                    UtilityClass.kNeighbours.add(interestedPeersList.get(rnd).peerID);
//
//                    PeerInfo sendPeer = interestedPeersList.get(rnd);
//                    Message sendMessage = new Message(UtilityClass.currentPeerID, PeerConstants.PEER_UNCHOKED);
//
//                    if (sendPeer.peerState != PeerState.UNCHOKED) {
//
//                        sendChokeOrUnchokeMsg(sendPeer, sendMessage);
//                    }

                } else {
                    interestedPeersList.sort(new Comparator<PeerInfo>() {
                        @Override
                        public int compare(PeerInfo o1, PeerInfo o2) {
                            if (o1.downloadRate == o2.downloadRate) {
                                return rand.nextInt(2);
                            }
                            return (o1.downloadRate - o2.downloadRate);    //sort in decreasing order
                        }
                    });

//                    chooseKPrefNeighbors(kPreferredNeighbors, count, interestedPeersList, randomSelection);
                }

                chooseKPrefNeighbors(kPreferredNeighbors, count, interestedPeersList, randomSelection);
            }
        } catch (Exception ex){
                bitTorrentLogger.log(ex.getMessage(), Level.SEVERE);
                ex.printStackTrace();
            }
        };

        try {
            final ScheduledFuture<?> kNeighborDeterminerHandle =
                    executorService.scheduleAtFixedRate(calculatingKPreferredNeighbors, unchokingInterval, unchokingInterval, TimeUnit.SECONDS);
        } catch (Exception e) {
            bitTorrentLogger.log(e.getMessage(), Level.SEVERE);
        }

    }

    private static void chooseKPrefNeighbors(int kPreferredNeighbors, int count, ArrayList<PeerInfo> interestedPeersList, boolean randomSelection) {
        // ArrayList a holds list of numbers from 0 to interestedPeersList.size(),
        ArrayList<Integer> a = new ArrayList<>(interestedPeersList.size());
        for (int i = 0; i < interestedPeersList.size(); i++){ //to generate from 0-10 inclusive.
            a.add(i);
        }

        // if randomSelection is true, then we shuffle the arraylist a
        if (randomSelection) {
            Collections.shuffle(a);
        }

        // we get a sublist of size 'kPreferredNeighbors' from arraylist a to send unchoke messages to the peers
        // indexed in interestedPeers list
        ArrayList<Integer> a_unchoked_index = (ArrayList<Integer>) a.subList(0, kPreferredNeighbors);
        UtilityClass.kNeighbours.clear();

            while (interestedPeersList.size() > kPreferredNeighbors && count < kPreferredNeighbors) {
                PeerInfo peerTobeUnchoked = interestedPeersList.get(a_unchoked_index.get(count));
                UtilityClass.kNeighbours.add(peerTobeUnchoked.peerID);
                count++;
                if (peerTobeUnchoked.peerState != PeerState.UNCHOKED) {
                    peerTobeUnchoked.peerState = PeerState.UNCHOKED;
                    // updating peerInfo map
                    UtilityClass.allPeerMap.put(peerTobeUnchoked.peerID, peerTobeUnchoked);
                    Message sendMessage = new Message(UtilityClass.currentPeerID, PeerConstants.UNCHOKE);

                    sendChokeOrUnchokeMsg(peerTobeUnchoked, sendMessage);
                }
            }

            //Now we'll send choke messages to the rest of the peers who are in the interestedList

        ArrayList<Integer> a_choked_index = (ArrayList<Integer>) a.subList(kPreferredNeighbors, interestedPeersList.size());

            for(int i = 0; i < a_choked_index.size(); i++){
                PeerInfo peerTobeChoked = interestedPeersList.get(a_choked_index.get(i));
                Message sendMessage = new Message(UtilityClass.currentPeerID, PeerConstants.CHOKE);
                peerTobeChoked.peerState = PeerState.CHOKED;
                // updating peerInfo map
                UtilityClass.allPeerMap.put(peerTobeChoked.peerID, peerTobeChoked);

                sendChokeOrUnchokeMsg(peerTobeChoked, sendMessage);
            }

    }

    private static void sendChokeOrUnchokeMsg(PeerInfo peerToSendUnchoke, Message message) {
        try {
            MessageHandler messageHandler = UtilityClass.channelMessageHandlerMap.get(peerToSendUnchoke);
            messageHandler.messagesQueue.add(UtilityClass.transformObject(message));

        } catch (IOException e) {
            bitTorrentLogger.log(e.getMessage(), Level.SEVERE);
        }
    }
}
