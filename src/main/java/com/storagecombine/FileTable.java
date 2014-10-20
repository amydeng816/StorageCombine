package main.java.com.storagecombine;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;

public class FileTable {
	private final static String TAG = "FileTable: ";
	
	public FileTable() {}
	
	public static void log(String msg) {
		System.err.println(TAG + msg);
	}
	
	private static Connection getConnection() {
		Connection c = null;

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Config.FILE_TABLE);
		} catch (Exception e) {
			log(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
    		}

		//log("opened FileTable successfully");

		return c;
	}
	
	public boolean exists(String table, String field, String value) {

		try {
			Connection c = getConnection();
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery("select " + field + " from " + table + " where " + field + " = '" + value + "'");
			c.close();

			if (r.next()) return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;  
	}
	
	public FileTableEntry load(String filename) {
		try {
			Connection c = getConnection();
                        Statement s = c.createStatement();

			String q = "select * from file where filename = '" + filename + "' limit 1";
                        ResultSet r = s.executeQuery(q);

			FileTableEntry f = new FileTableEntry(
				r.getString("filename"),
				r.getLong("filesize"),
				r.getLong("last_modified"),
				r.getString("hash"));

			q = "select * from chunks where filename = '" + r.getString("filename") + "' order by num";
			r = s.executeQuery(q);

			while (r.next()) {
				Chunk chunk = new Chunk(
					r.getInt("sid"),
					r.getString("filename"),
					r.getString("chunk"),
					r.getInt("num"));

				f.addChunk(chunk);
			}

			c.close();
			return f;

        } catch (SQLException e) {
			log(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	private boolean update(FileTableEntry f) {
		return del(f.getFileName()) && save(f);
	}

	public boolean save(FileTableEntry f) {

		if (exists("file", "filename", f.getFileName()))
			return update(f);

		try {
			Connection c = getConnection();
			c.setAutoCommit(false);

			String q = "insert into file (filename, filesize, last_modified, hash) values (?,?,?,?)";

			PreparedStatement s = c.prepareStatement(q);
			s.setString(1, f.getFileName());
			s.setLong(2, f.getFileSize());
			s.setLong(3, f.getLastModified());
			s.setString(4, f.getHash());
			s.executeUpdate();
			s.close();

			q = "insert into chunks (filename, sid, chunk, num) values (?,?,?,?)";

			for (Chunk chunk : f.getChunks()) {
				s = c.prepareStatement(q);
				s.setString(1, chunk.getFileName());
				s.setInt(2, chunk.getStorageId());
				s.setString(3, chunk.getChunkName());
				s.setInt(4, chunk.getNumber());
				s.executeUpdate();
				s.close();
			}

			c.commit();
			c.close();

		} catch (Exception e) {
			log(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			return false;
    		}

		log(f.getFileName() + " successfully saved");

		return true;
	}

	public boolean del(String filename) {

		try {
			Connection c = getConnection();
			c.setAutoCommit(false);

			//String q = "select * from file where filename = '" + filename + "' limit 1";
                        //Statement s = c.createStatement();
                        //ResultSet r = s.executeQuery(q);
			//int fid = r.getInt("fid");
			//s.close();

			String q = "delete from file where filename = ?";
			PreparedStatement p = c.prepareStatement(q);
			p.setString(1, filename);
			p.executeUpdate();

			q = "delete from chunks where filename = ?";
			p = c.prepareStatement(q);
			p.setString(1, filename);
			p.executeUpdate();

			c.commit();
			p.close();
			c.close();

		} catch (Exception e) {
			log(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			return false;
		}

		log(filename + " successfully deleted");

		return true;
	}

	public ArrayList<FileTableEntry> list() {
		ArrayList<FileTableEntry> files = new ArrayList<FileTableEntry>();

		try {
			Connection c = getConnection();

			String q = "select filename from file";
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(q);

			while (r.next()) {
				files.add(load(r.getString("filename")));
			}

			c.close();
		} catch (Exception e) {
			log(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		return files;
	}

	public FileTableEntry stat(String filename) {
		return load(filename);
	}
}
