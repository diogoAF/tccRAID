package files;

import java.io.Serializable;


@SuppressWarnings("serial")
public class DirectoryNode  implements Serializable {
	private String    name; 
	private Directory parent;
	private Metadata  metadata;
	
	public DirectoryNode(String name, Directory parent, Metadata metadata) {
		this.name     = name;
		this.parent   = parent;	
		this.metadata = metadata;
	}
	
	public String getPathStr() {
		String    path      = name;
		Directory parentDir = parent;
		
		while(parentDir != null) {
			path = parentDir.getName()+"/"+path;
			parentDir = parentDir.getParent();
		}
		
		return path;
	}
	
	public boolean isLoked() {
		return metadata.isLocked();
	}
	
	public void lock() {
		metadata.lock();
	}
	
	public void unlock() {
		metadata.unlock();
	}

	public String getName() {
		return name;
	}
	
	public Directory getParent() {
		return parent;
	}
	
	public Metadata getMetadata() {
		return metadata;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setParent(Directory parent) {
		this.parent = parent;
	}
	
	public void print() {
		System.out.println("name: "  +name);
		System.out.println("parent: "+parent.getName());
		metadata.print();
	}
}
