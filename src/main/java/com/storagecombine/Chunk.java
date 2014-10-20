package main.java.com.storagecombine;

public class Chunk {
	private int storageId;
	private String fileName;
	private String chunkName;
	private int number;
	
	public Chunk(int storageId, String fileName, String chunkName, int number) {
		this.storageId = storageId;
		this.fileName = fileName;
		this.chunkName = chunkName;
		this.number = number;
	}
	
	public int getStorageId() { return this.storageId; }

	public String getFileName() { return this.fileName; }
	public String getChunkName() { return this.chunkName; }
	public int getNumber() { return this.number; }

	public String toString() {
		return String.format("%d:%s:%s:%d", storageId, chunkName, fileName, number);
	}
}
