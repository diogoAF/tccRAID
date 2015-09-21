package dt;

import java.nio.file.Path;
import java.util.Iterator;

import dt.directory.Directory;
import dt.file.FileDFS;
import result.Result;


public class DirectoryTree {
	private Directory root;
	
	public DirectoryTree() {
		root = new Directory("root", null, new Metadata(0L));
	}
	
	public FileDFS open(Path path, long accTime) {
		FileDFS file = getFile(path);
		
		if(file == null) {
			return null;
		}
		file.getMetadata().setLastAccessTime(accTime);
		file.lock();
		
		return file;
	}

	public int create(Path path, Metadata metadata, long modTime) {
		Directory parentDir = getDirectory(path.getParent());
		String    fileName  = path.getFileName().toString();
		
		if(parentDir == null) {
			return Result.FAILURE;
		}
		if(parentDir.existFile(fileName)) {
    		return Result.FILEALREADYEXISTS;
    	}
		
		parentDir.setFile(new FileDFS(fileName, parentDir, metadata));
		parentDir.getMetadata().setLastModifiedTime(modTime);
		
		return Result.SUCCESS;
	}
	
	public int delete(Path path){
		Directory parentDir  = getDirectory(path.getParent());
		String    targetName = path.getFileName().toString();
		
		if(parentDir == null) {
			return Result.FAILURE;
		}
		if(!parentDir.existFile(targetName)) {
			return Result.FILENOTEXISTS;
		}
		
		FileDFS file = parentDir.getFile(targetName);
		if(file.isLoked()) {
			return Result.DIRLOCKED;
		}
		
		parentDir.removeFile(targetName);
		
		return Result.SUCCESS;
	}

	public int rename(Path path, String newName) {
		Directory parentDir  = getDirectory(path.getParent());
		String    targetName    = path.getFileName().toString();
		
		if(parentDir == null) {
			return Result.FAILURE;
		}
		
		FileDFS file = parentDir.getFile(targetName);
		
		if(file == null) {
			return Result.FILENOTEXISTS;
		}
		if(file.isLoked()) {
			return Result.FILELOCKED;
		}
		if(parentDir.existFile(newName)) {
			return Result.FILEALREADYEXISTS;
		}
		
		
		file.setName(newName);
		parentDir.setFile(file);
		
		parentDir.removeFile(targetName);
		
		return Result.SUCCESS;
	}
	
	public Directory openDirectory(Path path, long accTime) {
		Directory dir = getDirectory(path);
		
		if(dir == null) {
			return null;
		}
		dir.getMetadata().setLastAccessTime(accTime);
		dir.lock();
		
		return dir;
	}
	
	public int createDirectory(Path path, Metadata metadata, long modTime) {
		Directory parentDir  = getDirectory(path.getParent());
		String    targetName = path.getFileName().toString();
		
		if(parentDir == null) {
			return Result.FAILURE;
		}
		if(parentDir.existDir(targetName)) {
    		return Result.DIRALREADYEXISTS;
    	}
		
		parentDir.setDirectory(new Directory(targetName, parentDir, metadata));
		parentDir.getMetadata().setLastModifiedTime(modTime);
		
		return Result.SUCCESS;
	}
	
	public int deleteDirectory(Path path){
		Directory parentDir  = getDirectory(path.getParent());
		String    targetName = path.getFileName().toString();
		
		if(parentDir == null) {
			return Result.FAILURE;
		}
		if(!parentDir.existDir(targetName)) {
			return Result.DIRNOTEXISTS;
		}
		
		Directory dir = parentDir.getDirectory(targetName);
		if(dir.isLoked()) {
			return Result.DIRLOCKED;
		}
		
		parentDir.removeDirectory(targetName);
		
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
		if(parentDir.existDir(newName)) {
			return Result.DIRALREADYEXISTS;
		}
		
		
		dir.setName(newName);
		parentDir.setDirectory(dir);
		
		parentDir.removeDirectory(targetName);
		
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
	
	public FileDFS getFile(Path path) {
		FileDFS   file = null;
		Directory parent = getDirectory(path.getParent());
		
		String name = path.getFileName().toString();
		if(parent.existFile(name)) {
			file = parent.getFile(name);
		}
		
		return file;
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
	
	public Directory getRoot() {
		return root;
	}
	
	public void print() {
		System.out.println(root.getName());
		root.printDir("");
	}
}
