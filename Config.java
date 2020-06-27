package NetWork;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Random;

public class Config {
	
	
    private int PreferedNo;
    private int UnchokInterval;
    private int OptUnchokInterval;
    private String FileName;
    private int FileSize;
    private int PieceSize;
    private ArrayList<Peer> peers;
    static private int pieceNum;
    private int fileLeftChunk;

    private int myPid;
    private String myAddr;
    private int myPort;
    private Boolean myFile;
    //private byte[] data;
    private BitSet data;
    private ArrayList<Integer> chunkList; 
    private Boolean haveFile1;
    private int myIndex;
    private int totalDownload = 0;
    private boolean iscompleted=false;

    Config(int pid) throws IOException {
        myPid = pid;
        peers = new ArrayList<>();
        final String File = "Common.cfg";
        File file = new File("PeerInfo.cfg");
        InputStreamReader reader = new InputStreamReader(new FileInputStream(File));
        BufferedReader br = new BufferedReader(reader);
        int lineno = 0;

        String line = br.readLine();
        while (line != null){
            if (lineno == 0)
                PreferedNo =
                        Integer.parseInt(line.split(" ")[1]);
            else if (lineno == 1)
            	UnchokInterval =
                        Integer.parseInt(line.split(" ")[1]);
            else if (lineno == 2)
            	OptUnchokInterval =
                        Integer.parseInt(line.split(" ")[1]);
            else if (lineno == 3)
                FileName =
                        line.split(" ")[1];
            else if (lineno == 4)
                FileSize =
                        Integer.parseInt(line.split(" ")[1]);
            else if (lineno == 5)
                PieceSize =
                        Integer.parseInt(line.split(" ")[1]);
            line = br.readLine();
            lineno++;

        }

        pieceNum = FileSize / PieceSize;

        if (FileSize % PieceSize != 0) {
        	fileLeftChunk = FileSize % PieceSize;
            pieceNum++;
        } else {
        	fileLeftChunk = PieceSize;
        }

        reader = new InputStreamReader(new FileInputStream(file));
        br = new BufferedReader(reader);
        String temp = br.readLine();
        int myIndex = 0;
        while (temp != null) {
            String[] str = temp.split(" ");
            int pidTemp = 0;
            try {
                pidTemp = Integer.parseInt(str[0]);
            } catch (NumberFormatException e) {
                System.out.print("invalid pid. " + str[0]);
                e.printStackTrace();
            }

            if (pid != pidTemp) {
                Peer ptemp = new Peer(str);
                peers.add(ptemp);
            } else {
                myAddr = str[1];
                myPort = Integer.parseInt(str[2]);
                myFile = Integer.parseInt(str[3]) != 0;
                this.haveFile1 = myFile;
                data = new BitSet(8*pieceNum);
    	        //data = new byte[pieceNum];
    	        chunkList = new ArrayList<>();
    	        if (haveFile1) {
    	            /*for (int i = 0; i < data.length; i++) {
    	                data[i] = 1;
    	            }*/
    	        	for(int i = 0; i < 8*pieceNum;i++) {
    	        		if((i+1)%8==0) {
    	        			data.set(i);
    	        		}
    	        	}
    	        } else {
    	            for (int i = 0; i < pieceNum; i++) {
    	                //data[i] = 0;
    	                chunkList.add(i);
    	            }
    	            data.set(0, 8*pieceNum, false);
    	        }
               
                this.myIndex = myIndex;
            }
            myIndex++;
            temp = br.readLine();
        }
    }

    Boolean getHaveFile() {
        return haveFile1;
    }

    BitSet getData() {
    	return data;
    }
    
    /*byte[] getData() {
        return data;
    }*/
    
    synchronized void setPiece(int index) {
        if (index < pieceNum) {
            //data[index] = 1;
        	data.set(8*index-1);
        }

        for (int i : data.toByteArray()) {
            if (i==0) {
                return;
            }
        }
        haveFile1 = true;
    }
    
    synchronized void removeInterest(int index){
        if (chunkList.contains(index)) {
        	chunkList.remove(chunkList.indexOf(index));
        }
    }
    
    boolean isInterested(){
        return !chunkList.isEmpty();
    }

    int getLength(){
        return pieceNum;
    }
    
    public boolean isIscompleted() {
        return iscompleted;
    }

    public void setCompleted(boolean iscompleted) {
        this.iscompleted = iscompleted;
    }

    int getPreferedNo() {
        return PreferedNo;
    }

    int getUnchokInterval() {
        return UnchokInterval;
    }

    int getOptUnchokInterval() {
        return OptUnchokInterval;
    }

    String getFileName() {
        return FileName;
    }

    int getFileSize() {
        return FileSize;
    }

    int getPieceSize() {
        return PieceSize;
    }


    void setMyFile(Boolean myFile) {
        this.myFile = myFile;
    }

    static int getPieceNum() {
        return pieceNum;
    }

    int getFileLeftChunk() {
        return fileLeftChunk;
    }

    int getMyPid() {
        return myPid;
    }

    String getMyAddr() {
        return myAddr;
    }

    int getMyPort() {
        return myPort;
    }

    Boolean getMyFile() {
        return myFile;
    }

    boolean haveChunk(int index){
        return chunkList.contains(index);
    }

    
    int getMyIndex() {
        return myIndex;
    }

    ArrayList<Peer> getPeers() {
        return peers;
    }

    public int getTotal() {
        return totalDownload;
    }

    public void addTotal() {
        totalDownload++;
    }
    
 

}


