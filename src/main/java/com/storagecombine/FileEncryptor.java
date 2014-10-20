package main.java.com.storagecombine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.*;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public class FileEncryptor {
	
	private static final String algorithm = "AES";

	public FileEncryptor() {}
	
	public static File encrypt(String path, String key) throws Exception {
		File file = new File(path);

		FileInputStream fis = new FileInputStream(file);
		file = new File(file.getAbsolutePath() + ".enc");
		FileOutputStream fos = new FileOutputStream(file);

		Cipher encrypt =  Cipher.getInstance(algorithm);
		encrypt.init(Cipher.ENCRYPT_MODE, stringToKey(key));  
		CipherOutputStream cout = new CipherOutputStream(fos, encrypt);

		byte[] buf = new byte[1024];
		int read;
		while ((read = fis.read(buf)) != -1) cout.write(buf, 0, read);

		fis.close();
		cout.flush();
		cout.close();

		return file;
	}
	
	public static File decrypt(String path, String key) throws Exception {
		File file = new File(path);

		FileInputStream fis = new FileInputStream(file);
		file = new File(file.getAbsolutePath() + ".dec");
		FileOutputStream fos = new FileOutputStream(file);

		Cipher decrypt = Cipher.getInstance(algorithm);  
		decrypt.init(Cipher.DECRYPT_MODE, stringToKey(key));  
		CipherInputStream cin = new CipherInputStream(fis, decrypt);

		byte[] buf = new byte[1024];
		int read = 0;
		while ((read = cin.read(buf)) != -1) fos.write(buf, 0, read);

		cin.close();
		fos.flush();
		fos.close();

		return file;
	}
	
	public static SecretKey generateKey() {
		SecretKey secretKey = null;

		try {
			secretKey = KeyGenerator.getInstance(algorithm).generateKey();
		} catch (NoSuchAlgorithmException e) { e.printStackTrace(); }

		return secretKey;
	}
	
	public static String keyToString(SecretKey key) {
		Base64 b = new Base64();
		return b.encodeToString(key.getEncoded());
	}

	public static SecretKey stringToKey(String key) {
		Base64 b = new Base64();
		byte[] encodedKey = b.decode(key);
		SecretKey secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, algorithm);
		return secretKey;
	}
}
