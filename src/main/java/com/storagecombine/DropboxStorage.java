package main.java.com;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.URL;
import java.util.Locale;

import com.dropbox.core.*;

public class DropboxStorage implements StorageService {
	private String DBX_STORAGE_DIR = "";
	private static final String DBX_KEY = "ye2kftwgryzv60i";
	private static final String DBX_SECRET = "2jl7uvrkgnnrwsd";

	private Credentials cred = null;
	private DbxClient client = null;
	//private DbxAppInfo appInfo = null;

	public DropboxStorage(Credentials c) {
		this.cred = c;
		//DbxAppInfo appInfo = new DbxAppInfo(DBX_KEY, DBX_SECRET);
		DbxRequestConfig config = new DbxRequestConfig(Config.CLOUD_VAULT_IDENTIFIER, Locale.getDefault().toString());
		this.client = new DbxClient(config, cred.getToken());
	}

	public File get(String filename) {
		File newFile = new File(Config.MERGE_DIR + "/" + filename);
		FileOutputStream outputStream = null;

		try {
			outputStream = new FileOutputStream(newFile);
			DbxEntry.File downloadedFile = client.getFile("/" + filename, null, outputStream);
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return newFile;

	}

	public boolean put(File file) {
		try {
			FileInputStream inputStream = new FileInputStream(file);
			DbxEntry.File uploadedFile = client.uploadFile("/" + file.getName(), DbxWriteMode.add(), file.length(), inputStream);
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean del(String filename) {
		try {
			client.delete("/" + filename);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public long space() {
		try {
			DbxAccountInfo.Quota quota = client.getAccountInfo().quota;
			return (quota.total - quota.normal - quota.shared);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
