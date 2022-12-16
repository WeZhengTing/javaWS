package com.zc.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
        "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
        "y", "z"};
	
 
	
	private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 36;
        int iD2 = iRet % 36;
        return strDigits[iD1] + strDigits[iD2];
    }
	
	@SuppressWarnings("unused")
	private static String byteToNum(byte bByte) {
        int iRet = bByte;
        System.out.println("iRet1=" + iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        return String.valueOf(iRet);
    }
	
	private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }
	
	public static String GetMD5Code(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }
    
	 private static String byteArrayToHexString(byte b[]) {
	        StringBuffer resultSb = new StringBuffer();
	        for (int i = 0; i < b.length; i++)
	            resultSb.append(byteToHexString(b[i]));

	        return resultSb.toString();
	    }

	    private static String byteToHexString(byte b) {
	        int n = b;
	        if (n < 0)
	            n += 256;
	        int d1 = n / 36;
	        int d2 = n % 36;
	        return hexDigits[d1] + hexDigits[d2];
	    }

	    public static String MD5Encode(String origin, String charsetname) {
	        String resultString = null;
	        try {
	            resultString = new String(origin);
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            if (charsetname == null || "".equals(charsetname))
	                resultString = byteArrayToHexString(md.digest(resultString
	                        .getBytes()));
	            else
	                resultString = byteArrayToHexString(md.digest(resultString
	                        .getBytes(charsetname)));
	        } catch (Exception exception) {
	        }
	        return resultString;
	    }
	    
	    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
	        "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
	        "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
	        "y", "z"};
	    
	    public static void main(String[] args){
	    	String s=MD5Util.GetMD5Code("123456");
	    	System.out.println(s);
//	    	System.out.println();
	    }
	
}
