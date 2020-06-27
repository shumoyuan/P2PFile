package NetWork;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MsgManager {
	
    byte[] bLength;
    byte[] bPaylod;
    byte bType;
    String type;
    byte[] bCRC;

    public String getTypeStr(byte typeNo) {
    	String tmpstr = null;
    	switch(typeNo) {
    		case 0:
    			tmpstr = "CHOKE";
    			break;
    		case 1:
    			tmpstr = "UNCHOKE";
    			break;
    		case 2:
    			tmpstr = "INTERESTED";
    			break;
    		case 3:
    			tmpstr = "NOTINTERESTED";
    			break;
    		case 4:
    			tmpstr = "HAVE";
    			break;
    		case 5:
    			tmpstr = "BITFIELD";
    			break;
    		case 6:
    			tmpstr = "REQUEST";
    			break;
    		case 7:
    			tmpstr = "PIECE";
    			break;
    	}
    	return tmpstr;
    }
    
    public byte getTypeNo(String typeStr) {
    	byte tmpno = 0;
    	switch(typeStr) {
    		case "CHOKE":
    			tmpno = 0;
    			break;
    		case "UNCHOKE":
    			tmpno = 1;
    			break;
    		case "INTERESTED":
    			tmpno = 2;
    			break;
    		case "NOTINTERESTED":
    			tmpno = 3;
    			break;
    		case "HAVE":
    			tmpno = 4;
    			break;
    		case "BITFIELD":
    			tmpno = 5;
    			break;
    		case "REQUEST":
    			tmpno = 6;
    			break;
    		case "PIECE":
    			tmpno = 7;
    			break;
    	}
    	return tmpno;
    }

    MsgManager(){
    }

    MsgManager(byte[] bLength, byte bType, byte[] bPaylod,byte[] bCRC) {
        this.bLength = bLength;
        this.bType = bType;
        this.bPaylod = bPaylod;
        this.bCRC = bCRC;
        switch (MethodBase.bytesToInt(new byte[] {0,0,0,bType}))
        {
            case 0:
                type = "CHOKE";
                break;
            case 1:
                type = "UNCHOKE";
                break;
            case 2:
                type = "INTERESTED";
                break;
            case 3:
                type = "NOTINTERESTED";
                break;
            case 4:
                type = "HAVE";
                break;
            case 5:
                type = "BITFIELD";
                break;
            case 6:
                type = "REQUEST";
                break;
            case 7:
                type = "PIECE";
                break;
        };
    }

    MsgManager(Config config) {
        this.bLength = MethodBase.intToBytes(config.getLength());
        this.type = "BITFIELD";
        this.bType = getTypeNo("BITFIELD");
        this.bPaylod = config.getData().toByteArray();
        byte[] CRCsrc = this.bLength;
        MethodBase.concatBytes(this.bLength, new byte[]{this.bType});
        if(this.bPaylod!=null) {
        	CRCsrc = MethodBase.concatBytes(CRCsrc, this.bPaylod);
        }
        this.bCRC = MethodBase.intToBytes(MethodBase.getCRC(CRCsrc));
    }

    MsgManager(String msgType) {
        this.type = msgType;
        this.bLength = new byte[]{0, 0, 0, 1};
        this.bPaylod = new byte[0];
        this.bType=getTypeNo(msgType);
        byte[] CRCsrc = MethodBase.concatBytes(this.bLength, new byte[]{this.bType});
        if(this.bPaylod!=null) {
        	CRCsrc = MethodBase.concatBytes(CRCsrc, this.bPaylod);
        }
        this.bCRC = MethodBase.intToBytes(MethodBase.getCRC(CRCsrc));
    }

    MsgManager(String msgType, int index) {
        this.bLength = new byte[]{0, 0, 0, 4};
        this.type = msgType;
        this.bType = getTypeNo(msgType);
        this.bPaylod = MethodBase.intToBytes(index);
        byte[] CRCsrc = MethodBase.concatBytes(this.bLength, new byte[]{this.bType});
        if(this.bPaylod!=null) {
        	CRCsrc = MethodBase.concatBytes(CRCsrc, this.bPaylod);
        }
        this.bCRC = MethodBase.intToBytes(MethodBase.getCRC(CRCsrc));
    }

    MsgManager(FileChunk piece) {
        this.type="PIECE";
        this.bType=getTypeNo("PIECE");
        this.bLength = MethodBase.intToBytes(piece.getChunkArray().length + 4);
        this.bPaylod = MethodBase.concatBytes(MethodBase.intToBytes(piece.getChunkIndex()), piece.getChunkArray());
        byte[] CRCsrc = MethodBase.concatBytes(this.bLength, new byte[]{this.bType});
        if(this.bPaylod!=null) {
        	CRCsrc = MethodBase.concatBytes(CRCsrc, this.bPaylod);
        }
        this.bCRC = MethodBase.intToBytes(MethodBase.getCRC(CRCsrc));

    }

    public void sendMsg(OutputStream out) throws IOException{
        byte[] CRCsrc = MethodBase.concatBytes(this.bLength, new byte[]{this.bType});
        CRCsrc = MethodBase.concatBytes(CRCsrc, this.bPaylod);
        out.write(CRCsrc);
        out.flush();
    }

    public static MsgManager readMsg(Socket s) throws IOException {
        InputStream in = s.getInputStream();
        byte[] msgLength = new byte[4];
        byte[] msgType_temp=new byte[1];
        byte[] msgCRC = new byte[4];

        int rcvBytes;
        int totalBytes = 0;

        while (totalBytes < 4) {
        	rcvBytes = in.read(msgLength, totalBytes, 4- totalBytes);
            totalBytes += rcvBytes;
        }

        int length = MethodBase.bytesToInt(msgLength);

        totalBytes = 0;
        while (totalBytes < 1){
        	rcvBytes = in.read(msgType_temp, totalBytes, 1 - totalBytes);
            totalBytes += rcvBytes;
        }

        byte msgType = msgType_temp[0];

        byte[] msgPayLoad = new byte[length];
        totalBytes = 0;
        while (totalBytes < length - 1) {
        	rcvBytes = in.read(msgPayLoad, totalBytes, length - totalBytes);
            totalBytes += rcvBytes;
        }
        
        msgCRC = MethodBase.intToBytes(MethodBase.getCRC(msgPayLoad));
        return new MsgManager(msgLength, msgType, msgPayLoad, msgCRC);
    }

    int getIndex() {
        byte index[] = new byte[4];
        for (int i = 0; i < 4; i++) {
            index[i] = bPaylod[i];
        }
        return MethodBase.bytesToInt(index);
    }
    
    int getCRC() {
        byte CRC[] = new byte[4];
        int length = bPaylod.length;
        for (int i = 0; i < 4; i++) {
            CRC[i] = bPaylod[length-4+i];
        }
        return MethodBase.bytesToInt(CRC);
    }

    String getType() {
        return type;
    }


}
