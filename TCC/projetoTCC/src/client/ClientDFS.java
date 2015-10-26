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

import dt.LockList;
import dt.Metadata;
import dt.directory.DirEntries;
import dt.file.Block;
import dt.file.BlockInfo;
import dt.file.BlockInfoList;
import request.RequestType;
import result.ResultType;
import bftsmart.tom.ServiceProxy;

public class ClientDFS extends Thread{
    private ServiceProxy proxy;
	private String       currPath;
	private DirEntries   currDir;
	private LockList     lockList;
	
	//private LockManager lm;

    public ClientDFS(int id) {
        proxy    = new ServiceProxy(id);
        lockList = new LockList();
        
        this.openRoot();
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
        
        System.out.println("Nao foi possivel conectar com um dos servidores de dado");
        
        
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
        
        System.out.println("Nao foi possivel conectar com um dos servidores de dado");
        
        
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
    
    @SuppressWarnings("unused")
    public int create(String fileName) throws ClassNotFoundException, IOException  {
    	try {
    		File     file     = new File(fileName);
        	Metadata metadata = new Metadata(file);
        	String   tgtName  = file.getName();
        	
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
        	
        	bList.print();

            BlockInfo bInfo = null;
        	try {
        	    for(int i=0; i<4; i++) {
        	        bInfo = bList.get(i);
        	        
        	        FileInputStream     fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    
                    byte [] buffer = new byte[(int)file.length()];
                    int length = bis.read(buffer);
                    fis.close();
                    
                    Block block = new Block(bInfo.getID(),buffer);

        	        ClientServerSocket css = new ClientServerSocket(bInfo);
                    css.create(block);   
        	    }
        	} catch(ConnectException e) {
                failure(bInfo, tgtName);
                result = ResultType.FAILURE;
        	}
        	
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

        bList.print();
        
    	BlockInfo bInfo = null;
        try {

            for(int i=0; i<4; i++) {
                bInfo = bList.get(i);

                Block block = new Block(bInfo.getID(), null);
                
                ClientServerSocket css = new ClientServerSocket(bInfo);
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
        
        bList.print();

        BlockInfo bInfo = null;
        try {
            byte[][] fileBlock = new byte[4][];

            for(int i=0; i<4; i++) {
                bInfo = bList.get(i);

                Block block = new Block(bInfo.getID(), null);
                
                ClientServerSocket css = new ClientServerSocket(bInfo);
                fileBlock[i] = css.open(block);   
            }
            
            File file = new File("temp");
            if(!file.exists()) {
                file.mkdir();
            }
            file = new File("temp/"+tgtName);
            
            FileOutputStream     fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            
            bos.write(fileBlock[0]);
            
            bos.flush();
            bos.close();
        } catch(ConnectException e) {
            failure(bInfo);
            result = ResultType.FAILURE;
        }
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
        
        bList.print();

        BlockInfo bInfo = null;
        try {
            byte[][] fileBlock = new byte[4][];

            for(int i=0; i<4; i++) {
                bInfo = bList.get(i);

                Block block = new Block(bInfo.getID(), null);
                
                ClientServerSocket css = new ClientServerSocket(bInfo);
                fileBlock[i] = css.open(block);   
            }
            
            File file = new File("temp");
            if(!file.exists()) {
                file.mkdir();
            }
            file = new File("temp/"+tgtName);
            
            FileOutputStream     fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            
            bos.write(fileBlock[0]);
            
            bos.flush();
            bos.close();
        } catch(ConnectException e) {
            failure(bInfo);
            result = ResultType.FAILURE;
        }
        lockList.add(getCurrPath()+"/"+tgtName);
        
        return result;
    }
    
    public int close(int tgtIndex) throws ClassNotFoundException, IOException {
        try {
            Path tgtPath = Paths.get(lockList.get(tgtIndex));
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream    oos = new ObjectOutputStream(out);
            
            oos.writeInt(RequestType.CLOSE);
            oos.writeObject(tgtPath.getParent());
            oos.writeObject(tgtPath.getFileName());
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
    
    @SuppressWarnings("unused")
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
    	//return currDir.getPath();
    }
    
    public byte[] executeOrdered(byte[] request){
        return this.proxy.invokeOrdered(request);
    }    
    
    public byte[] executeUnordered(byte[] request){
        return this.proxy.invokeUnordered(request);
    }
    
}
