package test;

import java.nio.file.Paths;

import dt.DirectoryTree;
import dt.Metadata;
import dt.directory.Directory;

public class Test2 {
	public static void main(String[] args) {
		DirectoryTree dt;
		
		dt = new DirectoryTree();

		/*
		dt.print();
		dt.createDirectory(Paths.get("root/d1"), new Metadata(0));
		dt.createDirectory(Paths.get("root/d1/d1"), new Metadata(0));
		dt.createDirectory(Paths.get("root/d1/d1/d1"), new Metadata(0));
		dt.createDirectory(Paths.get("root/d1/d1/d1/d1"), new Metadata(0));
		dt.createDirectory(Paths.get("root/d1/d1/d1/d1/d1"), new Metadata(0));
		dt.createDirectory(Paths.get("root/d2"), new Metadata(0));
		dt.print();
		
		Directory dir = dt.openDirectory(Paths.get("root/d1/d1/d1/d1/d1"), 0L);
		System.out.println(dir.getPathStr());
		*/
	}
}
