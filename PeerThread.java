package NetWork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class PeerThread implements Runnable {
    private Peer guestPeer;
    private Random random;
    private ArrayList<Peer> peerList;
    private Logger logger;

    PeerThread(Peer peer) {
        guestPeer = peer;
        peerList = peerProcess.config.getPeers();
        random = new Random(System.currentTimeMillis());
        logger = peerProcess.logger;
    }

    @Override
    public void run() {
        try {
            while (true) {
                MsgManager temp = MsgManager.readMsg(guestPeer.getSocket());
                if(temp.getType()=="CHOKE") {
                    guestPeer.setChokeMon(true);
                    logger.LogWriter(guestPeer.getPID(),5);
                }
                else if(temp.getType()=="UNCHOKE") {
                	guestPeer.setChokeMon(false);
                    logger.LogWriter(guestPeer.getPID(),4);
                    if (guestPeer.isInterested_Peer()) {
                    	MsgManager request = new MsgManager("REQUEST", guestPeer.randomIndex(random));
                        request.sendMsg(guestPeer.getSocket().getOutputStream());
                    }
                }
                else if(temp.getType()=="INTERESTED") {
                    guestPeer.setInterestMon(true);
                    logger.LogWriter(guestPeer.getPID(),6);
                }
                else if(temp.getType()=="NOTINTERESTED") {
                    guestPeer.setInterestMon(false);
                    logger.LogWriter(guestPeer.getPID(),7);
                }
                else if(temp.getType()=="HAVE") {
                	logger.LogWriter(guestPeer.getPID(), temp.getIndex(),0);
                    if (peerProcess.config.haveChunk(temp.getIndex())) {
                    	MsgHandler.InterestHandler(guestPeer,temp,1);                   	
                        guestPeer.setInterest(temp.getIndex());
                        if (!guestPeer.getChokeMon() && guestPeer.isInterested_Peer()) {
                        	MsgManager have_request = new MsgManager("REQUEST", guestPeer.randomIndex(random));
                            have_request.sendMsg(guestPeer.getSocket().getOutputStream());
                        }
                    } else {
                    	MsgHandler.InterestHandler(guestPeer,temp,0);
                    }
                }
                else if(temp.getType()=="BITFIELD") {
                	MsgManager intermsg = new MsgManager("INTERESTED");
                    intermsg.sendMsg(guestPeer.getSocket().getOutputStream());
                }
                else if(temp.getType()=="REQUEST") {
                	if (!guestPeer.isChoked()) {
                		MsgManager piece = new MsgManager(peerProcess.fileManager.readMsg(temp.getIndex()));
                        piece.sendMsg(guestPeer.getSocket().getOutputStream());
                        guestPeer.incTransNumber();
                    } else {
                        break;
                    }
                }
                else if(temp.getType()=="PIECE") {
                	FileChunk chunkTemp = new FileChunk(temp);
                    peerProcess.config.addTotal();
                    logger.LogWriter(guestPeer.getPID(), peerProcess.config.getTotal(), temp.getIndex(),0);
                    if (peerProcess.config.getTotal() == peerProcess.config.getPieceNum()){
                        logger.LogWriter(guestPeer.getPID(),8);
                    }
                    peerProcess.fileManager.writeMsg(chunkTemp);
                    peerProcess.config.removeInterest(temp.getIndex());
                    peerProcess.config.setPiece(temp.getIndex());

                    MsgManager reply_have = new MsgManager("HAVE", temp.getIndex());
                    for (Peer peer:peerList) {
                        peer.removeInterest(temp.getIndex());
                        peer.setInterested_Peer(peer.isInterested());
                        reply_have.sendMsg(peer.getSocket().getOutputStream());
                    }
                    if (guestPeer.isInterested_Peer() && guestPeer.isChoked()) {
                    	MsgManager reply_request = new MsgManager("REQUEST", guestPeer.randomIndex(random));
                        reply_request.sendMsg(guestPeer.getSocket().getOutputStream());
                    }
                }

                if (peerProcess.config.getHaveFile()) {
                    boolean t = false;
                    for (Peer peer : peerList) {
                        if (!peer.getHaveFile()) {
                            t = true;
                        }
                    }

                    if (!t){
                        peerProcess.config.setCompleted(true);
                        guestPeer.getSocket().close();
                        return;
                    }
                }
                if (guestPeer.getSocket().isClosed())
                    break;

            }

        } catch (IOException e) {
          //  e.printStackTrace();
            try {
                guestPeer.getSocket().close();
            } catch (IOException e1) {
             //   e1.printStackTrace();
                return;
            }
            return;
        }

    }

}
