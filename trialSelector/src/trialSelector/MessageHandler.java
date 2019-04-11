package trialSelector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

import static trialSelector.UtilityClass.transformObject;

public class MessageHandler extends Thread{
    SocketChannel ch;

    public MessageHandler(SocketChannel keyChannel){
        ch = keyChannel;
    }
    ConcurrentLinkedQueue<Object> messagesQueue = new ConcurrentLinkedQueue<Object>();
    public void run(){

        if(!messagesQueue.isEmpty()){
            Object receivedMessage = messagesQueue.poll();
            System.out.println(receivedMessage instanceof  HandshakeMessage);

            if(receivedMessage instanceof HandshakeMessage){
                System.out.println("Message Handler Starts polling for messages");
                handleHandshakeMessage((HandshakeMessage)receivedMessage);
            }

        }
    }


    private void handleHandshakeMessage(HandshakeMessage message) {
        UtilityClass.allPeerMap.get(message.getPeerID()).isHandshakeCompleted = true;
        HandshakeMessage reply = new HandshakeMessage(UtilityClass.currentPeerID);
        try {
            System.out.println("Peer ID "+ message.getPeerID());

            System.out.println("Client Socket Channel should be the same I printed before" + ch);
            ch.write(transformObject(reply));
        }   catch (IOException e) {
            e.printStackTrace();
        }}
}


