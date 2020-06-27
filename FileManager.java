package NetWork;

import java.io.*;
import java.nio.file.Files;


public class FileManager {
    public static Config config;
    private static RandomAccessFile filemanager;

    public FileManager(Config config) throws IOException {
    	FileManager.config = config;

        String filetree = "peer_" + config.getMyPid() + "/";

        File dir = new File(filetree);

        if (!dir.exists()) {
            dir.mkdirs();
        }


        if (config.getMyFile()) {
            File temp = new File(config.getFileName());
            File t2 = new File(filetree+ config.getFileName());
            copyfile(temp, t2);
            filemanager = new RandomAccessFile(t2, "rw");
        } else {
        	filemanager = new RandomAccessFile(filetree + config.getFileName(), "rw");
            try {
            	filemanager.setLength(config.getFileSize());
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }

    private static void copyfile(File source, File dest)
            throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }


    public synchronized FileChunk readMsg(int index) throws IOException {
        int length = 0;
        int CRC;
        if(index == config.getPieceNum()-1){
            length = config.getFileLeftChunk();
        } else {
            length = config.getPieceSize();
        }
        int offset = index * config.getPieceSize();
        byte[] data = new byte[length];

        try {
        	filemanager.seek(offset);
        } catch (IOException e) {
           // e.printStackTrace();
        }

        for (int i = 0; i < length; i++) {
            data[i] = filemanager.readByte();
        }
        
        CRC = MethodBase.getCRC(data);

        FileChunk filePiece = new FileChunk(index, data,CRC);
        return filePiece;
    }



    public synchronized void writeMsg(FileChunk filePiece) throws IOException {
        int offset = filePiece.getChunkIndex()*config.getPieceSize();
        byte[] data = filePiece.getChunkArray();
        MethodBase.getCRC(data);
        filemanager.seek(offset);
        filemanager.write(data);
    }

    public void closeManageFile () throws IOException {
    	filemanager.close();
    }
}