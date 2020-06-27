package NetWork;

import java.io.IOException;
import java.util.Random;

class MsgHandler{
    public static void InterestHandler(Peer Peer,MsgManager msg,int i) throws IOException {
    	MsgManager tmpmsg = null;
    	if(i==1) {
    		Peer.setInterested_Peer(true);
    		tmpmsg = new MsgManager("INTERESTED");
    		Peer.setPiece(msg.getIndex());
    	}
    	else if(i==0) {
    		Peer.setInterested_Peer(false);
    		tmpmsg = new MsgManager("NOTINTERESTED");
            Peer.setPiece(msg.getIndex());
    	}        
		tmpmsg.sendMsg(Peer.getSocket().getOutputStream());       
    }
    
    public static void FileHandler(MsgManager Msg,Peer peer,int i) throws IOException {
        Msg.sendMsg(peer.getSocket().getOutputStream());
        if(i == 1) {
        peer.setInterestMon(true);
        peer.setInterested_Peer(false);
        }
        else if(i == 0){
            peer.setInterestMon(false);
            peer.setInterested_Peer(true);
        }
    }
    
    public static void PreferedHandler(Peer peer,int i) throws IOException {
    	MsgManager msg = null;
    	if(i==1) {
    		peer.setChoked(false);
    		peer.setPreferedNeighbor(true);
    		msg = new MsgManager("UNCHOKE");
        }
    	else if (i ==0) {
        	peer.setChoked(true);
            peer.setPreferedNeighbor(false);
            msg = new MsgManager("CHOKE");
    	}
        msg.sendMsg(peer.getSocket().getOutputStream());
    }
    
    public static void OptimisticHandler(Peer peer,int i) throws IOException {
    	MsgManager msg = null;
    	if(i==1) {
    	peer.setChoked(false);
        peer.setOptimisticNb(true);
        msg = new MsgManager("UNCHOKE");
        }
    	else if (i==0) {
            peer.setChoked(true);
            peer.setOptimisticNb(false);
            msg = new MsgManager("CHOKE");
        }
        msg.sendMsg(peer.getSocket().getOutputStream());
    }
}