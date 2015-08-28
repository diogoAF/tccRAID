package files;

public class FileBlock {
	private long servID;
	private long blockID;
	
	public FileBlock(long servID, long blockID) {
		this.servID  = servID;
		this.blockID = blockID;
	}

	public long getServID() {
		return servID;
	}

	public void setServID(long servID) {
		this.servID = servID;
	}

	public long getBlockID() {
		return blockID;
	}

	public void setBlockID(long blockID) {
		this.blockID = blockID;
	}
}
