package server;

public class ServerInfo {
    private String hostName;
	private int    port;
	private long   capacity;
	private long   size;
	private long   lastID;
	
	public ServerInfo(String host, int port, long cap, long size, long id) {
		this.hostName = host;
		this.port     = port;
		this.capacity = cap;
		this.size     = size;
		this.lastID   = id;
	}

	public ServerInfo(String host, int port, long cap) {
		this(host, port, cap, 0L, 0L);
	}
	
	public boolean equals(ServerInfo comp) {
		if(this.hostName.equals(comp.getHostName()) &&
		   this.port == comp.getPort()          
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
	
	
}
