package NetWork;

import java.io.IOException;
import java.util.*;

public class PreferChooser implements Runnable {
    private int interval;
    private ArrayList<Peer> preferedPeers;
    private int number;
    private Random random = new Random(System.currentTimeMillis());
    private Logger logger;


    public PreferChooser() throws IOException {
        this.interval = peerProcess.config.getUnchokInterval();
        this.number = peerProcess.config.getPreferedNo();
        this.preferedPeers = new ArrayList<>();
        this.logger = new Logger(peerProcess.config);
    }

    @Override
    public void run() {
        try {
            if(!firstChoose())
                return;
            logger.LogWriter(preferedPeers);
            while (true) {
                if (peerProcess.config.getMyFile()) {
                    if (peerProcess.config.getPeers().size() < number) {
                        for (Peer peer:peerProcess.config.getPeers()) {
                        	MsgHandler.PreferedHandler(peer,1);
                            preferedPeers.add(peer);
                            logger.LogWriter(preferedPeers);
                        }
                        break;
                    } else {
                        for (int i = 0; i < number; i++) {
                            int index = peerProcess.config.getPeers().indexOf(peerProcess.config.getPeers().get(random.nextInt(peerProcess.config.getPeers().size())));
                            Peer peer = peerProcess.config.getPeers().get(index);
                            if (!preferedPeers.contains(peer)) {
                                preferedPeers.add(peer);
                                MsgHandler.PreferedHandler(peer,1);
                                logger.LogWriter(preferedPeers);
                            } else {
                                i--;
                            }
                        }
                    }
                } else {
                    for (Peer pfPeer : preferedPeers) {
                        Peer peer = peerProcess.config.getPeers().get(peerProcess.config.getPeers().indexOf(pfPeer));
                        peer.setTransRate(peer.getTransNumber() / interval);
                        peer.setTransNumber(0);
                    }

                    Comparator<Peer> com=new Comparator<Peer>() { 
                    	@Override
                    	public int compare(Peer o1,Peer o2) {
                    		return o1.getTransRate()-o2.getTransRate();
                    	}       	
                    };
                    	
                    PriorityQueue<Peer> pq = new PriorityQueue<>(number + 1,com);
                    for (Peer x : peerProcess.config.getPeers()) {
                        if (pq.size() == number + 1) {
                            pq.poll();
                        }
                        pq.offer(x);
                    }
                    pq.poll();
                    
                    ArrayList<Peer> tempPfs = new ArrayList<>(pq);
                    for (Peer peer: tempPfs) {
                        if (preferedPeers.contains(peer)) {

                        } else if (peer.getOptimisticNb()) {
                            peerProcess.config.getPeers().get(peerProcess.config.getPeers().indexOf(peer)).setPreferedNeighbor(true);
                        } else {
                        	MsgHandler.PreferedHandler(peerProcess.config.getPeers().get(peerProcess.config.getPeers().indexOf(peer)),1);
                        }
                    }

                    for (Peer peer:preferedPeers) {
                        if (!tempPfs.contains(peer) && !peer.getOptimisticNb()){
                        	MsgHandler.PreferedHandler(peerProcess.config.getPeers().get(peerProcess.config.getPeers().indexOf(peer)),0);
                        }
                    }

                    preferedPeers.clear();
                    preferedPeers = tempPfs;
                }
                Thread.sleep(interval * 1000);
                if (peerProcess.config.getHaveFile()) {
                    boolean t = false;
                    for (Peer peer : peerProcess.config.getPeers()) {
                        if (!peer.getHaveFile()) {
                            t = true;
                        }
                    }

                    if (!t) {
                        peerProcess.config.setCompleted(true);
                        return;
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
          //  e.printStackTrace();
            return;
        }

    }

    public boolean firstChoose() throws IOException {
        if (peerProcess.config.getPeers().size() < number) {
            for (Peer peer:peerProcess.config.getPeers()) {
            	MsgHandler.PreferedHandler(peer,1);
                preferedPeers.add(peer);
                logger.LogWriter(preferedPeers);
            }
            return false;
        } else {
            for (int i = 0; i < number; i++) {
                int index = peerProcess.config.getPeers().indexOf(peerProcess.config.getPeers().get(random.nextInt(peerProcess.config.getPeers().size())));
                Peer peer = peerProcess.config.getPeers().get(index);
                if (!preferedPeers.contains(peer)) {
                    preferedPeers.add(peer);
                    MsgHandler.PreferedHandler(peer,1);
                    logger.LogWriter(preferedPeers);
                } else {
                    i--;
                }
            }
            return true;
        }
    }
}
