package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import message.Message;
import files.Directory;
import files.Metadata;
import request.RequestType;
import bftsmart.tom.ServiceProxy;

public class ClientDFS {
    private ServiceProxy proxy;
	private Directory    current;
	private Path		 currPath;

    public ClientDFS(int id) {
        try {
            proxy    = new ServiceProxy(id);
	        currPath = Paths.get("");
	        openDir("root");
		} catch (IOException e) {
			System.out.println("Nao foi possivel obter o diretorio raiz."); 
			e.printStackTrace();
            System.exit(-1);
		}
    }
    
    public byte[] create(String path, Metadata metadata) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	byte[] bytes = null;
	
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeInt(RequestType.CREATE);
		oos.writeObject(path);
		oos.writeObject(metadata);
		oos.flush();
		bytes = this.proxy.invokeOrdered(out.toByteArray());
		
		return bytes;
    }
    
    public byte[] delete(String path) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	byte[] bytes = null;
    	
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeInt(RequestType.DELETE);
		oos.writeObject(path);
		oos.flush();
		bytes = this.proxy.invokeOrdered(out.toByteArray());
			
    	return bytes;
    }

    public byte[] open(String path) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	byte[] bytes = null;

    	ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeInt(RequestType.OPEN);
		oos.writeObject(path);
		oos.flush();
		bytes = this.proxy.invokeOrdered(out.toByteArray());
    	
    	return bytes;
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

    public int openDir(String dirName) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.OPENDIR);
		oos.writeObject(currPath.toString()+"/"+dirName);
		oos.flush();
		
		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();
		
		current  = Directory.toDirectory(msg.getBytes());
		currPath = Paths.get(msg.getPath());
		
		return result;
    }

    public int criateDir(String dirName, Metadata metadata) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.CREATEDIR);
		oos.writeObject(currPath.toString()+"/"+dirName);
		oos.writeObject(metadata);
		oos.flush();
		
		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();
		
		current = Directory.toDirectory(msg.getBytes());
		
    	return result;
    }

    public int deleteDir(String dirName) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.DELETEDIR);
		oos.writeObject(currPath.toString()+"/"+dirName);
		oos.flush();
		
		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();

		current  = Directory.toDirectory(msg.getBytes());
		
		return result;
    }

    public int renameDir(String dirName, String newName) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.RENAMEDIR);
		oos.writeObject(currPath.toString()+"/"+dirName);
		oos.writeObject(newName);
		oos.flush();
		
		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();

		current  = Directory.toDirectory(msg.getBytes());
		
		return result;
    }
    
    public int closeDir() throws IOException {
    	if(current.isRoot())
    		return 0;
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.CLOSEDIR);
		oos.writeObject(currPath.toString());
		oos.flush();
		
		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();
		
		current  = Directory.toDirectory(msg.getBytes());
		currPath = Paths.get(msg.getPath());
		
		return result;
    }
    
    public void exit() {
    	this.proxy.close();
    }
    
    public Directory getCurrent() {
    	return current;
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
    
}
