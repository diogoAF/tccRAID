package dt.file;

import dt.DirectoryNode;
import dt.Metadata;
import dt.directory.Directory;

public class FileDFS extends DirectoryNode {
	private BlockList blockList;
	
	public FileDFS(String name, Directory parent, Metadata metadata, BlockList blockList) {
		super(name, parent, metadata);
		
		this.blockList = blockList;
	}
	
	public BlockList getBlockList() {
		return blockList;
	}

}
