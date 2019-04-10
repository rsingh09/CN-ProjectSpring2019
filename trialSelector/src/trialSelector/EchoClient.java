package trialSelector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class EchoClient {
    private static SocketChannel client;
    private static ByteBuffer buffer;
    private static EchoClient instance;

//    public static EchoClient start() {
//        if (instance == null)
//            instance = new EchoClient();
//
//        return instance;
//    }

//    public static void stop() throws IOException {
//        client.close();
//        buffer = null;
//    }

//    private EchoClient() {
//        try {
//            client = SocketChannel.open(new InetSocketAddress("localhost", c));
//            buffer = ByteBuffer.allocate(256);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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
            buffer = transformObject(msg);
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
