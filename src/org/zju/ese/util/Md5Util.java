package org.zju.ese.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * get md5 hash string
 * @author wenming.hong
 * @since 2012-6-23
 */
public class Md5Util {


    private static char hexDigits[] = { '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F' };
    
    public static String getMd5(byte[] bytes) {
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(bytes);
            byte[] digests = mdInst.digest();
            
            int j = digests.length;
            char str[] = new char[j * 2];
            
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = digests[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
           
        }
        
        return "";
    }
    
    public static String getMd5(String str) {
        return getMd5(str.getBytes());
    }
    
    public static long convertStringToLong(String str) {
        long l = 0;
        for(int i=0; i<16; i++) {
            int c = Character.digit(str.charAt(i), 16);
            l = (l<<4 | (long)(c & 0xf));
        }
        
        return l;
    }
    
    public static String convertLongToString(long l) {
        char str[] = new char[16];
        for(int i=0; i<16; i++) {
            int idx = (int) (l & 0xf);
            l = l >>> 4;
            str[15-i] = hexDigits[idx];
        }
        return new String(str);
    }
}
