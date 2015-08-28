package files;

import java.nio.file.Path;
import java.util.Iterator;

import result.Result;


public class DirectoryTree {
	private Directory root;
	private int		  dirCount;
	
	public DirectoryTree() {
		dirCount = 1;

		root = new Directory("root", null, new Metadata(0L));
	}
	
	public Directory openDirectory(Path path) {
		Directory dir = getDirectory(path);
		
		if(dir == null) {
			return null;
		}
		dir.lock();
		
		return dir;
	}
	
	public int createDirectory(Path path, Metadata metadata) {
		Directory parentDir = getDirectory(path.getParent());
		String    dirName   = path.getFileName().toString();
		
		if(parentDir == null) {
			return Result.FAILURE;
		}
		if(parentDir.exists(dirName)) {
    		return Result.DIRALREADYEXISTS;
    	}
		
		parentDir.setDirectory(new Directory(dirName, parentDir, metadata));
		dirCount++;
		
		return Result.SUCCESS;
	}
	
	public int deleteDirectory(Path path){
		Directory parentDir  = getDirectory(path.getParent());
		String    targetName = path.getFileName().toString();
		
		if(parentDir == null) {
			return Result.FAILURE;
		}
		if(!parentDir.exists(targetName)) {
			return Result.DIRNOTEXISTS;
		}
		
		Directory dir = parentDir.getDirectory(targetName);
		if(dir.isLoked()) {
			return Result.DIRLOCKED;
		}
		
		int remCount = parentDir.removeDirectory(targetName);
		
		dirCount -= remCount;
		
		return Result.SUCCESS;
	}

	public int renameDirectory(Path path, String newName) {
		Directory parentDir  = getDirectory(path.getParent());
		String    targetName = path.getFileName().toString();
		
		if(parentDir == null) {
			return Result.FAILURE;
		}
		
		Directory dir  = parentDir.getDirectory(targetName);
		
		if(dir == null) {
			return Result.DIRNOTEXISTS;
		}
		if(dir.isLoked()) {
			return Result.DIRLOCKED;
		}
		if(parentDir.exists(newName)) {
			return Result.DIRALREADYEXISTS;
		}
		
		parentDir.removeDirectory(targetName);
		
		dir.setName(newName);
		parentDir.setDirectory(dir);
		
		return Result.SUCCESS;
	}
	
	public int closeDirectory(Path path){
		Directory dir = getDirectory(path);
		
		if(dir == null) {
			return Result.FAILURE;
		}
		
		dir.unlock();
		
		return Result.SUCCESS;
	}
	
	public Directory getDirectory(Path path) {
		Directory 	   temp = root;
		Iterator<Path> ite  = path.iterator();
		
		String name = ite.next().toString();
		while(ite.hasNext()) {
			name = ite.next().toString();
			
			temp = temp.getDirectory(name);
			if(temp == null) {
				break;
			}
		}
		 
		return temp;
	}
	
	public Directory getRoot() {
		return root;
	}
	
	public void print() {
		System.out.println(dirCount+" directories");
		System.out.println(root.getName());
		root.printDir("");
	}
}
