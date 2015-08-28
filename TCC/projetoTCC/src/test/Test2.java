package test;

import java.nio.file.Paths;

import files.DirectoryTree;
import files.Metadata;

public class Test2 {
	public static void main(String[] args) {
		DirectoryTree dt;
		
		dt = new DirectoryTree();

		dt.print();
		dt.createDirectory(Paths.get("root/d1/d11"), new Metadata(0));
		dt.createDirectory(Paths.get("root/d1/d12/d121"), new Metadata(0));
		dt.createDirectory(Paths.get("root/d2/d21"), new Metadata(0));
		dt.print();

		dt.deleteDirectory(Paths.get("root/d1/d12/d121"));
	}
}
