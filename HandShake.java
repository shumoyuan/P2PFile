package NetWork;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.in;

public class HandShake {
	private String HandshakeHeader = "P2PFILESHARINGPROJ";
	private String ZeroBits = "0000000000";
	private int ID;

    HandShake(int ID) {
        this.ID = ID;
    }
    
    void sendHandShack(OutputStream out) throws IOException{
    	byte[] bID = new byte[4];
        bID = MethodBase.intToBytes(ID);
        byte[] msg = MethodBase.concatBytes(HandshakeHeader.getBytes(), ZeroBits.getBytes());
        msg = MethodBase.concatBytes(msg, bID);
        out.write(msg);
        out.flush();
    } 
    
    public int getID(Socket s) throws IOException {
        InputStream in = s.getInputStream();

        byte[] handshake = new byte[18];
        byte[] zerobits = new byte[10];
        byte[] ID = new byte[4];

        int bytes;
        int bytebuffer = 0;

        while (bytebuffer< 18) {
            bytes = in.read(handshake, bytebuffer, 18- bytebuffer);
            bytebuffer += bytes;
        }

        bytebuffer = 0;
        while (bytebuffer < 10){
            bytes = in.read(zerobits, bytebuffer, 10 - bytebuffer);
            bytebuffer += bytes;
        }

        bytebuffer = 0;
        while (bytebuffer < 4) {
            bytes = in.read(ID, bytebuffer, 4 - bytebuffer);
            bytebuffer += bytes;
        }

        if (!MethodBase.arrayCompare(HandshakeHeader.getBytes(), handshake) || !MethodBase.arrayCompare(ZeroBits.getBytes(),zerobits)) {
            throw new IOException("not a handshake message");
        }

        return MethodBase.bytesToInt(ID);

    }

}
