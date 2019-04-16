package trialSelector;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.*;

public class Splitter {
    private static String dir = System.getProperty("user.dir");
    private static final String suffix = ".dat";

    public static List<Path> splitFileToPieces(String fileName, int mBperSplit, int PeerID) throws IOException {
        dir = dir + File.separator + "peer_" + PeerID + File.separator;
        System.out.println("Directory of the split file for a peer "+dir);
        if (mBperSplit <= 0) {
            //System.out.println("Of course Eden has more bytes ");
            throw new IllegalArgumentException("MB per split must be more than zero");
        }

        List partFiles = new ArrayList();
        final long sourceSize = Files.size(Paths.get(fileName));
        int bytesPerSplit = mBperSplit;
        long numSplits = sourceSize / bytesPerSplit;
        int remainingBytes = (int) sourceSize % bytesPerSplit;
        //System.out.println("Source size"+sourceSize);

        /// Copy arrays
        //Convert file to bytes
        byte[] originalBytes = convertFileToBytes(fileName);
        int partNum = 0;
        //As long as there are parts
        while (partNum < numSplits) {
            //write bytes to a part file.
            //PartNumber =1

            copyBytesToPartFile(originalBytes, partFiles, partNum, bytesPerSplit, bytesPerSplit);
            ++partNum;
        }

        if (remainingBytes > 0) {
            copyBytesToPartFile(originalBytes, partFiles, partNum, bytesPerSplit, remainingBytes);
        }

        return partFiles;

    }
    private static byte[] convertFileToBytes(String location) throws IOException {
        RandomAccessFile f = new RandomAccessFile(location, "r");
        byte[] b = new byte[(int) f.length()];
        f.readFully(b);
        f.close();
        return b;
    }

    private static void writeBufferToFiles(byte[] buffer, String fileName) throws IOException {
        BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(fileName));
        bw.write(buffer);
        bw.close();
    }

    private static void copyBytesToPartFile(byte[] originalBytes, List partFiles, int partNum, int bytesPerSplit, int bufferSize) throws IOException {
        String partFileName = dir + "part" + partNum + suffix;
        byte[] b = new byte[bufferSize];
        System.arraycopy(originalBytes, (partNum * bytesPerSplit), b, 0, bufferSize);
        writeBufferToFiles(b, partFileName);
        partFiles.add(partFileName);
    }
}
