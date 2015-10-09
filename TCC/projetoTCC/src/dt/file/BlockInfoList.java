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
public class BlockInfoList implements Serializable {
	private ArrayList<BlockInfo> blocks;
	private long blockSize;
	
	public BlockInfoList(long blockSize) {
		this.blockSize = blockSize;
		this.blocks    = new ArrayList<BlockInfo>(4);
	}
	
	public void add(BlockInfo blockInfo) {
		blocks.add(blockInfo);
	}
	
    public BlockInfo  get(int i) {
        return blocks.get(i);
    }

	public long getBlockSize() {
		return blockSize;
	}
	
	public int size() {
        return blocks.size();
    }
    
	public static byte[] toBytes(BlockInfoList entries) {
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
	
	public static BlockInfoList toBlockList(byte[] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream    ois = new ObjectInputStream(bis);
			
			BlockInfoList entries = (BlockInfoList)ois.readObject();
			return entries;
		} catch (ClassNotFoundException | IOException e) {
			return null;
		}
	}
	
	public void print() {
		Iterator<BlockInfo> ite = blocks.iterator();
		
		System.out.println("Block size: "+blockSize);
		while(ite.hasNext()) {
			BlockInfo info = ite.next();
			info.print();
		}
	}
	
}
