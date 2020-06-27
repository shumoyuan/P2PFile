package NetWork;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

public class Logger {
    private Config config;
    private File file;
    private int peerID;
    private BufferedWriter out;
    private String hostID;
    private boolean flag=false;//system out
    private FileWriter temp;
    
    Logger(Config config) throws FileNotFoundException {
        this.config=config;
        peerID=config.getMyPid();
        hostID= config.getMyPid()+"";
        file = new File("peer_" + config.getMyPid() + "/" + "log_peer_" + peerID + ".log");
        try {
            file.createNewFile();
             temp=new FileWriter(file);

            out = new BufferedWriter(temp);


        } catch (IOException e) {
            //e.printStackTrace();
        }

    }
    
	private String getTime() {
		return new SimpleDateFormat("yy.MM.dd HH:mm:dd").format(new Date());
	}

    public synchronized void print(String log) throws IOException {
        log += "\n";
        if (flag) {
            System.out.println(log);

        } else {
            out.write(log);
            out.flush();
        }
        
    }
    
	public void LogWriter(int id, int MsgType) {
		try {
			String log;
			switch(MsgType) {
				case 1:
					log = getTime() + ": Peer " + hostID + " makes a connection to Peer " + id + ".";
					print(log);
					break;
				case 2:
					log = getTime() + ": Peer " + hostID + " is connected from Peer " + id + ".";
					print(log);
					break;
				case 3:
					log = getTime() + ": Peer " + hostID + " has the optimistically unchoked neighbor " + id + ".";
					print(log);
					break;
				case 4:
					log = getTime() + ": Peer " + hostID + " is unchoked by " + id + "." ;
					print(log);
					break;
				case 5:
					log = getTime() + ": Peer " + hostID + " is choked by " + id + "." ;
					print(log);
					break;
				case 6:
					log = getTime() + ": Peer " + hostID + " received the 'interested' message from " + id + "." ;
					print(log);
					break;
				case 7:
					log = getTime() + ": Peer " + hostID + " received the 'not interested' message from " + id + "." ;
					print(log);
					break;
				case 8:
					log = getTime() + ": Peer " + id + " has downloaded the complete file.";
					print(log);
					break;
				default:
					print("Wrong Message Type.");
					break;
			}
		}
			catch(Exception e) {
				//System.out.println("File writing is occupied");
			}	
	}
	
	public void LogWriter(ArrayList<Peer> list) {
		String ids = "";
        for (Peer x : list) {
            ids += x.getPID() + ", ";
        }
		try {
            ids = ids.substring(0, ids.length() - 2);
            String log = getTime() + ": Peer " + hostID + " has the preferred neighbors "
                    + ids + ".";
            print(log);
		} catch(Exception e) {
			//System.out.println("File writing is occupied");
		}
	}
	
	public void LogWriter(int id,int pieceNum, int index, int MsgType) {
		try {
	        String log = getTime() + ": Peer " + hostID + " has downloaded the piece "+ index + " from " + id + "." + " Now the number of pieces it has is " + pieceNum +".";
	        print(log);
		} catch(Exception e) {
			//System.out.println("File writing is occupied");
		}
	}
	
	public void LogWriter(int id,int pieceNum, int MsgType) {
		try {
	        String log = getTime() + ": Peer " + hostID + " received the 'have' message from " + id + " for the piece " + pieceNum + ".";
	        print(log);
		} catch(Exception e) {
			//System.out.println("File writing is occupied");
		}
	}

    public void loggerOf() throws Exception{
        out.flush();
        out.close();
        temp.close();

    }
}
