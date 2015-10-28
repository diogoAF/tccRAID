package dt.directory;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class DirEntries implements Serializable {
	private String      path;
	private ArrayList<String> dirs;
	private ArrayList<String> files;
	
	public DirEntries(String path, ArrayList<String> dirs, ArrayList<String> files) {
		this.path  = path;
		this.dirs  = dirs;
		this.files = files;
	}
	
	public int dirCount() {
		return dirs.size();
	}
	
	public int fileCount() {
		return files.size();
	}

	public String getPath() {
		return path;
	}
	
	public boolean isRoot() {
		return path.equals("root");
	}
	
	public void list() {
		for (String names : dirs) {
			System.out.println("<dir>\t"+names);
		}
		for (String names : files) {
			System.out.println("\t"+names);
		}
	}
}
