package com.firebig.util;

import java.security.MessageDigest;

/**
 * Created by root on 16-8-18.
 */
public class Md5Util {

    public static String bytesToHex(byte[] paramArrayOfByte) {
        StringBuffer localStringBuffer = new StringBuffer();
        for (int i = 0; ; i++) {
            if (i >= paramArrayOfByte.length)
                return localStringBuffer.toString().toUpperCase();
            int j = paramArrayOfByte[i];
            if (j < 0)
                j += 256;
            if (j < 16)
                localStringBuffer.append("0");
            localStringBuffer.append(Integer.toHexString(j));
        }
    }

    public static String getMD5(String paramString) {
        try {
            String str = bytesToHex(MessageDigest.getInstance("MD5").digest(paramString.getBytes()));
            return str;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return "";
    }

    public static String encryptPass(String pass, int frequency) {
        for (int i = 0; i < frequency; i++) {
            pass = getMD5(pass);
        }
        return pass;
    }
}
