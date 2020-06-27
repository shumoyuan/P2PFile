package NetWork;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OptChooser implements Runnable {
    private final int optInterval;
    ArrayList<Peer> allPeer;
    ArrayList<Peer> optList;
    Peer optLast;
    Peer optNow;
    Random r;
    Logger logger;

    OptChooser() throws FileNotFoundException{
        optInterval = peerProcess.config.getOptUnchokInterval();
        allPeer = peerProcess.config.getPeers();
        optList = new ArrayList<>();
        optLast = null;
        optNow = null;
        r = new Random(System.currentTimeMillis());
        logger = peerProcess.logger;
    }

    @Override
    public void run() {
        try {
            while (true) {
                for (Peer x : allPeer) {
                    if (x.isChoked() && x.getInterestMon()) {
                    	optList.add(x);
                    }
                }
                if (optList.size() == 0){
                    break;
                }
                optNow = optList.get(r.nextInt(optList.size()));
                MsgHandler.OptimisticHandler(optNow,1);

                if (optLast != null) {
                    if (!optLast.getpreferedNb()) {
                    	MsgHandler.OptimisticHandler(optLast,0);
                        logger.LogWriter(optNow.getPID(),3);
                    }
                    optLast.setTransRate(optLast.getTransNumber() / optInterval);
                    optLast.setTransNumber(0);
                }

                optLast = optNow;
                optList.clear();
                Thread.sleep(optInterval * 1000);

                if (peerProcess.config.getHaveFile()) {
                    boolean t = false;
                    for (Peer peer : allPeer) {
                        if (!peer.getHaveFile()) {
                            t = true;
                        }
                    }

                    if (!t){
                        peerProcess.config.setCompleted(true);
                        return;
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            //e.printStackTrace();
            return;
        }
    }
}