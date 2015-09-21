package dt.directory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class DirEntries implements Serializable {
	private String      path;
	private ArrayList<String> dirs;
	private ArrayList<String> files;
	
	public DirEntries(String path, ArrayList<String> dirs, ArrayList<String> files) {
		this.path  = path;
		this.dirs  = dirs;
		this.files = files;
	}
	
	public int dirCount() {
		return dirs.size();
	}
	
	public int fileCount() {
		return files.size();
	}

	public String getPath() {
		return path;
	}
	
	public boolean isRoot() {
		return path.equals("root");
	}
	
	public void list() {
		for (String names : dirs) {
			System.out.println("<dir>\t"+names);
		}
		for (String names : files) {
			System.out.println("\t"+names);
		}
	}
	
	public static byte[] toBytes(DirEntries entries) {
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ObjectOutputStream    oos = new ObjectOutputStream(bos);
			
	        oos.writeObject(entries);
	        byte[] bytes = bos.toByteArray(); 
	        oos.close();
	        bos.close();
	        return bytes;
		} catch(IOException e) {
			return null;	
		}
	}
	
	public static DirEntries toDirEntries(byte[] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream    ois = new ObjectInputStream(bis);
			
			DirEntries entries = (DirEntries)ois.readObject();
			return entries;
		} catch (ClassNotFoundException | IOException e) {
			return null;
		}
	}
}
