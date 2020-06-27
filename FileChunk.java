package NetWork;

public class FileChunk {
    private final int chunkIndex;
    private final byte[] chunkArray;
    private final int chunkCRC;

    public FileChunk(MsgManager MsgMng) {
        byte[] indexList = new byte[4];
        byte[] CRCList = new byte[4];
        for (int i = 0; i < 4; i++) {
        	indexList[i] = MsgMng.bPaylod[i];
        }
        chunkIndex = MethodBase.bytesToInt(indexList);
        chunkArray = new byte[MsgMng.bPaylod.length - 4];
        for (int i = 4; i < MsgMng.bPaylod.length; i++) {
            chunkArray[i - 4] = MsgMng.bPaylod[i];
        }
        for (int i = MsgMng.bPaylod.length-4; i < MsgMng.bPaylod.length; i++) {
        	CRCList[i-MsgMng.bPaylod.length+4] = MsgMng.bPaylod[i];
        }
        chunkCRC = MethodBase.bytesToInt(CRCList);
    }

    public FileChunk(int chunkIndex, byte[] chunkArray,int chunkCRC) {
        this.chunkIndex = chunkIndex;
        this.chunkArray = chunkArray;
        this.chunkCRC = chunkCRC;
    }

    public int getChunkIndex() {

        return chunkIndex;
    }

    public byte[] getChunkArray() {

        return chunkArray;
    }
    
    public int getChunkCRC() {

        return chunkCRC;
    }

    public boolean ChunkCheck() {
    	if(MethodBase.getCRC(chunkArray)==chunkCRC) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
}
