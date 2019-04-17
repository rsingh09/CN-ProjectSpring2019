package trialSelector;

import java.io.*;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

 public class BitTorrentLogger {
     static Logger logger = Logger.getLogger(Integer.toString(UtilityClass.currentPeerID));
    private boolean append;

     protected BitTorrentLogger(){
         append = true;
         logger = Logger.getLogger(Integer.toString(UtilityClass.currentPeerID));
         FileHandler handler = null;
         String fileName = "log_peer_" + UtilityClass.currentPeerID + ".log";
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
     }

   private static BitTorrentLogger loggerInstance = null;

//     protected BitTorrentLogger(){
//          append = true;
//     }

     public static BitTorrentLogger getInstance(){
         if (null == loggerInstance){
             synchronized (BitTorrentLogger.class){
                 if(null == loggerInstance)
                     loggerInstance = new BitTorrentLogger();
             }
         }

         return loggerInstance;
     }

	/*
	Function name : log
	Parameters passed: Peer ID , Msg (message to be printed), level of the message Info/Warning/Severe
	Return: Void
	Writes into the log file based on based on the peer id
	*/
 /*
    public void log(String msg, Level level) {
        Logger logger = Logger.getLogger(Integer.toString(UtilityClass.currentPeerID));
        FileHandler handler = null;
        String fileName = "log_peer_" + UtilityClass.currentPeerID + ".log";
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

        logger.log(level, msg);
//        switch (level) {
//            case Level.INFO:
//                logger.info(msg);
//                break;
//            case "Error":
//                logger.severe(msg);
//                break;
//            case "Warning":
//                logger.warning(msg);
//                break;

    } */

     public void log(String msg, Level level) {
        logger.log(level, msg);
     }
	/*
	Function name : log
	Parameters passed: Msg (message to be printed), level of the message Info/Warning/Severe
	Return: Void
	Writes into the common log file 
	*/
//    public void log(String msg, String level) {
//        Logger logger = Logger.getLogger("MyLog");
//        FileHandler handler = null;
//        String fileName = "general.log";
//        File f = new File("fileName");
//        if (!f.exists()) {
//            try {
//                f.createNewFile();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        try {
//            handler = new FileHandler(fileName, append);
//        } catch (SecurityException | IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        logger.addHandler(handler);
//        switch (level) {
//            case "Info":
//                logger.info(msg);
//                break;
//            case "Error":
//                logger.severe(msg);
//                break;
//            case "Warning":
//                logger.warning(msg);
//                break;
//        }
//    }
}
