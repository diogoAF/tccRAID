package server.meta;

import java.util.ArrayList;
import java.util.Iterator;

import server.ServerInfo;

public class ServerList {
	private ArrayList<ServerInfo> list;
	
	public ServerList() {
		list = new ArrayList<ServerInfo>();
	}
	
	public void add(ServerInfo newSrv) {
		list.add(newSrv);
	}
	
	public void remove(int i) {
        list.remove(i);
    }

    public void remove(String hostName, int port) {
        Iterator<ServerInfo> ite = list.iterator();
        ServerInfo info = null;
        
        while(ite.hasNext()) {
            info = ite.next();
            if(info.equals(hostName, port)) {
                ite.remove();
            }
        }
        
    }
    	
	public ServerInfo get(int i) {
		return list.get(i);
	}

    public ServerInfo get(String hostName, int port) {
        Iterator<ServerInfo> ite = list.iterator();
        ServerInfo info = null;
        
        while(ite.hasNext()) {
            info = ite.next();
            if(info.equals(hostName, port)) {
                break;
            }
        }
        
        return info;
    }
    
	public int size() {
		return list.size();
	}
	
	public ServerInfo[] nexts() throws IndexOutOfBoundsException {
		ServerInfo[] nexts = new ServerInfo[4];
		
		int max = list.size();
		for(int i = 0; i<4; i++) {
			int min = i;
			for(int j = 4 ; j<max; j++) {
				if(list.get(min).usedRate() > list.get(j).usedRate()) {
					min = j;
				}
			}
			if(i != min) {
				ServerInfo temp = list.get(i);
				list.set(i, list.get(min));
				list.set(min, temp);
			}
		}
		
		nexts[0] = list.get(0);
		nexts[1] = list.get(1);
		nexts[2] = list.get(2);
		nexts[3] = list.get(3);
		
		return nexts;
	}
	
	public void print() {
		Iterator<ServerInfo> ite = list.iterator();
		
		while(ite.hasNext()) {
			ServerInfo info =ite.next();
			info.print();
		}
	}
}
