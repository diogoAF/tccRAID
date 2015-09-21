package client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import dt.LockList;
import dt.Metadata;
import dt.directory.DirEntries;
import dt.file.BlockList;
import message.Message;
import request.RequestType;
import result.Result;
import bftsmart.tom.ServiceProxy;

public class ClientDFS {
    private ServiceProxy proxy;
	private Path         currPath;
	private DirEntries   currDir;
	private LockList     lockList;
	
	private LockManager lm;

    public ClientDFS(int id) {
        try {
            proxy    = new ServiceProxy(id);
	        currPath = Paths.get("");
	        openDir("root");
	        
	        lm = new LockManager(currPath, lockList);
	        lm.run();
	        
		} catch (IOException e) {
			System.out.println("Nao foi possivel obter o diretorio raiz."); 
			e.printStackTrace();
            System.exit(-1);
		}
    }
    
    public int create(String fileName) throws IOException {
    	int   result = -1;
    	
    	try {
    		File     file     = new File(fileName);
        	Metadata metadata = new Metadata(file);
        	String   tgtName  = file.getName();
        	
        	ByteArrayOutputStream out = new ByteArrayOutputStream();
    		ObjectOutputStream    oos = new ObjectOutputStream(out);
    		
    		oos.writeInt(RequestType.CREATE);
    		oos.writeObject(currPath.toString()+"/"+tgtName);
    		oos.writeObject(metadata);
    		oos.writeLong(System.currentTimeMillis());
    		oos.flush();
    		
    		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
    		Message  msg = Message.toMessage(bytes);
    		result = msg.getResult();
    		
        	if(result != Result.SUCCESS) {
        		return result;
        	}
        	
        	BlockList bList = BlockList.toBlockList(msg.getBytes());
        	
        	bList.print();
        	
        	
    	} catch (NoSuchFileException e) {
    		result = Result.NOSUCHFILE;
    	}
    	
    	
    	
    	updateDir();
    	
    	return result;
    }
    
    public int delete(String targetName) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.DELETE);
		oos.writeObject(currPath.toString()+"/"+targetName);
		oos.flush();
		
		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();

		currDir = DirEntries.toDirEntries(msg.getBytes());
		
		return result;
    }

    public int rename(String oldName, String newName) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.RENAME);
		oos.writeObject(currPath.toString()+"/"+oldName);
		oos.writeObject(newName);
		oos.flush();
		
		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();

		currDir = DirEntries.toDirEntries(msg.getBytes());
		
		return result;
    }

    public int open(String tgtName) throws IOException {
    	String tgtPath = currPath.toString()+"/"+tgtName;
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.OPENDIR);
		oos.writeObject(tgtPath);
		oos.writeLong(System.currentTimeMillis());
		oos.flush();
		
		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();
		
		currDir  = DirEntries.toDirEntries(msg.getBytes());
		currPath = Paths.get(currDir.getPath());
		
		return result;
    }

    public byte[] append(String path) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	byte[] bytes = null;

    	ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeInt(RequestType.APPEND);
		oos.writeObject(path);
		oos.flush();
		bytes = this.proxy.invokeOrdered(out.toByteArray());
    	
    	return bytes;
    }

    public int criateDir(String tgtName) throws IOException {
		Metadata metadata = new Metadata(System.currentTimeMillis());
		
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.CREATEDIR);
		oos.writeObject(currPath.toString()+"/"+tgtName);
		oos.writeObject(metadata);
		oos.writeLong(System.currentTimeMillis());
		oos.flush();
		
		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();
		
		currDir = DirEntries.toDirEntries(msg.getBytes());
		
    	return result;
    }

    public int deleteDir(String tgtName) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.DELETEDIR);
		oos.writeObject(currPath.toString()+"/"+tgtName);
		oos.flush();
		
		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();

		currDir = DirEntries.toDirEntries(msg.getBytes());
		
		return result;
    }

    public int renameDir(String oldName, String newName) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.RENAMEDIR);
		oos.writeObject(currPath.toString()+"/"+oldName);
		oos.writeObject(newName);
		oos.flush();
		
		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();

		currDir = DirEntries.toDirEntries(msg.getBytes());
		
		return result;
    }

    public int openDir(String tgtName) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.OPENDIR);
		oos.writeObject(currPath.toString()+"/"+tgtName);
		oos.writeLong(System.currentTimeMillis());
		oos.flush();
		
		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();
		
		currDir  = DirEntries.toDirEntries(msg.getBytes());
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
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();
		
		currDir  = DirEntries.toDirEntries(msg.getBytes());
		currPath = Paths.get(currDir.getPath());
		
		return result;
    }

    public int updateDir() throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.OPENDIR);
		oos.writeObject(currPath.toString());
		oos.writeLong(System.currentTimeMillis());
		oos.flush();
		
		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();
		
		currDir  = DirEntries.toDirEntries(msg.getBytes());
		
		return result;
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
