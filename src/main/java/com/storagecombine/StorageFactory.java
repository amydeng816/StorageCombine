package main.java.com.storagecombine;

import java.util.ArrayList;

public class StorageFactory {
	public  StorageFactory() {}
	
	public ArrayList<StorageService> getStorageServices(ArrayList<Credentials> credentials) {
        ArrayList<StorageService> ssrvs = new ArrayList<StorageService>();
        for (Credentials c : credentials) {
        	ssrvs.add(c.getId(), create(c));
        }

        return ssrvs;
	}
	
	public StorageService create(Credentials c) {
		switch (c.getStorageId()) {
			case Config.SRV_LOCAL:
				return new LocalStorage(c);
			case Config.SRV_DROPBOX:
				return new DropboxStorage(c);
			case Config.SRV_GDRIVE:
				return new GoogleDriveStorage(c);
		}

		return null;
	}	
	
}
