package main.java.com.storagecombine;
import java.util.ArrayList;
import java.util.Scanner;

public class StorageCombine {
	public StorageCombine() {}
	
	public static void get(String filename) {
		try {
			FileTable ft = new FileTable();
			FileTableEntry fte = ft.stat(filename);
		} catch (Exception e) {
			e.printStacke();
		} 
	}
	
	public static void put(String filename) {
		try {
			FileTable ft = new FileTable();
			FileTableEntry fte = Split.split(filename);
			ft.save(fte);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void del(String filename) {
		try {
			FileTable ft = new FileTable();
			FileTableEntry fte = ft.stat(filename);
			StorageFactory factory = new StorageFactory();
			StorageService ssrv = null;
			CredentialsConf credConf = new CredentialsConf();
			ArrayList<Credentials> creds = credConf.load();
			ArrayList<StorageService> ssrvs = factory.getStorageServices(creds);
			
			System.out.print("Deleting");
			boolean ok = true;
			for (Chunk c : fte.getChunks()) {
				System.out.print(".");
				//ssrv = factory.create(creds.get(c.getStorageId()));
				ssrv = ssrvs.get(c.getStorageId());
				if (!ssrv.del(c.getChunkName())) {
					ok = false;
				}
			}
			
			if (ok) {
				System.out.println("done!");
				ft.del(filename);
			} else {
				System.out.println("Something wrong with delete");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stat(String filename) {
	}
	
	public static void configure(String service) {
		CredentialsConf credConf = new CredentialsConf();
		
		if (service.equals("local")) {
			Scanner scanner = new Scanner(System.in)
			System.out.print("Enter path to local storage: ");
			String path = scanner.nextLine();
			credConf.save(new Credentials(Config.SRV_LOCAL, "local", path, FileEncryptor.keyToString(FileEncryptor.generateKey())));
		} else if (service.equals("dropbox")) {
			Credentials cred = credConf.loadByStorageId(Config.SRV_DROPBOX);
			cred.setKey(FileEncryptor.keyToString(FileEncryptor.generateKey()));
			credConf.save(cred);
		} else if (service.equals("gdrive")) {
			Credentials cred = credConf.loadByStorageId(Config.SRV_GDRIVE);
			cred.setKey(FileEncryptor.keyToString(FileEncryptor.generateKey()));
			credConf.save(cred);
		} else {
			usage();
		}
	}
	
	public static void list() {
		FileTable ft = new FileTable();
		for (FileTableEntry fte : ft.list()) {
			System.out.println(String.format("%12d %s %s %s", fte.getFileSize(), Epoch.toString(fte.getLastModified()), fte.getHash(), fte.getFileName()));
		}
	}
	
	public static void usage() {
		System.out.println("usage: java StorageCombine command [file|service]\n");
		System.out.println("commands:");
		System.out.println("get file    - get file");
		System.out.println("put file    - put file");
		System.out.println("del file    - delete file");
		System.out.println("list        - list files stored");
		System.out.println("stat file   - show information for file\n");
		System.out.println("configure service   - configure service");
		System.out.println("services: local, dropbox, gdrive");
	}
	
	public static void main(String[] args) {
		//long epoch = Epoch.getNow();
		//System.out.println(epoch);
		//System.out.println(Epoch.toString(epoch));

		if ((args.length > 1) && (args[0].equals("get"))) {
			get(args[1]);
		} else if ((args.length > 1) && (args[0].equals("put"))) {
			put(args[1]);
		} else if ((args.length > 1) && (args[0].equals("del"))) {
			del(args[1]);
		} else if ((args.length > 0) && (args[0].equals("list"))) {
			list();
		} else if ((args.length > 1) && (args[0].equals("stat"))) {
			stat(args[1]);
		} else if ((args.length > 1) && (args[0].equals("configure"))) {
			configure(args[1]);
		} else {
			usage();
		}
	}
}
