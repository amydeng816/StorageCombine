package main.java.com.storagecombine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import java.util.ArrayList;

public class Merge {
	public static void merge(FileTableEntry fte) throws IOException {
		File ofile = new File(Config.MERGE_DIR + "/" + fte.getFileName());

		FileOutputStream fos;
		FileInputStream fis;

		byte[] fileBytes;
		int bytesRead = 0;

		StorageFactory factory = new StorageFactory();
		CredentialsConf credConf = new CredentialsConf();
                ArrayList<Credentials> creds = credConf.load();
		ArrayList<StorageService> ssrvs = factory.getStorageServices(creds);

		try {
			fos = new FileOutputStream(ofile, true);

			System.out.print("Downloading");
			for (Chunk chunk : fte.getChunks()) {

				System.out.print(".");
				// StorageService objects knows where a chunk is stored.
				// Have the StorageFactory return the type of StorageService
				// (Local, DropBox, etc..) associated with the chunk we are
				// retrieving
				StorageService ssrv = ssrvs.get(chunk.getStorageId());

				// Requste chunk from StorageService object
				File file = ssrv.get(chunk.getChunkName());

				// decrypt file
				File decFile = FileEncryptor.decrypt(file.getPath(), credConf.loadById(chunk.getStorageId()).getKey());

				fis = new FileInputStream(decFile);
				fileBytes = new byte[(int)decFile.length()];
				bytesRead = fis.read(fileBytes, 0, (int)decFile.length());

				fos.write(fileBytes);
				fos.flush();

				if (chunk.getStorageId() != Config.SRV_LOCAL) file.delete();
				decFile.delete();

				fileBytes = null;
				fis.close();
				fis = null;
			}
			System.out.println("done!");

			fos.close();
			fos = null;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
