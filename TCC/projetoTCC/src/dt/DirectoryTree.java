package dt;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import result.ResultType;
import dt.directory.Directory;
import dt.file.FileDFS;


public class DirectoryTree {
	private Directory root;
	
	public DirectoryTree() {
		root = new Directory("root", null, new Metadata(0L));
	}
	
	public Directory openDirectory(Path path, long accTime) {
	    return openDirectory(path.toString(), accTime);
	}
	public int closeDirectory(Path path){
	    return closeDirectory(path.toString());
	}
	
	
	
	public Directory openDirectory(String path, long accTime) {
		Directory dir = getDirectory(path);
		
		if(dir == null) {
			return null;
		}
		dir.getMetadata().setLastAccessTime(accTime);
		
		return dir;
	}
	
	public int closeDirectory(String path) {
		Directory dir = getDirectory(path);
		
		if(dir == null) {
			return ResultType.FAILURE;
		}
		
		dir.unlock();
		
		return ResultType.SUCCESS;
	}
	
	public FileDFS getFile(Path path) {
		FileDFS   file = null;
		Directory parent = getDirectory(path.getParent());
		
		String name = path.getFileName().toString();
		if(parent.existFile(name)) {
			file = parent.getFile(name);
		}
		
		return file;
	}

    public FileDFS getFile(String pathStr) {
        return getFile(Paths.get(pathStr));
    }
    
	public Directory getDirectory(Path path) {
		Directory 	   dir = root;
		Iterator<Path> ite = path.iterator();
		
		String name = ite.next().toString();
		while(ite.hasNext()) {
			name = ite.next().toString();
			
			dir = dir.getDirectory(name);
			if(dir == null) {
				break;
			}
		}
		 
		return dir;
	}

    public Directory getDirectory(String pathStr) {
        return getDirectory(Paths.get(pathStr));
    }
    
	public Directory getRoot() {
		return root;
	}
	
	public void print() {
		System.out.println(root.getName());
		root.printDir("");
	}
}
