package files;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class DirEntries implements Serializable {
	private String path;
	private HashMap<String, Metadata> directories;
	private HashMap<String, Metadata> files;
	
	public DirEntries(Directory dir) {
		directories = new HashMap<String, Metadata>();
		files		= new HashMap<String, Metadata>();
		
		path = dir.getPathStr();
		
	}

	public int dirCount() {
		return directories.size();
	}
	
	public int fileCount() {
		return files.size();
	}

	public String getPath() {
		return path;
	}
	
	private void setFile(String name, Metadata metadata) {		
		files.put(name, metadata);
	}
	
	private void setDirectory(String name, Metadata metadata) {
		directories.put(name, metadata);
	}
	
	public Metadata getFile(String name) {		
		return (Metadata)files.get(name);
	}
	
	public Metadata getDirectory(String name) {		
		return (Metadata)directories.get(name);
	}
	
	public void list() {
		for (String names : directories.keySet()) {
			//Metadata metadata = getDirectory(names);
			System.out.println("<dir>\t"+names);
		}
		for (String names : files.keySet()) {
			//Metadata metadata = getFile(names);
			System.out.println("<dir>\t"+names);
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
	
	public static DirEntries toDirectory(byte[] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream    ois = new ObjectInputStream(bis);
			
			DirEntries entries = (DirEntries) ois.readObject();
			return entries;
		} catch (ClassNotFoundException | IOException e) {
			return null;
		}
	}
}
