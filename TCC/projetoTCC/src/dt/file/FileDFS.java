package dt.file;

import dt.DirectoryNode;
import dt.Metadata;
import dt.directory.Directory;

public class FileDFS extends DirectoryNode {
	private BlockList blockList;
	
	public FileDFS(String name, Directory parent, Metadata metadata) {
		super(name, parent, metadata);
		
		blockList = new BlockList();
	}
	
	public BlockList getBlockList() {
		return blockList;
	}

}
