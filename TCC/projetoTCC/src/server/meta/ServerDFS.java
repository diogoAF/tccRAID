package server.meta;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import dt.DirectoryTree;
import dt.Metadata;
import dt.directory.DirEntries;
import dt.directory.Directory;
import dt.file.BlockList;
import dt.file.FileBlockInfo;
import dt.file.FileDFS;
import message.Message;
import request.RequestType;
import result.Result;
import server.ServerInfo;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;

public class ServerDFS extends DefaultSingleRecoverable {
	private DirectoryTree dt;
	private ServerList    list;

    public ServerDFS(int id){
         new ServiceReplica(id, this, this);
         dt   = new DirectoryTree();
         list = new ServerList(); 
    }
    
    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        ByteArrayInputStream in = new ByteArrayInputStream(command);
		
        int    reqType	   = -1;
		byte[] resultBytes = null;

        System.out.println("");
        System.out.println("executeOrdered");

		try {
			ObjectInputStream ois = new ObjectInputStream(in);
			reqType = ois.readInt();

			switch(reqType) {
				case RequestType.CREATEDIR:
					resultBytes = criateDir(ois);
					break;
	
				case RequestType.DELETEDIR:
					resultBytes = deleteDir(ois);
					break;
	
				case RequestType.RENAMEDIR:
					resultBytes = renameDir(ois);
					break;
	
				case RequestType.OPENDIR:
					resultBytes = openDir(ois);
					break;
	
				case RequestType.CLOSEDIR:
					resultBytes = closeDir(ois);
					break;

				case RequestType.UPDATEDIR:
					resultBytes = updateDir(ois);
					break;

				case RequestType.CREATE:
					resultBytes = create(ois);
					break;
					
				case RequestType.DELETE:
					resultBytes = delete(ois);
					break;

				case RequestType.RENAME:
					resultBytes = rename(ois);
					break;

				case RequestType.OPEN:
					resultBytes = open(ois);
					break;

				case RequestType.APPEND:
					resultBytes = append(ois);
					break;

				case RequestType.CLOSE:
					resultBytes = close(ois);
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

    private byte[] criateDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path     path     = null;
    	Metadata metadata = null;
    	long modTime;

    	path     = Paths.get((String)ois.readObject());
    	metadata = (Metadata)ois.readObject();
    	modTime  = ois.readLong();
    	
		System.out.println("Request for create directory: ");
		System.out.println(path.toString());
		
		int    result = dt.createDirectory(path, metadata, modTime);
		Directory dir = dt.getDirectory(path.getParent());
		Message   msg = new Message( result, DirEntries.toBytes(dir.getDirEntries()) );
		
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
    	
		System.out.println("Request for delete directory: ");
		System.out.println(path.toString());

		int    result = dt.deleteDirectory(path);
		Directory dir = dt.getDirectory(path.getParent());
		Message   msg = new Message( result, DirEntries.toBytes(dir.getDirEntries()) );
		
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
		Message   msg = new Message( result, DirEntries.toBytes(dir.getDirEntries()) );
		
		if(result == Result.SUCCESS) {
			dt.print();
		} else {
			System.out.println("rename directory failured");
		}
		
		return Message.toBytes(msg);
    }

    private byte[] openDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path path = null;
    	long accTime;

    	path    = Paths.get((String)ois.readObject());
    	accTime = ois.readLong();
    	
		System.out.println("Request for open directory: ");
		System.out.println(path.toString());

		Directory dir = dt.openDirectory(path, accTime);
		int    result = -1;
		
		if(dir != null) {
			result = Result.SUCCESS;
		} else {
			System.out.println("open directory failured");
			if(path.getParent() != null)
				dir = dt.openDirectory(path.getParent(), accTime);
			else
				dir = dt.getRoot();
			result = Result.DIRNOTEXISTS;
		}
		
		Message msg = new Message( result, DirEntries.toBytes(dir.getDirEntries()) );

		return Message.toBytes(msg);
    }
 
    private byte[] closeDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path path = null;

    	path = Paths.get((String)ois.readObject());
    	
		System.out.println("Request for close directory: ");
		System.out.println(path.toString());

		int    result = dt.closeDirectory(path);
		Directory dir = dt.getDirectory(path.getParent());
		Message   msg = new Message( result, DirEntries.toBytes(dir.getDirEntries()) );
		
		if(result == Result.SUCCESS) {
		} else {
			System.out.println("close directory failured");
		}
		
		return Message.toBytes(msg);
    }

    private byte[] updateDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path path = null;

    	path = Paths.get((String)ois.readObject());
    	
		System.out.println("Request for update directory: ");
		System.out.println(path.toString());

		int    result = dt.closeDirectory(path);
		Directory dir = dt.getDirectory(path.getParent());
		Message   msg = new Message( result, DirEntries.toBytes(dir.getDirEntries()) );
		
		if(result == Result.SUCCESS) {
		} else {
			System.out.println("update directory failured");
		}
		
		return Message.toBytes(msg);
    }
    
    private byte[] create(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path     path     = null;
    	Metadata metadata = null;
    	long modTime;

    	path     = Paths.get((String)ois.readObject());
    	metadata = (Metadata)ois.readObject();
    	modTime  = ois.readLong();
    	
		System.out.println("Request for create file: ");
		System.out.println(path.toString());
		
		int    result = dt.create(path, metadata, modTime);
		
		BlockList bList = BlockListTeste.teste();
		
		
		Message   msg = new Message( result, BlockList.toBytes(bList) );
		
		if(result == Result.SUCCESS) {
			dt.print();
		} else {
			System.out.println("create file failured");
			return Message.toBytes(msg);
		}
		
		
		
		
		return Message.toBytes(msg);
    }

    private byte[] delete(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path path = null;

    	path = Paths.get((String)ois.readObject());
    	
		System.out.println("Request for delete file: ");
		System.out.println(path.toString());

		int    result = dt.delete(path);
		Directory dir = dt.getDirectory(path.getParent());
		Message   msg = new Message( result, DirEntries.toBytes(dir.getDirEntries()) );
		
		if(result == Result.SUCCESS) {
			dt.print();
		} else {
			System.out.println("delete file failured");
		}
		
		return Message.toBytes(msg);
    }

    private byte[] rename(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path   path    = null;
    	String newName = null;

    	path    = Paths.get((String)ois.readObject());
    	newName = (String)ois.readObject();
    	
		System.out.println("Request for rename file: ");
		System.out.println(path.toString());

		int    result = dt.rename(path, newName);
		Directory dir = dt.getDirectory(path.getParent());
		Message   msg = new Message( result, DirEntries.toBytes(dir.getDirEntries()) );
		
		if(result == Result.SUCCESS) {
			dt.print();
		} else {
			System.out.println("rename file failured");
		}
		
		return Message.toBytes(msg);
    }

    private byte[] open(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path path = null;
    	long accTime;

    	path    = Paths.get((String)ois.readObject());
    	accTime = ois.readLong();
    	
		System.out.println("Request for open file: ");
		System.out.println(path.toString());

		FileDFS target = dt.open(path, accTime);
		int     result = -1;
		
		if(target != null) {
			result = Result.SUCCESS;
		} else {
			System.out.println("open file failured");
			result = Result.FILENOTEXISTS;
		}
		
		Message msg = new Message( result, BlockList.toBytes(target.getBlockList()) );

		return Message.toBytes(msg);
    }

    private byte[] append(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path path;

    	path = Paths.get((String)ois.readObject());
		System.out.println("Request for append file: ");
		System.out.println(path.toString());
		
		return (new String("APPEND!")).getBytes();
    }

    private byte[] close(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path path;

    	path = Paths.get((String)ois.readObject());
		System.out.println("Request for close file: ");
		System.out.println(path.toString());
		
		return (new String("CLOSE!")).getBytes();
    }
   
    
    private byte[] join(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	String hostName = (String)ois.readObject();
    	int    port     = ois.readInt();
    	long   capacity = ois.readLong();
    	
		System.out.println("Request for join: ");
		
		list.add(new ServerInfo(hostName, port, capacity));
		
		
		return (new String("JOIN!")).getBytes();
    }
}