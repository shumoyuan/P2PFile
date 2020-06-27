package NetWork;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.InputStream;
import java.util.Arrays;

class MethodBase {

    public static int bytesToInt(byte[] src) {
        int value= ByteBuffer.wrap(src).getInt();
        return value;
    }

    public static byte[] intToBytes(int value)
    {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24));
        src[1] = (byte) ((value>>16));
        src[2] = (byte) ((value>>8));
        src[3] = (byte) (value);
        return src;
    }

    public static byte[] concatBytes(byte[] a, byte[] b) {
        byte[] res = new byte[a.length + b.length];
        System.arraycopy(a, 0, res, 0, a.length);  
        System.arraycopy(b, 0, res, a.length, b.length); 
        return res;
    }

    public static boolean arrayCompare(byte[] a, byte[] b) {
    	return Arrays.equals(a, b);
    }
    
     public static int getCRC(byte[] datas) {
    	 int result = 0;
    	 for(int i = 0; i<datas.length;i++) {
    		 result = result ^ datas[i]; 
    	 }
    	 return result;
     }
}
