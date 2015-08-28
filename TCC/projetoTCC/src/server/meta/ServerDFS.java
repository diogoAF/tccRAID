package server.meta;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import message.Message;
import files.Directory;
import files.DirectoryTree;
import files.Metadata;
import request.RequestType;
import result.Result;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;

public class ServerDFS extends DefaultSingleRecoverable {
	DirectoryTree dt;

    public ServerDFS(int id){
         new ServiceReplica(id, this, this);
         dt = new DirectoryTree();
    }
    
    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        ByteArrayInputStream in = new ByteArrayInputStream(command);
		
        int    reqType	   = -1;
		byte[] resultBytes = null;

        System.out.println("\nexecuteOrdered");

		try {
			ObjectInputStream ois = new ObjectInputStream(in);
			reqType = ois.readInt();

			switch(reqType) {
				case RequestType.CREATE:
					resultBytes = criate(ois);
					break;
					
				case RequestType.DELETE:
					resultBytes = delete(ois);
					break;

				case RequestType.OPEN:
					resultBytes = open(ois);
					break;

				case RequestType.APPEND:
					resultBytes = append(ois);
					break;

				case RequestType.OPENDIR:
					resultBytes = openDir(ois);
					break;

				case RequestType.CREATEDIR:
					resultBytes = criateDir(ois);
					break;

				case RequestType.DELETEDIR:
					resultBytes = deleteDir(ois);
					break;

				case RequestType.RENAMEDIR:
					resultBytes = renameDir(ois);
					break;

				case RequestType.CLOSEDIR:
					resultBytes = closeDir(ois);
					break;

				case RequestType.JOIN:
					resultBytes = join(ois);
					break;
					
				default:
					System.out.println("Unknown request number " + reqType);
					break;
			}
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
        return resultBytes;
    }

    @Override
    public byte[] executeUnordered(byte[] command, MessageContext msgCtx) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("executeUnordered");
        return command;
    }

    @Override
    public void installSnapshot(byte[] state) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] getSnapshot() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return new byte[0];
        
    }

    private byte[] criate(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path     path;
    	Metadata metadata;
    	
    	path     = Paths.get((String)ois.readObject());
    	metadata = (Metadata)ois.readObject();
    	
    	
		System.out.println("Request for create file: ");
		System.out.println(path.toString());
		metadata.print();
		
		
		return (new String("CREATE!")).getBytes();
    }

    private byte[] delete(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path path;

    	path = Paths.get((String)ois.readObject());
    	
    	
		System.out.println("Request for delete file: ");
		System.out.println(path.toString());
		
		return (new String("DELETE!")).getBytes();
    }

    private byte[] open(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path path;

    	path = Paths.get((String)ois.readObject());
    	
		System.out.println("Request for open file: ");
		System.out.println(path.toString());
		
		return (new String("OPEN!")).getBytes();
    }

    private byte[] append(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path path;

    	path = Paths.get((String)ois.readObject());
		System.out.println("Request for append file: ");
		System.out.println(path.toString());
		
		return (new String("APPEND!")).getBytes();
    }

    private byte[] openDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path path = null;

    	path = Paths.get((String)ois.readObject());
    	
		System.out.println("Request for open directory: ");
		System.out.println(path.toString());

		Directory dir = dt.openDirectory(path);
		int    result = -1;
		
		if(dir != null) {
			result = Result.SUCCESS;
		} else {
			System.out.println("open directory failured");
			if(path.getParent() != null)
				dir = dt.openDirectory(path.getParent());
			else
				dir = dt.getRoot();
			result = Result.DIRNOTEXISTS;
		}
		Message msg = new Message(result, dir.getPathStr(), Directory.toBytes(dir));
		
		return Message.toBytes(msg);
    }
    
    private byte[] criateDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path     path     = null;
    	Metadata metadata = null;

    	path     = Paths.get((String)ois.readObject());
    	metadata = (Metadata)ois.readObject();
    	
		System.out.println("Request for create directory: ");
		System.out.println(path.toString());
		
		int    result = dt.createDirectory(path, metadata);
		Directory dir = dt.getDirectory(path.getParent());
		Message   msg = new Message(result, Directory.toBytes(dir));
		
		if(result == Result.SUCCESS) {
			dt.print();
		} else {
			System.out.println("create directory failured");
		}
		
		return Message.toBytes(msg);
    }
    
    private byte[] deleteDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path path = null;

    	path = Paths.get((String)ois.readObject());
    	
		System.out.println("Request for open directory: ");
		System.out.println(path.toString());

		int    result = dt.deleteDirectory(path);
		Directory dir = dt.getDirectory(path.getParent());
		Message   msg = new Message(result, Directory.toBytes(dir));
		
		if(result == Result.SUCCESS) {
			dt.print();
		} else {
			System.out.println("delete directory failured");
		}
		
		return Message.toBytes(msg);
    }
    
    private byte[] renameDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path   path    = null;
    	String newName = null;

    	path    = Paths.get((String)ois.readObject());
    	newName = (String)ois.readObject();
    	
		System.out.println("Request for rename directory: ");
		System.out.println(path.toString());

		int    result = dt.renameDirectory(path, newName);
		Directory dir = dt.getDirectory(path.getParent());
		Message   msg = new Message(result, Directory.toBytes(dir));
		
		if(result == Result.SUCCESS) {
			dt.print();
		} else {
			System.out.println("rename directory failured");
		}
		
		return Message.toBytes(msg);
    }
    
    private byte[] closeDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path path = null;

    	path = Paths.get((String)ois.readObject());
    	
		System.out.println("Request for close directory: ");
		System.out.println(path.toString());

		int    result = dt.closeDirectory(path);
		Directory dir = dt.getDirectory(path.getParent());
		Message   msg = new Message(result, dir.getPathStr(), Directory.toBytes(dir));
		
		if(result == Result.SUCCESS) {
		} else {
			System.out.println("close directory failured");
		}
		
		return Message.toBytes(msg);
    }
    
    private byte[] join(ObjectInputStream ois) throws ClassNotFoundException, IOException {

    	
		System.out.println("Request for join: ");
		
		return (new String("JOIN!")).getBytes();
    }
}