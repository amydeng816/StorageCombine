package main.java.com.storagecombine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class LocalStorage implements StorageService {
	private String LOCAL_STORAGE_DIR = Config.STORAGE_COMBINE_DIR + "/LocalStorage";
	private Credentials cred = null;

	public LocalStorage(Credentials c) {
		this.cred = c;
		this.LOCAL_STORAGE_DIR = c.getToken();
	}
	
	public File get(String filename) {
		return new File(LOCAL_STORAGE_DIR + "/" + filename);
	}

	public boolean put(File file) {
		try {
			File newFile = new File(LOCAL_STORAGE_DIR + "/" + file.getName());
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));	
			FileOutputStream out = new FileOutputStream(newFile);

			byte[] buffer = new byte[(int)file.length()];

			int tmp = 0;
			while ((tmp = in.read(buffer)) > 0) {
				out.write(buffer, 0, tmp);
			}

			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public boolean del(String filename) {
		try {
			File file = new File(LOCAL_STORAGE_DIR + "/" + filename);
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public long space() {
		File file = new File(LOCAL_STORAGE_DIR);
		return file.getFreeSpace();
	}
}
