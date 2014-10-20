package main.java.com.storagecombine;

public class Config {
	public final static String STORAGE_COMBINE_IDENTIFIER = "StorageCombine/1.0";
	public final static String STORAGE_COMBINE_DIR = System.getProperty("user.home") + "/.cloudvault";
	public final static String SPLIT_DIR = STORAGE_COMBINE_DIR + "/split";
	public final static String MERGE_DIR = STORAGE_COMBINE_DIR + "/merge";
	public final static String CREDENTIALS = STORAGE_COMBINE_DIR + "/credentials";
	public final static String CREDENTIALS_DB = STORAGE_COMBINE_DIR + "/credentials.db";
	public final static String FILE_TABLE = STORAGE_COMBINE_DIR + "/filetable.db";

	public final static int SRV_LOCAL = 0;
	public final static int SRV_DROPBOX = 1;
	public final static int SRV_GDRIVE = 2;
}
