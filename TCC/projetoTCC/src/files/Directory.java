package files;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Directory extends DirectoryNode {
	private HashMap<String, Directory> directories;
	private HashMap<String, FileDFS>   files;
	
	public Directory(String name, Directory parent, Metadata metadata) {
		super(name, parent, metadata);
		
		directories = new HashMap<String, Directory>();
		files		= new HashMap<String, FileDFS>();
	}
	
	private Directory(Directory src) {
		super(src.getName(), null, src.getMetadata());
		
		this.directories = src.directories;
		this.files       = src.files;
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
	
	public boolean isRoot() {
		return getName().equals("root");
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
	
	public static byte[] toBytes(Directory dir) {
		
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ObjectOutputStream    oos = new ObjectOutputStream(bos);
			
	        oos.writeObject(new Directory(dir));
	        byte[] bytes = bos.toByteArray(); 
	        oos.close();
	        bos.close();
	        return bytes;
		} catch(IOException e) {
			return null;	
		}
	}
	
	public static Directory toDirectory(byte[] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream    ois = new ObjectInputStream(bis);
			
			Directory dir = (Directory) ois.readObject();
			return dir;
		} catch (ClassNotFoundException | IOException e) {
			return null;
		}
	}
	
	public Path getPath() {
		Path path = null;
		
		return path;
	}
	
	public void printDir(String sep) {
		for (String keys : directories.keySet()) {
			Directory dir = directories.get(keys);
			System.out.print("|"+sep+"-"+dir.getName());
			System.out.print("("+dir.getParent().getName()+")");
			System.out.println();
			dir.printDir(sep+" ");
		}
	}
}
