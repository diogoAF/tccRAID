package server;

public class ServerInfo {
    private String hostName;
	private int    port;
	private long   capacity;
	private long   size;
	private long   lastID;
    private long   lastAccessTime;
	
	public ServerInfo(String host, int port, long cap, long size, long id, long accTime) {
		this.hostName = host;
		this.port     = port;
		this.capacity = cap;
		this.size     = size;
		this.lastID   = id;

	    this.lastAccessTime = accTime;
	}

	public ServerInfo(String host, int port, long cap, long accTime) {
		this(host, port, cap, 0L, 0L, accTime);
	}
	
	public ServerInfo(ServerInfo si) {
		this.hostName = si.getHostName();
		this.port     = si.getPort();
		this.capacity = si.getCapacity();
		this.size     = si.getSize();
		this.lastID   = si.getLastID();
	}
	
	public void addSize(long size) {
		this.size += size;
	}

    public void subSize(long size) {
        this.size -= size;
    }
    
	public float usageRate() {
		return (float)size/(float)capacity;
	}

	public long getID() {
		long ID = lastID; 
		
		lastID++;
		
		return ID;
	}
	
	public void setLastAccTime(long accTime) {
	    this.lastAccessTime = accTime;
	}
	
	public boolean equals(ServerInfo comp) {
		if( this.hostName.equals(comp.getHostName()) &&
		    this.port == comp.getPort()          
		) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean equals(String hostName, int port) {
	    if( this.hostName.equals(hostName) &&
            this.port == port          
         ) {
             return true;
         } else {
             return false;
         }
    }

	public String getHostName() {
		return hostName;
	}

	public int getPort() {
		return port;
	}

	public long getCapacity() {
		return capacity;
	}

	public long getSize() {
		return size;
	}

	public long getLastID() {
		return lastID;
	}
	
	public long lastAccessTime() {
        return lastAccessTime;
    }
	
	public void print() {
		System.out.printf("host %s:%d\tused %1.4f(%d/%d)\tlastID %d",
				hostName, port, usageRate(), size, capacity, lastID);
		System.out.println();
	}

}
