package server.meta;

import java.util.ArrayList;

import server.ServerInfo;

public class ServerList {
	private ArrayList<ServerInfo> list;
	
	public ServerList() {
		list = new ArrayList<ServerInfo>();
	}
	
	public void add(ServerInfo newSrv) {
		list.add(newSrv);
	}
	
	
	
}
