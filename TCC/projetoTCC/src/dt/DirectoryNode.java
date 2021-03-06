package dt;

import dt.directory.Directory;

public class DirectoryNode {
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
	
	public boolean isLokedR() {
        return metadata.isLocked(LockType.READ);
    }

    public boolean isLokedW() {
        return metadata.isLocked(LockType.WRITE);
    }
    
	public void lock(int lockType) {
		metadata.lock(lockType);
	}
	
	public void lockR() {
        metadata.lock(LockType.READ);
    }
	
	public void lockW() {
        metadata.lock(LockType.WRITE);
    }
	
	public void unlock() {
		metadata.unlock();
	}

	public long getLastAccTime() {
	    return metadata.lastAccessTime().toMillis();
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
