package dt.file;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BlockInfo implements Serializable {
    private String hostName; 
	private int    port;
	private long   blockID;
	
	public BlockInfo(String hostName, int port, long blockID) {
		this.hostName = hostName;
		this.port     = port;
		this.blockID  = blockID;
	}
	
	public boolean equals(BlockInfo comp) {
		if(this.hostName.equals(comp.getHostName()) &&
		   this.port    == comp.getPort()           &&
		   this.blockID == comp.getID()
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

	public long getID() {
		return blockID;
	}

	public void print() {
		System.out.println(hostName+":"+port+" "+blockID);
	}
}
