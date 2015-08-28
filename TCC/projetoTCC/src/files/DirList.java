package files;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class DirList implements Serializable {
	private HashMap<String, Directory> directories;
	private HashMap<String, FileDFS>   files;
	
	public DirList() {
		directories = new HashMap<String, Directory>();
		files		= new HashMap<String, FileDFS>();
	}

	public void setFile(FileDFS newFile) {		
		files.put(newFile.getName(), newFile);
	}
	
	public void setDirectory(Directory newDir) {
		directories.put(newDir.getName(), newDir);
	}
	
	public FileDFS getFile(String name) {		
		return (FileDFS)files.get(name);
	}
	
	public Directory getDirectory(String name) {		
		return (Directory)directories.get(name);
	}
	
	public int removeDirectory(String name) {
		Directory dir = getDirectory(name);
		int     count = dir.dirCount();
		
		directories.remove(name);
		
		return count;
	}
	
	public boolean exists(String name) {
		Directory dir = directories.get(name);
		
		if(dir == null) {
			return false;
		} else {
			return true;
		}
	}

	public int dirCount() {
		return directories.size();
	}
	
	public int fileCount() {
		return files.size();
	}

	public void list() {
		for (String keys : directories.keySet()) {
			Directory dir = directories.get(keys);
			System.out.println("<dir>\t"+dir.getName());
		}
		for (String keys : files.keySet()) {
			FileDFS file = files.get(keys);
			System.out.println("\t"+file.getName());
		}
	}
	
	public static byte[] toBytes(DirList list) {
		
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ObjectOutputStream    oos = new ObjectOutputStream(bos);
			
	        oos.writeObject(list);
	        byte[] bytes = bos.toByteArray(); 
	        oos.close();
	        bos.close();
	        return bytes;
		} catch(IOException e) {
			return null;	
		}
	}
	
	public static DirList toDirectory(byte[] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream    ois = new ObjectInputStream(bis);
			
			DirList list = (DirList) ois.readObject();
			return list;
		} catch (ClassNotFoundException | IOException e) {
			return null;
		}
	}
}
