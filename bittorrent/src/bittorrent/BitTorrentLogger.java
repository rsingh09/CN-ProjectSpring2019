package bittorrent;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class BitTorrentLogger {
    boolean append = true;

    public void WriteToLog(int peerId, String msg, String level) {
        Logger logger = Logger.getLogger(Integer.toString(peerId));
        FileHandler handler = null;
        String fileName = "log_peer_" + peerId + ".log";
        File f = new File("fileName");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            handler = new FileHandler(fileName, append);
            handler.setFormatter(new SimpleFormatter());
        } catch (SecurityException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        logger.addHandler(handler);
        switch (level) {
            case "Info":
                logger.info(msg);
                break;
            case "Error":
                logger.severe(msg);
                break;
            case "Warning":
                logger.warning(msg);
                break;
        }
    }

    public void WriteToLog(String msg, String level) {
        Logger logger = Logger.getLogger("MyLog");
        FileHandler handler = null;
        String fileName = "general.log";
        File f = new File("fileName");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            handler = new FileHandler(fileName, append);
        } catch (SecurityException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        logger.addHandler(handler);
        switch (level) {
            case "Info":
                logger.info(msg);
                break;
            case "Error":
                logger.severe(msg);
                break;
            case "Warning":
                logger.warning(msg);
                break;
        }
    }
}
