package trialSelector;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.BitSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import static trialSelector.UtilityClass.*;

public class MessageHandler extends Thread{
    SocketChannel ch;

    public MessageHandler(SocketChannel keyChannel){
        ch = keyChannel;
    }
    ConcurrentLinkedQueue<Object> messagesQueue = new ConcurrentLinkedQueue<Object>();
    public void run(){

        if(!messagesQueue.isEmpty()){
            Object receivedMessage = messagesQueue.poll();
            System.out.println(messagesQueue.size());
            if(receivedMessage instanceof HandshakeMessage){
                handleHandshakeMessage((HandshakeMessage)receivedMessage);


            } else if(receivedMessage instanceof Message){
                switch(((Message)receivedMessage).getMessageType()){
                    case BITFIELD:
                        System.out.println("Handle Bitfield Message");
                        handleBitfieldMessage((Message) receivedMessage);
                        break;

                    case INTERESTED:
                        handleInterestedMessage();
                        break;

                }
            }

        }
    }

    private void handleHandshakeMessage(HandshakeMessage message) {
        try {

            System.out.println(currentPeerID + " is polling for handshake messages ");
            System.out.println(( message).getPeerID()+" is the peer ID I have to reply to");
            if(!allPeerMap.get(message.getPeerID()).isHandshakeSent){
                HandshakeMessage reply = new HandshakeMessage(UtilityClass.currentPeerID);
                ch.write(transformObject(reply));
                allPeerMap.get(message.getPeerID()).isHandshakeSent = true;
                //Send Bitfield messsage only if my Bitset is not empty
                if(!allPeerMap.get(currentPeerID).bitfield.isEmpty()){
                    sendBitfieldMessage((message).getPeerID());

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }}

        private void handleBitfieldMessage(Message message){
            try {
                System.out.println(currentPeerID + " is handling for Bitfield messages ");
                if(message.messagePayload != null){
                    PeerInfo remotePeer = allPeerMap.get(message.PeerID);
                    remotePeer.bitfield = ((BitSet) BitSet.valueOf(message.messagePayload).clone());
                    allPeerMap.put(message.PeerID, remotePeer);
                }

                int splitParts = totalSplitParts;

                System.out.println("The number of split parts are " + splitParts);

                BitSet currentPeerBitset = new BitSet(splitParts);
                currentPeerBitset = (BitSet) allPeerMap.get(currentPeerID).bitfield.clone();


                BitSet peerBitset = new BitSet(splitParts);
                peerBitset = (BitSet) BitSet.valueOf(message.messagePayload).clone();

                currentPeerBitset.flip(0,totalSplitParts);
                currentPeerBitset.and(peerBitset);
                if(!currentPeerBitset.isEmpty()) {sendInterestedMessage();
                }

                else {
                    Message messageToSend = new Message(MessageTypes.NOT_INTERESTED);
                    ch.write(UtilityClass.transformActualObject(messageToSend));
                }

            } catch(IOException e){
                e.printStackTrace();
            }

        }

    private void sendInterestedMessage() {
    }

    private void  handleInterestedMessage(){

    }
    private void sendBitfieldMessage(int remotePeerID) {
        try{
            System.out.println("Sending bitfield message from "+ currentPeerID + " to " + remotePeerID);
            byte[] bitfieldByteArray = UtilityClass.allPeerMap.get(currentPeerID).bitfield.toByteArray();
            Message actualMessage = new Message(currentPeerID, MessageTypes.BITFIELD);
            actualMessage.setMessageType(MessageTypes.BITFIELD);
            actualMessage.messagePayload = bitfieldByteArray;
            ch.write(transformActualObject(actualMessage));

        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
