package dt.directory;

import java.util.ArrayList;
import java.util.HashMap;

import dt.DirectoryNode;
import dt.Metadata;
import dt.file.FileDFS;

public class Directory extends DirectoryNode {
	private HashMap<String, Directory> dirs;
	private HashMap<String, FileDFS>   files;
	
	public Directory(String name, Directory parent, Metadata metadata) {
		super(name, parent, metadata);
		
		dirs  = new HashMap<String, Directory>();
		files = new HashMap<String, FileDFS>();
	}
	
	/*
	private Directory(Directory src) {
		super(src.getName(), null, src.getMetadata());
		
		this.dirs  = src.dirs;
		this.files = src.files;
	}
	*/
	
	public void setFile(FileDFS newFile) {		
		files.put(newFile.getName(), newFile);
	}
	
	public void setDirectory(Directory newDir) {
		dirs.put(newDir.getName(), newDir);
	}
	
	public FileDFS getFile(String name) {		
		return (FileDFS)files.get(name);
	}
	
	public Directory getDirectory(String name) {		
		return (Directory)dirs.get(name);
	}

	public void removeFile(String name) {
		files.remove(name);
	}
	
	public int removeDirectory(String name) {
		Directory dir = getDirectory(name);
		int     count = dir.dirCount();
		
		dirs.remove(name);
		
		return count;
	}

	public boolean existFile(String name) {
		if(files.get(name) == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean existDir(String name) {
		if(dirs.get(name) == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isRoot() {
		return getName().equals("root");
	}
	
	public int dirCount() {
		return dirs.size();
	}
	
	public int fileCount() {
		return files.size();
	}

	public void list() {
		for (String keys : dirs.keySet()) {
			Directory dir = dirs.get(keys);
			System.out.println("<dir>\t"+dir.getName());
		}
		for (String keys : files.keySet()) {
			FileDFS file = files.get(keys);
			System.out.println("\t"+file.getName());
		}
	}
	
	public DirEntries getDirEntries() {
		ArrayList<String> dirs  = new ArrayList<String>(this.dirs.keySet());
		ArrayList<String> files = new ArrayList<String>(this.files.keySet());		
		
		return new DirEntries(getPathStr(), dirs, files);
	}
	
	/*
	public static byte[] toBytes(Directory dir) {
		
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ObjectOutputStream    oos = new ObjectOutputStream(bos);
			
	        oos.writeObject(dir);
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
	*/
	public void printDir(String sep) {
		for (String keys : dirs.keySet()) {
			Directory dir = dirs.get(keys);
			System.out.print("|"+sep+"-"+dir.getName());
			System.out.print("("+dir.getParent().getName()+")");
			System.out.println();
			dir.printDir(sep+" ");
		}
	}
}
