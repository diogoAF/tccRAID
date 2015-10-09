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
	
	public void setFile(FileDFS newFile, long modTime) {		
		files.put(newFile.getName(), newFile);
		getMetadata().setLastModifiedTime(modTime);
	}
	
	public void setDirectory(Directory newDir, long modTime) {
		dirs.put(newDir.getName(), newDir);
        getMetadata().setLastModifiedTime(modTime);
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
    
	public void removeFile(String name, long modTime) {
		files.remove(name);
        getMetadata().setLastModifiedTime(modTime);
	}
	
	public void removeDirectory(String name, long modTime) {
		dirs.remove(name);
        getMetadata().setLastModifiedTime(modTime);
	}
	
	public void renameFile(String tgtName, String newName, long modTime) {
	    FileDFS target = files.get(tgtName);
        files.remove(tgtName);
        target.setName(newName);
        setFile(target, modTime);
        getMetadata().setLastModifiedTime(modTime);
    }

    public void renameDirectory(String tgtName, String newName, long modTime) {
        Directory target = dirs.get(tgtName);
        dirs.remove(tgtName);
        target.setName(newName);
        setDirectory(target, modTime);
        getMetadata().setLastModifiedTime(modTime);
    }
    
	public boolean existFile(String name) {
		return files.containsKey(name);
	}
	
	public boolean existDir(String name) {
	    return dirs.containsKey(name);
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
