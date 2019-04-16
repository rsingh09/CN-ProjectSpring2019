package trialSelector;

import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;

public class ReadConfigurations
{
	public static void readPeerInfo() {
        File file = new File(System.getProperty("user.dir") + File.separator + "PeerInfo.cfg");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            int index = 1;
            while ((line = br.readLine()) != null) 
            {
                String[] tokens = line.split(" ");
                UtilityClass.allPeerMap.put(Integer.parseInt(tokens[0]), new PeerInfo(Integer.parseInt(tokens[0]),
                tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), index));
                index++;
            }
            br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block Add in logger
			e.printStackTrace();
		}
	}

	public static void readCommonConfig(){
        try{
            String newLine;
            BufferedReader in = new BufferedReader(new FileReader("Common.cfg"));
            while((newLine = in.readLine()) != null){
                String[] params = newLine.split("\\s+");
                switch(params[0]) {
                    case "NumberOfPreferredNeighbors":
                        CommonProperties.numberOfPreferredNeighbors = Integer.parseInt(params[1]);
                        break;
                    case "UnchokingInterval":
                        CommonProperties.unchokingInterval = Integer.parseInt(params[1]);
                        break;
                    case "OptimisticUnchokingInterval":
                        CommonProperties.optimisticUnchokingInterval = Integer.parseInt(params[1]);
                        break;
                    case "FileName":
                        CommonProperties.fileName = params[1];
                        break;
                    case "FileSize":
                        CommonProperties.fileSize = Integer.parseInt(params[1]);
                        break;
                    case "PieceSize":
                        CommonProperties.pieceSize = Integer.parseInt(params[1]);
                        break;
                    default :

                }
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
