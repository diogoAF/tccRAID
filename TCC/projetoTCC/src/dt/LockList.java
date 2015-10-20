package dt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings("serial")
public class LockList implements Serializable {
	ArrayList<String> files;
	
	public LockList() {
		files = new ArrayList<String>();
	}

	public String get(int i) {
	    return files.get(i);
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
	
	public int size() {
	    return files.size();
	}

	public boolean isEmpty() {
	    return files.isEmpty();
	}
	
	public void list() {
	    int max = files.size();
	    
	    for(int i=0; i<max; i++) {
	        String path = files.get(i);
            System.out.println("["+i+"] "+path);
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
