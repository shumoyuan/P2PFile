package NetWork;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

public class Peer {
	private int PID;
    private Boolean chokeMon;
    private Boolean isChoked;
    private Boolean interestMon;
    private Boolean isInterested;
    private Boolean preferedNb;
    private Boolean optimisticNb;
    private int transNumber;
    private int transRate;
    private BitSet data_peer;
    private ArrayList<Integer> chunkList_peer; 
    private Boolean haveFile_peer;
    private String address;
    private int port;
    private Socket socket;
    
    Peer(String[] peerInfo) {
        if (peerInfo.length == 4) {
            try {
                PID = Integer.parseInt(peerInfo[0]);
            } catch (NumberFormatException e) {
                System.out.print("invalid pid." + peerInfo[0]);
                e.printStackTrace();
            }

            chokeMon = true;
            isChoked = true;
            interestMon = false;
            isInterested = false;
            preferedNb = false;
            optimisticNb = false;
            transRate = 0;
            transNumber = 0;
            address = peerInfo[1];
            try {
                port = Integer.parseInt(peerInfo[2]);
            } catch (NumberFormatException e) {
                System.out.print("invalid port" + peerInfo[2]);
                e.printStackTrace();
            }

            int haveFile = 0;
            try {
                haveFile = Integer.parseInt(peerInfo[3]);
            } catch (NumberFormatException e) {
                System.out.print("invalid have file info: " + peerInfo[3]);
                e.printStackTrace();
            }
            this.haveFile_peer = haveFile == 1;
            data_peer = new BitSet(8*Config.getPieceNum());
	        chunkList_peer = new ArrayList<>();
	        if (haveFile_peer) {
	            for (int i = 0; i < Config.getPieceNum(); i++) {
	                chunkList_peer.add(i);
	            }
	        	for(int i = 0; i < 8*Config.getPieceNum();i++) {
	        		if((i+1)%8==0) {
	        			data_peer.set(i);
	        		}
	        	}

	        } else {
	        	data_peer.set(0, 8*Config.getPieceNum(), false);
	        }
            
            
        } else {
            try {
                throw new Exception("invalid peer info");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    int getTransNumber() {
        return transNumber;
    }

    void setTransNumber(int transNumber) {
        this.transNumber = transNumber;
    }

    synchronized void incTransNumber() {
        this.transNumber++;
    }

    int getPID() {
        return PID;
    }

    void setPID(int PID) {
        this.PID = PID;
    }

    Boolean getChokeMon() {
        return chokeMon;
    }

    synchronized void setChokeMon(Boolean chokeMon) {
        this.chokeMon = chokeMon;
    }

    Boolean isChoked() {
        return isChoked;
    }

    synchronized void setChoked(Boolean choked) {
        this.isChoked = choked;
    }

    Boolean getInterestMon() {
        return interestMon;
    }

    synchronized void setInterestMon(Boolean interestMon) {
        this.interestMon = interestMon;
    }

    Boolean isInterested_Peer() {
        return isInterested;
    }

    synchronized void setInterested_Peer(Boolean isInterested) {
        this.isInterested = isInterested;
    }

    int getTransRate() {
        return transRate;
    }

    void setTransRate(int transRate) {
        this.transRate = transRate;
    }

    Boolean getHaveFile() {
        return haveFile_peer;
    }
    
    boolean isInterested(){
        return !chunkList_peer.isEmpty();
    }

    void setHaveFile(Boolean haveFile) {
        this.haveFile_peer = haveFile;
    }
    
    synchronized void removeInterest(int index){
        if (chunkList_peer.contains(index)) {
        	chunkList_peer.remove(chunkList_peer.indexOf(index));
        }
    }
    
    boolean isInterested(int index){
        return chunkList_peer.contains(index);
    }
    
    synchronized int randomIndex(Random r){
        if (chunkList_peer.size() == 0){
            try {
                throw new Exception("nothing in interest list");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return chunkList_peer.get(r.nextInt(chunkList_peer.size()));

    }
    synchronized void setInterest(int intex){
        if(!isInterested(intex)) {
        	chunkList_peer.add(intex);
        }
    }

   

    synchronized void setPiece(int index) {
        if (index < Config.getPieceNum()) {
        	data_peer.set(8*index-1);
        }

        for (int i : data_peer.toByteArray()) {
            if (i==0) {
                return;
            }
        }
        haveFile_peer = true;
    }

    Socket getSocket() {
        return socket;
    }

    Boolean setSocket() throws IOException {
        socket = new Socket(address, port);
        return socket.isConnected();
    }

    Boolean getpreferedNb() {
        return preferedNb;
    }

    void setPreferedNeighbor(Boolean preferedNb) {
        this.preferedNb = preferedNb;
    }

    Boolean getOptimisticNb() {
        return optimisticNb;
    }

    void setOptimisticNb(Boolean optimisticNb) {
        this.optimisticNb = optimisticNb;
    }

    boolean setSocket(Socket socket) {
        if(socket != null) {
            this.socket = socket;
            return true;
        } else {
            return false;
        }

    }

    String getAddress() {
        return address;
    }

    int getPort() {
        return port;
    }
}
