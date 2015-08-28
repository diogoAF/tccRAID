package test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

public class Test1 {

	public static void main(String[] args) {
		File file = new File("testfile");
		Path path = Paths.get(file.getAbsolutePath());
		Iterator<Path> ite;
		Path sub;
		Path parent;
		Path root;


		System.out.println(path);
		parent = path.getParent();
		ite = parent.iterator();
		while(ite.hasNext()) {
			sub = ite.next();
			System.out.println(sub);
		}
		
		root = Paths.get("root/folder");

		System.out.println(root);
		ite = root.iterator();
		sub = ite.next();
		while(ite.hasNext()) {
			sub = ite.next();
			System.out.println(sub);
		}
	}
	
	

}
