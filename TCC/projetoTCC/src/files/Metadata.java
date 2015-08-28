package files;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

@SuppressWarnings("serial")
public class Metadata implements Serializable {
	private long creationTimeL;
	private long lastAccessTimeL;
	private long lastModifiedTimeL;
	
	private long size;
	
	private boolean lock;
		
	private Metadata() {
		size = 0;
		lock = false;
	}
	
	public Metadata(long time) {
		this();
		
		setTime(time);
	}
	
	public Metadata(BasicFileAttributes attr) {
		this();
		
		setAttr(attr);
	}
	
	public Metadata(File file) {
		this();
		
		BasicFileAttributes attr = null;
		Path path = Paths.get(file.getAbsolutePath());
		
		try {
			
			attr = Files.readAttributes(path, BasicFileAttributes.class);

			setAttr(attr);
			
		} catch (IOException e) {
			System.out.println("File not found.");
			e.printStackTrace();
            System.exit(-1);
		}
	}
	
	public void lock() {
		lock = true;
	}
	
	public void unlock() {
		lock = false;
	}
	
	public boolean isLocked() {
		return lock;
	}
	
	public long size() {
		return size;
	}
	
	public FileTime creationTime() {
		return FileTime.fromMillis(creationTimeL);
	}

	public FileTime lastAccessTime() {
		return FileTime.fromMillis(lastAccessTimeL);
	}

	public FileTime lastModifiedTime() {
		return FileTime.fromMillis(lastModifiedTimeL);
	}
	
	private void setTime(long time) {
		creationTimeL     = time;
		lastAccessTimeL   = time;
		lastModifiedTimeL = time;
	}
	
	private void setAttr(BasicFileAttributes attr) {
		creationTimeL     = attr.creationTime().toMillis();
		lastAccessTimeL   = attr.lastAccessTime().toMillis();
		lastModifiedTimeL = attr.lastModifiedTime().toMillis();

		size =  attr.size();
	}
	
	public void print() {
		System.out.println("creationTime: "     + creationTime().toString());
		System.out.println("lastAccessTime: "   + lastAccessTime().toString());
		System.out.println("lastModifiedTime: " + lastModifiedTime().toString());

		System.out.println("size: " + size);
	}

	
}
