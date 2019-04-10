package trialSelector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class EchoClient {
    private static SocketChannel client;
    private static ByteBuffer buffer;
    //private static EchoClient instance;

    public EchoClient(PeerInfo peerInfoObj) {
        try {
            client = SocketChannel.open(new InetSocketAddress(peerInfoObj.hostName, peerInfoObj.listeningPort));
            peerInfoObj.socketChannel = client;
            System.out.println("Socket Channel for the Peer ID "+peerInfoObj.peerID + peerInfoObj.socketChannel);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        buffer = ByteBuffer.allocate(CommonProperties.pieceSize + 10);
        // TODO Auto-generated constructor stub
    }

    public String sendMessage(HandshakeMessage msg) {
        String response = null;
        try {
            buffer = UtilityClass.transformObject(msg);
            client.write(buffer);
            buffer.clear();
            client.read(buffer);
            response = new String(buffer.array()).trim();
            //System.out.println("response=" + response);
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;

    }
}
