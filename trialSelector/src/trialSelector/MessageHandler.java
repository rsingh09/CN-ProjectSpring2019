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
            if(receivedMessage instanceof HandshakeMessage){
                System.out.println("Polling for Handshake Messages ");
                handleHandshakeMessage((HandshakeMessage)receivedMessage);

            } else if(receivedMessage instanceof ActualMessage){
                System.out.println("Polling for Actual Messages ");
                handleBitfieldMessage((ActualMessage) receivedMessage);
            }

        }
    }

    private void handleHandshakeMessage(HandshakeMessage message) {
        //message.peerID = 1001
        //Current Peer ID = 1002
        HandshakeMessage reply = new HandshakeMessage(UtilityClass.currentPeerID);
        try {
            System.out.println("Peer ID of the peer who is processing the handshake message"+ message.getPeerID());

            ch.write(transformObject(reply));

            UtilityClass.allPeerMap.get(message.getPeerID()).isHandshakeCompleted = true;

            //If I have complete file/any of one bits is set
            byte[] bitfieldByteArray = UtilityClass.allPeerMap.get(currentPeerID).bitfield.toByteArray();
            ActualMessage actualMessage = new ActualMessage();
            actualMessage.setMessageType(MessageTypes.BITFIELD);
            actualMessage.messagePayload = bitfieldByteArray;
            ch.write(transformActualObject(actualMessage));

        }   catch (IOException e) {
            e.printStackTrace();
        }}

        private void handleBitfieldMessage(ActualMessage message){
            try {
                System.out.println("Peer ID of the peer receiving the Bitfield" + currentPeerID);
            //Starting this part as peer two. Right now, peer 1 has written to my message queue
            //I pick this bitfield message, compare it to my own bitfield and then send him whether I am interested in his data
            if(message.getMessageType() == MessageTypes.BITFIELD){
               BitSet myOwnBitfield =  allPeerMap.get(currentPeerID).bitfield;
               //compare the message that I have received and send an interested message to the guy who actually sent me his bitfield
                //byte[] bitfieldByteArray = myOwnBitfield.toByteArray();
                //We will send interested message to him
                ActualMessage actualMessage = new ActualMessage();
                actualMessage.setMessageType(MessageTypes.INTERESTED);
                System.out.println("Peer ID sending the interested message "+ currentPeerID);
                ch.write(transformActualObject(actualMessage));

            }

            } catch(Exception e){
                e.printStackTrace();
            }

        }

}


