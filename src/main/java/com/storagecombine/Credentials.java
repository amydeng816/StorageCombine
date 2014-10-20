package main.java.com.storagecombine;

public class Credentials {
	private int id = -1;
	private int sid;
	private String name;
	private String token;
	private String key;
	
	public Credentials(int sid, String name, String token, String key) {
		this.sid = sid;
		this.name = name;
		this.token = token;
		this.key = key;
	}
	
	public Credentials(int id, int sid, String name, String token, String key) {
		this.id = id;
		this.sid = sid;
		this.name = name;
		this.token = token;
		this.key = key;
	}
	
	public int getId() { return this.id - 1; } // sqlite autocrement starts at 1, we want 0
	public int getStorageId() { return this.sid; }
	public String getName() { return this.name; }
	public String getToken() { return this.token; }
	public String getKey() { return this.key; }

	public void setStorageId() { this.sid = sid; }
	public void setToken(String token) { this.token = token; }
	public void setKey(String key) { this.key = key; }

	public String toString() {
		return String.format("%d:%d:%s:%s:%s", id, sid, name, token, key);
	}
}
