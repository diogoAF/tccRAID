package dt.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings("serial")
public class BlockList implements Serializable {
	private ArrayList<FileBlockInfo> blocks;
	
	public BlockList() {
		blocks = new ArrayList<FileBlockInfo>();
	}
	
	public void add(FileBlockInfo blockInfo) {
		blocks.add(blockInfo);
	}
	

	public static byte[] toBytes(BlockList entries) {
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ObjectOutputStream    oos = new ObjectOutputStream(bos);
			
	        oos.writeObject(entries);
	        byte[] bytes = bos.toByteArray(); 
	        oos.close();
	        bos.close();
	        return bytes;
		} catch(IOException e) {
			return null;	
		}
	}
	
	public static BlockList toBlockList(byte[] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream    ois = new ObjectInputStream(bis);
			
			BlockList entries = (BlockList)ois.readObject();
			return entries;
		} catch (ClassNotFoundException | IOException e) {
			return null;
		}
	}
	
	public void print() {
		Iterator<FileBlockInfo> ite = blocks.iterator();
		
		while(ite.hasNext()) {
			FileBlockInfo info = ite.next();
			info.print();
		}
	}
	
}
