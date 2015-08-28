package files;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class FileDFS extends DirectoryNode {
	private ArrayList<FileBlock> blockList;
	
	public FileDFS(String name, Directory parent, Metadata metadata) {
		super(name, parent, metadata);
		
		blockList = new ArrayList<FileBlock>();
	}

	public ArrayList<FileBlock> getBlockList() {
		return blockList;
	}
}
