package files;

import java.io.Serializable;


@SuppressWarnings("serial")
public class DirObject  implements Serializable {
	private String   name; 
	private String   parent;
	private Metadata metadata;
	
	public DirObject(String name, String parent, Metadata metadata) {
		this.name     = name;
		this.parent   = parent;	
		this.metadata = metadata;
	}

	public String getName() {
		return name;
	}
	
	public String getParent() {
		return parent;
	}
	
	public Metadata getMetadata() {
		return metadata;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public void print() {
		System.out.println("name: "  +name);
		System.out.println("parent: "+parent);
		metadata.print();
	}
}
