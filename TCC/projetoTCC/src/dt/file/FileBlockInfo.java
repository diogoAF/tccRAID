package dt.file;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FileBlockInfo implements Serializable {
    private String hostName; 
	private int    port;
	private long   blockID;
	
	public FileBlockInfo(String hostName, int port, long blockID) {
		this.hostName = hostName;
		this.port     = port;
		this.blockID  = blockID;
	}
	
	public boolean equals(FileBlockInfo comp) {
		if(this.hostName.equals(comp.getHostName()) &&
		   this.port    == comp.getPort()           &&
		   this.blockID == comp.getBlockID()
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

	public long getBlockID() {
		return blockID;
	}

	public void print() {
		System.out.println(hostName+":"+port+" "+blockID);
	}
}
