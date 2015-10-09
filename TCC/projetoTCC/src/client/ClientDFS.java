package client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import message.Message;
import message.Result;
import request.RequestType;
import bftsmart.tom.ServiceProxy;

public class ClientDFS extends Thread{
    private ServiceProxy proxy;
	private Path         currPath;
	private DirEntries   currDir;
	private LockList     lockList;
	
	//private LockManager lm;

    public ClientDFS(int id) {
        try {
            proxy    = new ServiceProxy(id);
	        currPath = Paths.get("");
	        openRoot();
	        
	        lockList = new LockList();
	        
	        this.start();
	        /*
	        lm = new LockManager(currPath, lockList);
	        lm.run();*/
	        
		} catch (IOException e) {
			System.out.println("Nao foi possivel obter o diretorio raiz."); 
			e.printStackTrace();
            System.exit(-1);
		}
    }
    
    public void openRoot() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(RequestType.OPENROOT);
        oos.flush();
        
        byte[]  bytes   = this.proxy.invokeOrdered(out.toByteArray());
        Message message = Message.toMessage(bytes);
        int     result  = message.getResult();
        
        if(result != Result.SUCCESS) {
            throw new IOException();
        }
        
        currDir  = DirEntries.toDirEntries(message.getBytes());
        currPath = Paths.get(currDir.getPath());
        
    }
    
    public int criateDir(String tgtName) throws IOException {
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
        Message message = Message.toMessage(bytes);
        int     result  = message.getResult();
        
        currDir = DirEntries.toDirEntries(message.getBytes());
        
        return result;
    }

    public int deleteDir(String tgtName) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(RequestType.DELETEDIR);
        oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
        oos.writeLong(System.currentTimeMillis());
        oos.flush();
        
        byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
        Message  message = Message.toMessage(bytes);
        int   result = message.getResult();

        currDir = DirEntries.toDirEntries(message.getBytes());
        
        return result;
    }

    public int renameDir(String tgtName, String newName) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(RequestType.RENAMEDIR);
        oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
        oos.writeObject(newName);
        oos.writeLong(System.currentTimeMillis());
        oos.flush();
        
        byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
        Message  message = Message.toMessage(bytes);
        int   result = message.getResult();

        currDir = DirEntries.toDirEntries(message.getBytes());
        
        return result;
    }

    public int openDir(String tgtName) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(RequestType.OPENDIR);
        oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
        oos.writeLong(System.currentTimeMillis());
        oos.flush();
        
        byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
        Message  message = Message.toMessage(bytes);
        int   result = message.getResult();
        
        currDir  = DirEntries.toDirEntries(message.getBytes());
        currPath = Paths.get(currDir.getPath());
        
        return result;
    }

    public int closeDir() throws IOException {
        if(currDir.isRoot())
            return 0;
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(RequestType.CLOSEDIR);
        oos.writeObject(currPath.toString());
        oos.flush();
        
        byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
        Message  message = Message.toMessage(bytes);
        int   result = message.getResult();
        
        currDir  = DirEntries.toDirEntries(message.getBytes());
        currPath = Paths.get(currDir.getPath());
        
        return result;
    }

    public int updateDir() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(RequestType.UPDATEDIR);
        oos.writeObject(currPath.toString());
        oos.writeLong(System.currentTimeMillis());
        oos.flush();
        
        byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
        Message  message = Message.toMessage(bytes);
        int   result = message.getResult();
        
        currDir  = DirEntries.toDirEntries(message.getBytes());
        
        return result;
    }

    public void failure(BlockInfo bInfo, String tgtName) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        System.out.println("Nao foi possivel conectar com um dos servidores de dado");
        
        
        oos.writeInt(RequestType.FAILURE);
        oos.writeInt(RequestType.CREATE);
        oos.writeObject(bInfo);
        oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
        oos.flush();
        
        this.proxy.invokeOrdered(out.toByteArray());
    }
    
    public void failure(BlockInfo bInfo) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        System.out.println("Nao foi possivel conectar com um dos servidores de dado");
        
        
        oos.writeInt(RequestType.FAILURE);
        oos.writeInt(0);
        oos.writeObject(bInfo);
        oos.flush();
        
        this.proxy.invokeOrdered(out.toByteArray());
    }
    
    public int create(String fileName) throws IOException {
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
    		Message message = Message.toMessage(bytes);
    		int     result  = message.getResult();
    		
        	if(result != Result.SUCCESS) {
        		return result;
        	}
        	
        	BlockInfoList bList = BlockInfoList.toBlockList(message.getBytes());
        	
        	bList.print();

        	
        	
            BlockInfo bInfo = null;
        	try {

        	    for(int i=0; i<4; i++) {
        	        bInfo = bList.get(i);
        	        
                    InputStream fileInputStream = new FileInputStream(file);
                    int bytesRead;
                    int bytesReadTotal = 0;
                    byte [] fileContent = new byte[(int)file.length()];
                    while ((bytesRead = fileInputStream.read(fileContent)) > 0){
                        bytesReadTotal += bytesRead;
                    }
                    fileInputStream.close();
                    

                    Block block = new Block(bytesReadTotal, bInfo.getBlockID());
                    

                    block.setBytes(fileContent, 0, bytesReadTotal);
        	        
        	        ClientServerSocket css = new ClientServerSocket(bInfo);
                    css.sendFile(block);   
        	    }
        	} catch(ConnectException e) {
        	    if(true) {
                    failure(bInfo, tgtName);
                    
                    result = Result.FAILURE;
                }
        	}
        	
        	
        	
        	
        	
        	
        	
            updateDir();
            
            return result;
    	} catch (NoSuchFileException e) {
    	    return Result.NOSUCHFILE;
    	}
    }
    
    public int delete(String tgtName) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.DELETE);
		oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
        oos.writeLong(System.currentTimeMillis());
		oos.flush();
		
		byte[]  bytes   = this.proxy.invokeOrdered(out.toByteArray());
		Message message = Message.toMessage(bytes);
		int     result  = message.getResult();

		if(result == Result.SUCCESS) {
        	BlockInfoList bList = BlockInfoList .toBlockList (message.getBytes());
        	
    	    boolean response = ( (System.currentTimeMillis()%2L) == 0 );
            
            if(response) {

                //failure(bList);
                
                result = Result.FAILURE;
            }
        	
        	
        	
        	
        	
        	bList.print();
        	
        	updateDir();
		}
		
		
		
		return result;
    }

    public int rename(String tgtName, String newName) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.RENAME);
		oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
		oos.writeObject(newName);
        oos.writeLong(System.currentTimeMillis());
		oos.flush();
		
		byte[]  bytes   = this.proxy.invokeOrdered(out.toByteArray());
		Message message = Message.toMessage(bytes);
		int     result  = message.getResult();

		currDir = DirEntries.toDirEntries(message.getBytes());
		
		return result;
    }

    public int open(String tgtName) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.OPEN);
		oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
		oos.writeLong(System.currentTimeMillis());
		oos.flush();
		
		byte[]  bytes   = this.proxy.invokeOrdered(out.toByteArray());
		Message message = Message.toMessage(bytes);
		int     result  = message.getResult();
		
		if(result == Result.SUCCESS) {
            BlockInfoList bList = BlockInfoList .toBlockList (message.getBytes());
            
            
            
            
            
            bList.print();
            
        }
		
		return result;
    }

    public int append(String tgtName) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(RequestType.APPEND);
        oos.writeObject(currPath.toString());
        oos.writeObject(tgtName);
        oos.writeLong(System.currentTimeMillis());
        oos.flush();
        
        byte[]  bytes   = this.proxy.invokeOrdered(out.toByteArray());
        Message message = Message.toMessage(bytes);
        int     result  = message.getResult();
        
        if(result == Result.SUCCESS) {
            BlockInfoList bList = BlockInfoList .toBlockList (message.getBytes());
            
            
            
            
            
            
            
            
            bList.print();
            
        }
        
        return result;
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
                
                byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
                
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
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
