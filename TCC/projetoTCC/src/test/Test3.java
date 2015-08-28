package test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import files.Metadata;

public class Test3 {
	public static void main(String[] args) {
		Path path = Paths.get("root/");
		
		System.out.println(path.getParent());
		path = Paths.get("root/dir");
		System.out.println(path.getParent());
	}
}
