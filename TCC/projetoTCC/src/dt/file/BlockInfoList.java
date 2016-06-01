package dt.file;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings("serial")
public class BlockInfoList implements Serializable {
    private ArrayList<BlockInfo> blocks;
    private long blockSize;
    private int  raidType;
    private int  nServers;

    public BlockInfoList(long bSize, int raid, int n) {
        this.blockSize = bSize;
        this.raidType  = raid;
        this.nServers  = n;
        this.blocks    = new ArrayList<BlockInfo>(nServers);
    }
	
    public void add(BlockInfo blockInfo) {
        blocks.add(blockInfo);
    }
	
    public BlockInfo get(int i) {
        return blocks.get(i);
    }

    public int getBlockSize() {
            return (int)blockSize;
    }
	
    public int size() {
        return blocks.size();
    }
    
    public int getRaidType() {
        return raidType;
    }

    public int getNServers() {
        return nServers;
    }
    
    public void print() {
        System.out.println("Block size: "+blockSize);
        System.out.println("RAID type:  "+raidType);
        System.out.println("Number of servers:  "+nServers);

        Iterator<BlockInfo> ite = blocks.iterator();
            while(ite.hasNext()) {
                    BlockInfo info = ite.next();
                    info.print();
            }
    }
	
}
