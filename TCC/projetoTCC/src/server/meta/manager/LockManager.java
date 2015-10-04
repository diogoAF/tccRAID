package server.meta.manager;

import dt.DirectoryTree;

public class LockManager extends Thread{
	DirectoryTree dt;
	
	public LockManager(DirectoryTree dt) {
		this.dt = dt;
	}
	
	public void run() {
		System.out.println("LockManager running");
	}
}
