package main.java.com.storagecombine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Split {
	public static byte[] randomBufferSize(long length, Random rnd) {
		long buf_size = length * (rnd.nextInt(10) + 10)/100;
		byte[] buffer = new byte[(int)buf_size];
		return buffer;
	}

	public static FileTableEntry split(String filename) throws IOException {
		File file = new File(filename);
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		FileOutputStream out;
		String name = file.getName();
		//FileTableEntry fte = new FileTableEntry(file.getName(), file.length(), file.lastModified()/1000, MD5.fileNameToMD5(file));
		FileTableEntry fte = new FileTableEntry(file.getName(), file.length(), Epoch.getNow(), MD5.fileNameToMD5(file));

		StorageFactory factory = new StorageFactory();
		CredentialsConf credConf = new CredentialsConf();
		ArrayList<Credentials> creds = credConf.load();
		ArrayList<StorageService> ssrvs = factory.getStorageServices(creds);

		Random rnd = new Random();

		int partCounter = 0;
		//int sizeOfFiles = 1024 * 1024; // 1MB

		// twelve chunks
		//if (file.length() >= 12)
			//sizeOfFiles = (int)file.length()/11;
		//else
		//	sizeOfFiles = 1;

		try {
			int tmp = 0;
			int cred_index = 0;
			byte[] buffer = randomBufferSize(file.length(), rnd);
			System.out.print("Uploading");

			while ((tmp = bis.read(buffer)) > 0) {
				//Credentials c = creds.get(cred_index);
				Credentials c = creds.get(rnd.nextInt(creds.size()));
				StorageService ssrv = ssrvs.get(c.getId());

				if (ssrv.space() <= buffer.length) {
					boolean enough_space = false;
					for (int i = 0; i<creds.size(); i++) {
						c = creds.get(i);
						ssrv = ssrvs.get(c.getId());

						if (ssrv.space() > buffer.length) {
							enough_space = true;
							break;
						}
					}

					if (!enough_space) {
						System.err.println("Not enough space available on any configured StorageService!");
						System.exit(1);
					}
				}

				File tmpFile = new File(Config.SPLIT_DIR + "/" + file.getName() + String.format("%03d", partCounter++));
				tmpFile.createNewFile();

				out = new FileOutputStream(tmpFile);
				out.write(buffer, 0, tmp);
				out.close();

				System.out.print(".");

				// encrypt file
				File encFile = FileEncryptor.encrypt(tmpFile.getPath(), c.getKey());
				tmpFile.delete();

				// chunk name = MD5(filename + now() + MD5(chunk))
				// so we can chunk two or more files with different names but same content
				File hashChunk = new File(Config.SPLIT_DIR + "/" +
					MD5.stringToMD5(file.getName() +
					String.valueOf(System.currentTimeMillis()) +
					MD5.fileNameToMD5(encFile)));
				encFile.renameTo(hashChunk);

				// upload
				//StorageService ssrv = ssrvs.get(c.getId());
				ssrv.put(hashChunk);

				Chunk chunk = new Chunk(c.getId(), file.getName(), hashChunk.getName(), partCounter);
				fte.addChunk(chunk);

				//if (++cred_index >= creds.size()) cred_index = 0;

				hashChunk.delete();
				buffer = randomBufferSize(file.length(), rnd);
			}
			System.out.println("done!");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return fte;
	}
}
