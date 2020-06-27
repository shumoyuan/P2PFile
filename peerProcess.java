package NetWork;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;

import static NetWork.MsgManager.*;
import static NetWork.MethodBase.*;
import static NetWork.FileManager.*;

public class peerProcess {

    static Config config;
    static FileManager fileManager;
    private HandShake handshake;
    static Logger logger;


    peerProcess(int pid) throws IOException {
        config = new Config(pid);
        fileManager = new FileManager(config);
        handshake = new HandShake(pid);
        logger = new Logger(config);
    }

    public static void main(String[] args) throws Exception {
        peerProcess p = new peerProcess(Integer.parseInt(args[0]));
        p.run();
    }


    void run() {
        try {
        	ServerSocket serverSocket = new ServerSocket(config.getMyPort());
        	for (int i = 0; i < config.getPeers().size(); i++) {
        		if (i < config.getMyIndex())
        			config.getPeers().get(i).setSocket();
        		else 
        			config.getPeers().get(i).setSocket(serverSocket.accept());
                if (config.getPeers().get(i).getSocket().isConnected()) {
                    logger.LogWriter(config.getPeers().get(i).getPID(),2);
                }
                config.getPeers().get(i).getSocket().setKeepAlive(true);
                handshake.sendHandShack(config.getPeers().get(i).getSocket().getOutputStream());
                int id1 = handshake.getID(config.getPeers().get(i).getSocket());
                int id2 = config.getPeers().get(i).getPID();
        	}
        	
        	

            for (Peer peer : config.getPeers()
                    ) {
            	MsgManager bitfieldMsg = new MsgManager(config);

                if (config.getMyFile()) {
                	MsgHandler.FileHandler(bitfieldMsg,peer,1);
                }

                if (peer.getHaveFile()) {
                    readMsg(peer.getSocket());
                    MsgManager interestMsg = new MsgManager("INTERESTED");
                    MsgHandler.FileHandler(interestMsg,peer,0);

                }
            }

            ExecutorService peerThreadPool = Executors.newFixedThreadPool(config.getPeers().size());
            for (Peer peer : config.getPeers()) {
                peerThreadPool.submit(new PeerThread(peer));
            }


            ExecutorService specialNeighbourSelector = Executors.newFixedThreadPool(2);
            specialNeighbourSelector.submit(new OptChooser());
            specialNeighbourSelector.submit(new PreferChooser());


            peerThreadPool.shutdown();
            specialNeighbourSelector.shutdown();
            
            while(true) {
            	if (peerThreadPool.isTerminated() && specialNeighbourSelector.isTerminated())
            		break;
            }

            specialNeighbourSelector.shutdownNow();
            peerThreadPool.shutdownNow();


            fileManager.closeManageFile();
            for (Peer peer : config.getPeers()) {
                peer.getSocket().close();
            }
            logger.loggerOf();
            serverSocket.close();

        } catch (Exception e) {
        	e.printStackTrace();
            
        }

    }

}

