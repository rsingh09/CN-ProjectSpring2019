package trialSelector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageHandler extends Thread{
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
            SocketChannel clientSocketChannel = UtilityClass.allPeerMap.get(message.getPeerID()).socketChannel;
            System.out.println("Client Socket Channel should be the same I printed before" + clientSocketChannel);
            clientSocketChannel.write(transformObject(reply));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private ByteBuffer transformObject(HandshakeMessage handshakeMsg) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(handshakeMsg);
        oos.flush();
        byte[] data = bos.toByteArray();
        ByteBuffer buf = ByteBuffer.wrap(data);
        return buf;
    }
}
