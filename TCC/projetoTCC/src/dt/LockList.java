package dt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings("serial")
public class LockList implements Serializable{
	ArrayList<String> files;
	
	public LockList(String path) {
		files = new ArrayList<String>();
	}

	public void add(String path) {
		files.add(path);
	}
	
	public void remove(String tgtPath) {		
		Iterator<String> ite = files.iterator();
		
		while(ite.hasNext()) {
			String path = ite.next();
			if(path.equals(tgtPath)) {
				ite.remove();
				break;
			}
		}	
	}
	
	public void print() {
		Iterator<String> ite = files.iterator();
		
		while(ite.hasNext()) {
			String path = ite.next();
			System.out.println(path);
		}
	}
}
