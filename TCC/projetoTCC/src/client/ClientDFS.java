package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import dt.LockList;
import dt.Metadata;
import dt.directory.DirEntries;
import dt.file.Block;
import dt.file.BlockInfo;
import dt.file.BlockInfoList;
import request.RequestType;
import result.ResultType;
import server.meta.RaidType;
import bftsmart.tom.ServiceProxy;

public class ClientDFS extends Thread{
    private boolean verbose;
    
    private ServiceProxy proxy;
	private String       currPath;
	private DirEntries   currDir;
	private LockList     lockList;
	
    public ClientDFS(int id, boolean ver) {
        proxy = new ServiceProxy(id);
        lockList = new LockList();
        
        verbose = ver;
        
        this.openRoot();
        if(verbose)
            this.start(); 
    }
    
    private void openRoot() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream    oos = new ObjectOutputStream(out);
            
            oos.writeInt(RequestType.OPENROOT);
            oos.flush();
            
            byte[]  bytes   = this.proxy.invokeUnordered(out.toByteArray());
    
            ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
            ObjectInputStream    ois = new ObjectInputStream(in);
            
            int result = ois.readInt();
            
            if(result != ResultType.SUCCESS) {
                throw new IOException();
            }
            
            currDir  = (DirEntries) ois.readObject();
            currPath = currDir.getPath();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Nao foi possivel abrir o diretorio raiz."); 
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    public int criateDir(String tgtName) throws ClassNotFoundException, IOException {
        Metadata metadata = new Metadata(System.currentTimeMillis());
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(RequestType.CREATEDIR);
        oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
        oos.writeObject(metadata);
        oos.writeLong(System.currentTimeMillis());
        oos.flush();
        
        byte[]  bytes   = this.proxy.invokeOrdered(out.toByteArray());
        
        ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
        ObjectInputStream    ois = new ObjectInputStream(in);

        int result = ois.readInt();
        currDir    = (DirEntries)ois.readObject();

        if(result == ResultType.FAILURE) {
            currPath = currDir.getPath();
        }
        
        return result;
    }

    public int deleteDir(String tgtName) throws ClassNotFoundException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(RequestType.DELETEDIR);
        oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
        oos.writeLong(System.currentTimeMillis());
        oos.flush();
        
        byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());

        ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
        ObjectInputStream    ois = new ObjectInputStream(in);
        
        int result = ois.readInt();
        currDir    = (DirEntries)ois.readObject();

        if(result == ResultType.FAILURE) {
            currPath = currDir.getPath();
        }
        
        return result;
    }

    public int renameDir(String tgtName, String newName) throws ClassNotFoundException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(RequestType.RENAMEDIR);
        oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
        oos.writeObject(newName);
        oos.writeLong(System.currentTimeMillis());
        oos.flush();
        
        byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());

        ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
        ObjectInputStream    ois = new ObjectInputStream(in);
        
        int result = ois.readInt();
        currDir    = (DirEntries)ois.readObject();

        if(result == ResultType.FAILURE) {
            currPath = currDir.getPath();
        }
        
        return result;
    }

    public int openDir(String tgtName) throws ClassNotFoundException, IOException  {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(RequestType.OPENDIR);
        oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
        oos.writeLong(System.currentTimeMillis());
        oos.flush();
        
        byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
        
        ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
        ObjectInputStream    ois = new ObjectInputStream(in);
        
        int result = ois.readInt();
        currDir    = (DirEntries)ois.readObject();
        currPath   = currDir.getPath();
        
        return result;
    }

    public int closeDir() throws ClassNotFoundException, IOException {
        if(currDir.isRoot())
            return 0;
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(RequestType.CLOSEDIR);
        oos.writeObject(currPath.toString());
        oos.flush();
        
        byte[] bytes = this.proxy.invokeUnordered(out.toByteArray());

        ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
        ObjectInputStream    ois = new ObjectInputStream(in);
        
        int result = ois.readInt();
        currDir    = (DirEntries)ois.readObject();
        currPath   = currDir.getPath();
        
        return result;
    }

    public int updateDir() throws ClassNotFoundException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(RequestType.UPDATEDIR);
        oos.writeObject(currPath.toString());
        oos.writeLong(System.currentTimeMillis());
        oos.flush();
        
        byte[] bytes = this.proxy.invokeUnordered(out.toByteArray());

        ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
        ObjectInputStream    ois = new ObjectInputStream(in);
        
        int result = ois.readInt();
        currDir    = (DirEntries)ois.readObject();
        
        if(result == ResultType.FAILURE) {
            currPath = currDir.getPath();
        }
        
        return result;
    }

    public void failure(BlockInfo bInfo, String tgtName) throws ClassNotFoundException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        //System.out.println("Nao foi possivel conectar com um dos servidores de dado");
        
        
        oos.writeInt(RequestType.FAILURE);
        oos.writeInt(RequestType.CREATE);
        oos.writeObject(bInfo);
        oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
        oos.flush();
        
        byte[] bytes = this.proxy.invokeUnordered(out.toByteArray());

        ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
        ObjectInputStream    ois = new ObjectInputStream(in);
        
        currDir  = (DirEntries)ois.readObject();
        currPath = currDir.getPath();
    }
    
    public void failure(BlockInfo bInfo) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        //System.out.println("Nao foi possivel conectar com um dos servidores de dado");
        
        
        oos.writeInt(RequestType.FAILURE);
        oos.writeInt(0);
        oos.writeObject(bInfo);
        oos.flush();
        
        byte[] bytes = this.proxy.invokeUnordered(out.toByteArray());

        ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
        ObjectInputStream    ois = new ObjectInputStream(in);
        
        currDir  = (DirEntries)ois.readObject();
        currPath = currDir.getPath();
    }
    
    public int create(String fileName, String tgtName) throws ClassNotFoundException, IOException  {
        try {
            File     file     = new File(fileName);
            Metadata metadata = new Metadata(file);
            
            if(tgtName == null)
                tgtName  = file.getName();
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream    oos = new ObjectOutputStream(out);
            
            oos.writeInt(RequestType.CREATE);
            oos.writeObject(currPath.toString());
            oos.writeObject(tgtName);
            oos.writeObject(metadata);
            oos.writeLong(System.currentTimeMillis());
            oos.flush();
            
            byte[]  bytes   = this.proxy.invokeOrdered(out.toByteArray());

            ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
            ObjectInputStream    ois = new ObjectInputStream(in);
            
            int result = ois.readInt();
            currDir    = (DirEntries)ois.readObject();
            
            if(result != ResultType.SUCCESS) {
                if(result == ResultType.FAILURE) {
                    currPath = currDir.getPath();
                }
                return result;
            }
            
            BlockInfoList bList = (BlockInfoList)ois.readObject();

            if(verbose)
                bList.print();
            
            int raidType  = bList.getRaidType();
            int nServers  = bList.getNServers();
            int blockSize = bList.getBlockSize();

            FileInputStream     fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            
            BlockInfo bInfo = null;
            switch(raidType) {
            case(RaidType.RAID0):
                for(int i=0; i<nServers; i++) {
                    byte [] buffer = new byte[blockSize];
                    Arrays.fill(buffer, (byte) 0);
                    
                    try {
                        bInfo = bList.get(i);
                        
                        bis.read(buffer, 0, blockSize);
                        
                        Block block = new Block(bInfo.getID(),buffer);

                        ClientServerSocket css = new ClientServerSocket(bInfo, verbose);
                        css.create(block);   
                    } catch(ConnectException e) {
                        failure(bInfo);
                        result = ResultType.FAILURE;
                        break;
                    }
                }
                break;

            case(RaidType.RAID1):
                int buffSize = (int)file.length();
                for(int i=0; i<nServers; i++) {
                    byte [] buffer = new byte[buffSize];
                    
                    try {
                        bInfo = bList.get(i);
                        
                        bis.read(buffer);
                        
                        Block block = new Block(bInfo.getID(),buffer);

                        ClientServerSocket css = new ClientServerSocket(bInfo, verbose);
                        css.create(block);   
                    } catch(ConnectException e) {
                        failure(bInfo);
                        result = ResultType.FAILURE;
                        break;
                    }
                }
                break;
                
            case(RaidType.RAID5):
                byte [] parityBlock = new byte[blockSize];
                Arrays.fill(parityBlock, (byte) 0);
                
                for(int i=0; i<nServers-1; i++) {
                    byte [] buffer = new byte[blockSize];
                    Arrays.fill(buffer, (byte) 0);
                    
                    try {
                        bInfo = bList.get(i);
                        
                        bis.read(buffer, 0, blockSize);
                        
                        Block block = new Block(bInfo.getID(),buffer);

                        ClientServerSocket css = new ClientServerSocket(bInfo, verbose);
                        css.create(block);  
                        
                        for (int j = 0; j<blockSize; j++)
                            parityBlock[j] ^= buffer[j];
                    } catch(ConnectException e) {
                        failure(bInfo);
                        result = ResultType.FAILURE;
                        break;
                    }
                }
                try {
                    bInfo = bList.get(nServers-1);
                    Block block = new Block(bInfo.getID(),parityBlock);
                    
                    ClientServerSocket css = new ClientServerSocket(bInfo, verbose);
                    css.create(block); 
                } catch(ConnectException e) {
                    failure(bInfo);
                    result = ResultType.FAILURE;
                }
                break;
                
            default:
                result = ResultType.FAILURE;
                break;
                
            }
            

            fis.close();
            
            return result;
        } catch (NoSuchFileException e) {
            return ResultType.NOSUCHFILE;
        }
    }
    
    public int delete(String tgtName) throws ClassNotFoundException, IOException  {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.DELETE);
		oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
        oos.writeLong(System.currentTimeMillis());
		oos.flush();
		
		byte[]  bytes   = this.proxy.invokeOrdered(out.toByteArray());

        ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
        ObjectInputStream    ois = new ObjectInputStream(in);
        
        int result = ois.readInt();
        currDir    = (DirEntries)ois.readObject();
        
        if(result != ResultType.SUCCESS) {
            if(result == ResultType.FAILURE) {
                currPath = currDir.getPath();
            }
            return result;
        }
        
        BlockInfoList bList = (BlockInfoList)ois.readObject();
        
        if(verbose)
            bList.print();

        int nServers = bList.getNServers();
        
    	BlockInfo bInfo = null;
        try {

            for(int i=0; i<nServers; i++) {
                bInfo = bList.get(i);

                Block block = new Block(bInfo.getID(), null);
                
                ClientServerSocket css = new ClientServerSocket(bInfo, verbose);
                css.delete(block);   
            }
            
        } catch(ConnectException e) {
            failure(bInfo);
            result = ResultType.FAILURE;
        }
    	
		return result;
    }

    public int rename(String tgtName, String newName) throws ClassNotFoundException, IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.RENAME);
		oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
		oos.writeObject(newName);
        oos.writeLong(System.currentTimeMillis());
		oos.flush();
		
		byte[]  bytes   = this.proxy.invokeOrdered(out.toByteArray());

        ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
        ObjectInputStream    ois = new ObjectInputStream(in);
        
        int result = ois.readInt();
        currDir    = (DirEntries)ois.readObject();
        
        if(result == ResultType.FAILURE) {
            currPath = currDir.getPath();
        }
        
		return result;
    }

    public int open(String tgtName) throws ClassNotFoundException, IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.OPEN);
		oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
		oos.writeLong(System.currentTimeMillis());
		oos.flush();
		
		byte[]  bytes   = this.proxy.invokeOrdered(out.toByteArray());

        ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
        ObjectInputStream    ois = new ObjectInputStream(in);
        
        int result = ois.readInt();
        currDir    = (DirEntries)ois.readObject();
        
        if(result != ResultType.SUCCESS) {
            if(result == ResultType.FAILURE) {
                currPath = currDir.getPath();
            }
            return result;
        }

        BlockInfoList bList = (BlockInfoList)ois.readObject();
        long fileSize       = ois.readLong();
        
        if(verbose)
            bList.print();

        int raidType  = bList.getRaidType();
        int nServers  = bList.getNServers();
        int bSize     = bList.getBlockSize();
        
        File file = new File("temp");
        if(!file.exists()) {
            file.mkdir();
        }
        file = new File("temp/"+tgtName);
        
        if(!verbose)
            file = new File("temp/file");
        
        FileOutputStream     fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        
        BlockInfo bInfo        = null;
        int       failureCount = 0;
        
        switch(raidType) {
        case(RaidType.RAID0):
            for(int i=0; i<nServers; i++) {
                try {
                    bInfo = bList.get(i);

                    ClientServerSocket css   = new ClientServerSocket(bInfo, verbose);

                    byte[] fileBlock = css.open(new Block(bInfo.getID(), null)); 
                    if(fileSize < bSize)
                        bos.write(fileBlock, 0, (int) fileSize);
                    else {
                        bos.write(fileBlock);
                        fileSize -= bSize;
                    }
                } catch(ConnectException e) {
                    failure(bInfo);
                    file.delete();
                    result = ResultType.FAILURE;
                    break;
                }           
            }
            break;

        case(RaidType.RAID1):
            for(int i=0; i<nServers; ) {
                try {
                    bInfo = bList.get(i);

                    ClientServerSocket css = new ClientServerSocket(bInfo, verbose);

                    byte[] fileBlock = css.open(new Block(bInfo.getID(), null)); 
                    bos.write(fileBlock);
                } catch(ConnectException e) {
                    failureCount++;
                    failure(bInfo);
                    if(nServers == failureCount) {
                        file.delete();
                        result = ResultType.FAILURE;
                        break;
                    }
                    i++;
                }
            }
            break;
            
        case(RaidType.RAID5):
            int failureIndex = -1;
            byte[] buffer = new byte[nServers*bSize];
            for(int i=0; i<nServers-1; i++) {
                try {
                    bInfo  = bList.get(i);
    
                    ClientServerSocket css   = new ClientServerSocket(bInfo, verbose);
    
                    byte[] fileBlock = css.open(new Block(bInfo.getID(), null)); 
                    blockConcat(buffer, fileBlock, i*bSize, bSize);
    
                } catch(ConnectException e) {
                    failureCount++;
                    failureIndex = i;
                    failure(bInfo);
                    if(failureCount>1) {
                        file.delete();
                        result = ResultType.FAILURE;
                        break;
                    }
                }
            }
            
            if(failureCount > 0) {
                try {
                    bInfo = bList.get(nServers-1);
                    
                    ClientServerSocket css   = new ClientServerSocket(bInfo, verbose);
                    
                    byte[] fileBlock = css.open(new Block(bInfo.getID(), null));
                    blockConcat(buffer, fileBlock, failureIndex*bSize, bSize);
                    for(int i=0; i<nServers; i++) {
                        if(i != failureIndex){
                            for(int j=0; j<bSize; j++) {
                                buffer[failureIndex*bSize+j] ^= buffer[i*bSize+j];                            }
                        }
                    }
                } catch(ConnectException e) {
                    failure(bInfo);
                    if(failureCount>1) {
                        file.delete();
                        result = ResultType.FAILURE;
                        break;
                    }
                }
            }
            bos.write(buffer, 0, (int) fileSize);
            break;
            
        default:
            file.delete();
            result = ResultType.FAILURE;
            break;
        }

        bos.flush();
        bos.close();
        if(verbose)
            file.deleteOnExit();
        
        lockList.add(getCurrPath()+"/"+tgtName);
        
		return result;
    }

    public int append(String tgtName) throws ClassNotFoundException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(RequestType.APPEND);
        oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
        oos.writeLong(System.currentTimeMillis());
        oos.flush();

        byte[]  bytes   = this.proxy.invokeOrdered(out.toByteArray());

        ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
        ObjectInputStream    ois = new ObjectInputStream(in);
        
        int result = ois.readInt();
        currDir    = (DirEntries)ois.readObject();
        
        if(result != ResultType.SUCCESS) {
            if(result == ResultType.FAILURE) {
                currPath = currDir.getPath();
            }
            return result;
        }

        BlockInfoList bList = (BlockInfoList)ois.readObject();
        long fileSize       = ois.readLong();
        
        if(verbose)
            bList.print();

        int raidType  = bList.getRaidType();
        int nServers  = bList.getNServers();
        int bSize     = bList.getBlockSize();
        
        File file = new File("temp");
        if(!file.exists()) {
            file.mkdir();
        }
        file = new File("temp/"+tgtName);
        
        FileOutputStream     fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        
        BlockInfo bInfo        = null;
        int       failureCount = 0;
        
        switch(raidType) {
        case(RaidType.RAID0):
            for(int i=0; i<nServers; i++) {
                try {
                    bInfo = bList.get(i);

                    ClientServerSocket css   = new ClientServerSocket(bInfo, verbose);

                    byte[] fileBlock = css.open(new Block(bInfo.getID(), null)); 
                    if(fileSize < bSize)
                        bos.write(fileBlock, 0, (int) fileSize);
                    else {
                        bos.write(fileBlock);
                        fileSize -= bSize;
                    }
                } catch(ConnectException e) {
                    failure(bInfo);
                    file.delete();
                    result = ResultType.FAILURE;
                    break;
                }           
            }
            break;

        case(RaidType.RAID1):
            for(int i=0; i<nServers; ) {
                try {
                    bInfo = bList.get(i);

                    ClientServerSocket css = new ClientServerSocket(bInfo, verbose);

                    byte[] fileBlock = css.open(new Block(bInfo.getID(), null)); 
                    bos.write(fileBlock);
                } catch(ConnectException e) {
                    failureCount++;
                    failure(bInfo);
                    if(nServers == failureCount) {
                        file.delete();
                        result = ResultType.FAILURE;
                        break;
                    }
                    i++;
                }
            }
            break;
            
        case(RaidType.RAID5):
            int failureIndex = -1;
            byte[] buffer = new byte[nServers*bSize];
            for(int i=0; i<nServers-1; i++) {
                try {
                    bInfo  = bList.get(i);
    
                    ClientServerSocket css   = new ClientServerSocket(bInfo, verbose);
    
                    byte[] fileBlock = css.open(new Block(bInfo.getID(), null)); 
                    blockConcat(buffer, fileBlock, i*bSize, bSize);
    
                } catch(ConnectException e) {
                    failureCount++;
                    failureIndex = i;
                    failure(bInfo);
                    if(failureCount>1) {
                        file.delete();
                        result = ResultType.FAILURE;
                        break;
                    }
                }
            }
            
            if(failureCount > 0) {
                try {
                    bInfo = bList.get(nServers-1);
                    
                    ClientServerSocket css   = new ClientServerSocket(bInfo, verbose);
                    
                    byte[] fileBlock = css.open(new Block(bInfo.getID(), null));
                    blockConcat(buffer, fileBlock, failureIndex*bSize, bSize);
                    for(int i=0; i<nServers; i++) {
                        if(i != failureIndex){
                            for(int j=0; j<bSize; j++) {
                                buffer[failureIndex*bSize+j] ^= buffer[i*bSize+j];                            }
                        }
                    }
                } catch(ConnectException e) {
                    failure(bInfo);
                    if(failureCount>1) {
                        file.delete();
                        result = ResultType.FAILURE;
                        break;
                    }
                }
            }
            bos.write(buffer, 0, (int) fileSize);
            break;
            
        default:
            file.delete();
            result = ResultType.FAILURE;
            break;
        }

        bos.flush();
        bos.close();
        if(verbose)
            file.deleteOnExit();
        
        lockList.add(getCurrPath()+"/"+tgtName);
        
        return result;
    }
    
    public int close(int tgtIndex) throws ClassNotFoundException, IOException {
        try {
            Path tgtPath = Paths.get(lockList.get(tgtIndex));
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream    oos = new ObjectOutputStream(out);
            
            oos.writeInt(RequestType.CLOSE);
            oos.writeObject(tgtPath.getParent().toString());
            oos.writeObject(tgtPath.getFileName().toString());
            oos.writeLong(System.currentTimeMillis());
            oos.flush();
            
            byte[]  bytes   = this.proxy.invokeOrdered(out.toByteArray());

            ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
            ObjectInputStream    ois = new ObjectInputStream(in);
            
            int result = ois.readInt();
            
            if(result != ResultType.SUCCESS) {
                currDir = (DirEntries)ois.readObject();
                if(result == ResultType.FAILURE) {
                    currPath = currDir.getPath();
                }
            }
            
            return result;
        } catch (IndexOutOfBoundsException e) {
            return ResultType.WRONGINDEX;
        }
    }
    
    
    public void run() {
        while(true) {
            try {
                Thread.sleep(30*1000);
                
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectOutputStream    oos = new ObjectOutputStream(out);
                
                oos.writeInt(RequestType.UPDATEACC);
                oos.writeObject(currPath.toString());
                oos.writeObject(lockList);
                oos.writeLong(System.currentTimeMillis());
                oos.flush();
                
                @SuppressWarnings("unused")
                byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
                
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public LockList getLockList() {
        return lockList;
    }
    
    public void exit() {
        this.proxy.close();
    }
    
    public DirEntries getCurrDir() {
    	return currDir;
    }
    
    public String getCurrPath() {
    	return currPath.toString();
    }
    
    public byte[] executeOrdered(byte[] request){
        return this.proxy.invokeOrdered(request);
    }    
    
    public byte[] executeUnordered(byte[] request){
        return this.proxy.invokeUnordered(request);
    }
    
    private void blockConcat(byte[] tgt, byte[] src, int offset, int n) {
        for(int i=0; i<n; i++)
            tgt[offset+i] = src[i];
    }
}
