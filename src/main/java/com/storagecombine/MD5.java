package main.java.com.storagecombine;

import java.security.*;
import java.io.*;

public class MD5 {

	private static String getMD5(byte[] source) {
		String s = null;
		char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',  'e', 'f'};
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance( "MD5" );
			md.update(source);
			byte tmp[] = md.digest();        

			char str[] = new char[16 * 2];  

			int k = 0;                               
			for (int i = 0; i < 16; i++) {                                              
				byte byte0 = tmp[i];             
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];                                   
				str[k++] = hexDigits[byte0 & 0xf];           
			}
			s = new String(str);                               

		}catch(Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	private static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			throw new IOException("File is too large "+file.getName());
		}
		byte[] bytes = new byte[(int)length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "+file.getName());
		}

		is.close();
		return bytes;
	}
	

	public static String stringToMD5(String string) {
		return MD5.getMD5(string.getBytes());
	}

	public static String fileNameToMD5(File file) {
		byte[] source = null;
		try {
			source = getBytesFromFile(file);
		} catch(IOException e){
			System.out.println("IOException");
		}

		return MD5.getMD5(source);
	}
}
