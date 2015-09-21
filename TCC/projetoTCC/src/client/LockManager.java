package client;

import java.nio.file.Path;

import dt.LockList;

public class LockManager extends Thread {
	private Path     currPath;
	private LockList files;
	
	public LockManager(Path path, LockList list) {
		this.currPath = path;
		this.files    = list;
	}
	
	public void run() {
		
	}
	
	public void print() {
		System.out.println("currpath: "+currPath.toString());
		files.print();
	}
}
