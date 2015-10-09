package dt.file;

import dt.DirectoryNode;
import dt.Metadata;
import dt.directory.Directory;

public class FileDFS extends DirectoryNode {
	private BlockInfoList blockList;
	
	public FileDFS(String name, Directory parent, Metadata metadata, BlockInfoList blockList) {
		super(name, parent, metadata);
		
		this.blockList = blockList;
	}
	
	public BlockInfoList getBlockList() {
		return blockList;
	}

}
