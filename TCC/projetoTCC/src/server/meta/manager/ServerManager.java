package server.meta.manager;

import server.ServerInfo;
import server.meta.ServerList;

public class ServerManager extends Thread {
	ServerList list;
	
	public ServerManager(ServerList list) {
		this.list = list;
	}
	
	public void run() {
		System.out.println("ServerManager is starting");
		
		while(true) {
		    try {
                Thread.sleep(30*1000);
                
                int max = list.size();
                for(int i=0; i<max; i++) {
                    ServerInfo info = list.get(i);
                    if( (System.currentTimeMillis()-info.lastAccessTime()) >= 30*1000) {
                        list.remove(i);
                        System.out.println();
                        System.out.println("server"+info.getHostName()+":"+info.getPort()+"is lost:");
                        System.out.println("current servers:");
                        list.print();
                        max = list.size();
                    }
                }
                
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(0);
            }
		}
	}
}
