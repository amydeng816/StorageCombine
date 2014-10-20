package main.java.com.storagecombine;

import java.io.File;

public interface StorageService {
	File get(String filename);
	boolean put(File file);
	boolean del(String filename);
	long space();
}
