package main.java.com.storagecombine;

import java.util.ArrayList;

public class FileTableEntry {
	private String filename;
	private long filesize;
	private long lastModified;
	private String hash;
	private ArrayList<Chunk> chunks = null;
	
	public FileTableEntry(String filename, long filesize, long lastModified, String hash) {
		this.filename = filename;
		this.filesize = filesize;
		this.lastModified = lastModified;
		this.hash = hash;
		chunks = new ArrayList<Chunk>();
	}
	
	public String getFileName() { return this.filename; }
	public long getFileSize() { return this.filesize; }
	public long getLastModified() { return this.lastModified; }
	public String getHash() { return this.hash; }
	public int getNumChunks() { return this.chunks.size(); }
	public ArrayList<Chunk> getChunks() { return this.chunks; }

	public void addChunk(Chunk chunk) { this.chunks.add(chunk); }

	public String toString() {
		return String.format("%s:%d:%d:%s", filename, filesize, lastModified, hash);
	}
}
