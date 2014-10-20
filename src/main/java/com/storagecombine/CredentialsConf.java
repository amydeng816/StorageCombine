package main.java.com.storagecombine;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.lang.*;
import java.io.*;
import java.util.ArrayList;

public class CredentialsConf {
	private static final String TAG = "CredentialsConf: ";
	private static final boolean logging = true;

	private	ArrayList<Credentials> credentials = null;
	
	public CredentialsConf() {
		this.credentials = new ArrayList<Credentials>();
	}
	
	public void log(String msg) {
		if (!logging) return;
		System.err.println(TAG + msg);
	}
	
	private Connection getConnection() {
		Connection c = null;

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Config.CREDENTIALS_DB);
		} catch (Exception e) {
			log(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return c;
	}
	
	public boolean exists(String table, String field, String value) {
		try {
			Connection c = getConnection();
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery("select " + field + " from " + table + " where " + field + " = '" + value + "'");
			c.close();

			if (r.next()) return true;
		} catch (SQLException e) { e.printStackTrace(); }

		return false;
	}
	
	public Credentials loadById(int id) {
		return load("select * from credentials where id = '" + (id+1) + "'"); // sqlite autoincrement hack
	}

	public Credentials loadByStorageId(int sid) {
		return load("select * from credentials where sid = '" + sid + "'");
	}
	
	public Credentials load(String q) {
		Credentials cred = null;

		try {
			Connection c = getConnection();
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(q);

			while (r.next()) {
				cred = new Credentials(
					r.getInt("id"),
					r.getInt("sid"),
					r.getString("name"),
					r.getString("token"),
					r.getString("key"));
			}

			c.close();
		} catch (SQLException e) {
			log(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		return cred;
	}
	
	public ArrayList<Credentials> load() {
		credentials.clear();

		try {
			Connection c = getConnection();
			Statement s = c.createStatement();

			String q = "select * from credentials";
			ResultSet r = s.executeQuery(q);

			while (r.next()) {
				Credentials cred = new Credentials(
						r.getInt("id"),
						r.getInt("sid"),
						r.getString("name"),
						r.getString("token"),
						r.getString("key"));
				credentials.add(cred);
			}

			c.close();
		} catch (SQLException e) {
			log(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		return credentials;
	}
	
	public boolean save(Credentials cred) {
		if (exists("credentials", "id", String.valueOf(cred.getId())))
			return update(cred);

		try {
			Connection c = getConnection();
			c.setAutoCommit(false);

			String q = "insert into credentials (sid, name, token, key) values (?,?,?,?)";

			PreparedStatement s = c.prepareStatement(q);
			s.setInt(1, cred.getStorageId());
			s.setString(2, cred.getName());
			s.setString(3, cred.getToken());
			s.setString(4, cred.getKey());
			s.executeUpdate();

			s.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			log(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	private boolean update(Credentials cred) {
		return del(cred.getId()) && save(cred);
	}

	public boolean del(int id) {
		try {
			Connection c = getConnection();
			c.setAutoCommit(false);

			String q = "delete from credentials where id = ?";
			PreparedStatement p = c.prepareStatement(q);
			p.setInt(1, id);
			p.executeUpdate();

			c.commit();
			p.close();
			c.close();

		} catch (Exception e) {
			log(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public ArrayList<Credentials> read(String filename) {
		credentials.clear();

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;

			while ((line = br.readLine()) != null) {
				if (line.trim().length() == 0) continue;
				if (line.contains("#")) continue;

				//System.out.println(line);
				int id = Integer.parseInt(line.split(":")[0]);
				String name = line.split(":")[1];
				String token = line.split(":")[2];
				String key = line.split(":")[3];

				credentials.add(new Credentials(id, name, token, key));
			}
		} catch (Exception e) {
			log("ERROR - credentials configuration file contains errors");
			log(e.getMessage());
		}

		if (credentials.size() == 0) log("WARNING - no configured Storage Services");

		return credentials;
	}
	
	public String toString() {
		String s = "";

		for (int i = 0; i<credentials.size(); i++) {
			s = s + credentials.get(i).toString();
			if ((i+1) < credentials.size()) s = s + "\n";
		}

		return s;
	}
}
