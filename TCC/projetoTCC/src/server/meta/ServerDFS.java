package server.meta;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import dt.DirectoryTree;
import dt.LockList;
import dt.Metadata;
import dt.directory.DirEntries;
import dt.directory.Directory;
import dt.file.BlockInfoList;
import dt.file.BlockInfo;
import dt.file.FileDFS;
import message.Message;
import message.Result;
import request.RequestType;
import server.ServerInfo;
import server.meta.manager.LockManager;
import server.meta.manager.ServerManager;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;

public class ServerDFS extends DefaultSingleRecoverable {
	private DirectoryTree dt;
	private ServerList    list;

	private LockManager   lockMan;
	private ServerManager servMan;

	public ServerDFS(int id){
         new ServiceReplica(id, this, this);
         dt   = new DirectoryTree();
         list = new ServerList(); 
         
         lockMan = new LockManager(dt);
         servMan = new ServerManager(list);
         
         //lockMan.run();
         //servMan.start();
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

                case RequestType.UPDATEACC:
                    resultBytes = updateAccess(ois);
                    break;

                case RequestType.OPENROOT:
                    resultBytes = openRoot();
                    break;

                case RequestType.FAILURE:
                    resultBytes = failure(ois);
                    break;

				case RequestType.JOIN:
					resultBytes = join(ois);
					break;

                case RequestType.KEEPALIVE:
                    resultBytes = keepAlive(ois);
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
        Path     currPath = Paths.get((String)ois.readObject());
        String   tgtName  = (String)ois.readObject();
        Metadata metadata = (Metadata)ois.readObject();
        long     accTime  = ois.readLong();

        System.out.println("Request for create directory: ");
        System.out.println(currPath.toString()+"/"+tgtName);
        
        Directory currDir = dt.openDirectory(currPath, accTime);
        int       result  = -1;
		
        if(currDir == null) {
            result = Result.FAILURE;
        } else if(currDir.existDir(tgtName)) {
            result = Result.DIRALREADYEXISTS;
        } else {
            currDir.setDirectory(new Directory(tgtName, currDir, metadata), accTime);
            result = Result.SUCCESS;
        }
      
		Message   msg = new Message( result, DirEntries.toBytes(currDir.getDirEntries()) );
		
		if(result == Result.SUCCESS) {
			dt.print();
		} else {
			System.out.println("create directory failured");
		}
		
		return Message.toBytes(msg);
    }
    
    private byte[] deleteDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        Path     currPath = Paths.get((String)ois.readObject());
        String   tgtName  = (String)ois.readObject();
        long     accTime  = ois.readLong();

        System.out.println("Request for delete directory: ");
        System.out.println(currPath.toString()+"/"+tgtName);
        
        Directory currDir = dt.openDirectory(currPath, accTime);
        int       result  = -1;
        
        if(currDir == null) {
            result = Result.FAILURE;
        } else if(!currDir.existDir(tgtName)) {
            result = Result.DIRNOTEXISTS;
        } else {
            Directory target = currDir.getDirectory(tgtName);
            
            if( ( target.isLoked() ) &&
                ( System.currentTimeMillis()-target.getLastAccTime() ) <= 30*1000) 
            {
                    result = Result.DIRLOCKED;
            } else {
                currDir.removeDirectory(tgtName, accTime);
                result = Result.SUCCESS;
            }
        }
      
		Message   msg = new Message( result, DirEntries.toBytes(currDir.getDirEntries()) );
		
		if(result == Result.SUCCESS) {
			dt.print();
		} else {
			System.out.println("delete directory failured");
		}
		
		return Message.toBytes(msg);
    }
    
    private byte[] renameDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        Path     currPath = Paths.get((String)ois.readObject());
        String   tgtName  = (String)ois.readObject();
        String   newName  = (String)ois.readObject();
        long     accTime  = ois.readLong();

        System.out.println("Request for rename directory: ");
        System.out.println(currPath.toString()+"/"+tgtName+" to "+newName);
        
        Directory currDir = dt.openDirectory(currPath, accTime);
        int       result  = -1;

        if(currDir == null) {
            result = Result.FAILURE;
        } else if(!currDir.existDir(tgtName)) {
            result = Result.DIRNOTEXISTS;
        } else if(currDir.existDir(newName)) {
            result = Result.DIRALREADYEXISTS;
        } else {
            Directory target = currDir.getDirectory(tgtName);
            
            if( ( target.isLoked() ) &&
                ( System.currentTimeMillis()-target.getLastAccTime() ) <= 30*1000) 
            {
                    result = Result.DIRLOCKED;
            }
            currDir.renameDirectory(tgtName, newName, accTime);
            result = Result.SUCCESS;
        }

		Message   msg = new Message( result, DirEntries.toBytes(currDir.getDirEntries()) );
		
		if(result == Result.SUCCESS) {
			dt.print();
		} else {
			System.out.println("rename directory failured");
		}
		
		return Message.toBytes(msg);
    }
    private byte[] openDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        Path     currPath = Paths.get((String)ois.readObject());
        String   tgtName  = (String)ois.readObject();
        long     accTime  = ois.readLong();

		System.out.println("Request for open directory: ");
        System.out.println(currPath.toString()+"/"+tgtName);

        Directory currDir = dt.openDirectory(currPath, accTime);
        int       result  = -1;
		
        if(currDir == null) {
            result = Result.FAILURE;
        } else if(!currDir.existDir(tgtName)) {
            result = Result.DIRNOTEXISTS;
        } else {
            currDir = currDir.getDirectory(tgtName);
            currDir.lockR();
            result  = Result.SUCCESS;
        }
        
		if(result == Result.SUCCESS) {
		} else {
			System.out.println("open directory failured");
		}
		
		Message msg = new Message( result, DirEntries.toBytes(currDir.getDirEntries()) );

		return Message.toBytes(msg);
    }
 
    private byte[] closeDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path currPath = Paths.get((String)ois.readObject());
    	
		System.out.println("Request for close directory: ");
		System.out.println(currPath.toString());
		
		Directory currDir = dt.getDirectory(currPath);
        int       result  = -1;
		
        if(currDir == null) {
            result = Result.FAILURE;
        } else {
            currDir.unlock();
            currDir = currDir.getParent();
            result  = Result.SUCCESS;
        }
        
		Message   msg = new Message( result, DirEntries.toBytes(currDir.getDirEntries()) );
		
		if(result == Result.SUCCESS) {
		} else {
			System.out.println("close directory failured");
		}
		
		return Message.toBytes(msg);
    }

    private byte[] updateDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path currPath = Paths.get((String)ois.readObject());
        long accTime  = ois.readLong();
    	
		System.out.println("Request for update directory: ");
		System.out.println(currPath.toString());

		Directory currDir = dt.openDirectory(currPath, accTime);
		int       result  = -1;
		
		if(currDir == null) {
		    result = Result.FAILURE;
		} else {
		    result = Result.SUCCESS;
		}
		
		if(result == Result.SUCCESS) {
		} else {
			System.out.println("update directory failured");
		}
        
		Message   msg = new Message( result, DirEntries.toBytes(currDir.getDirEntries()) );
		
		return Message.toBytes(msg);
    }

    private byte[] openRoot() {

        System.out.println("Request for open root directory");
        
        Directory currDir = dt.getRoot();
        int       result  = -1;
        
        if(currDir == null) {
            result = Result.FAILURE;
        } else {
            result = Result.SUCCESS;
        }
        
        if(result == Result.SUCCESS) {
        } else {
            System.out.println("open root failured");
        }
        
        Message msg = new Message( result, DirEntries.toBytes(currDir.getDirEntries()) );

        return Message.toBytes(msg);
    }
    
    private byte[] failure(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        int       opType = ois.readInt();
        BlockInfo info  = (BlockInfo)ois.readObject();
        
        System.out.println("Reporting data server error");
        
            
        String hostName = info.getHostName();
        int    port     = info.getPort();
        
        list.remove(hostName, port);
    
        list.print();
        
        if(opType == RequestType.CREATE) {
            String currPath = (String)ois.readObject();
            String tgtName  = (String)ois.readObject();

            Directory currDir = dt.getDirectory(currPath);
            
            currDir.removeFile(tgtName);
        }
        
        return null;
    }
    
    private byte[] create(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path     currPath = Paths.get((String)ois.readObject());
    	String   tgtName  = (String)ois.readObject();
    	Metadata metadata = (Metadata)ois.readObject();
    	long     accTime  = ois.readLong();
    	
		System.out.println("Request for create file: ");
		System.out.println(currPath.toString()+"/"+tgtName);

        Directory currDir = dt.openDirectory(currPath, accTime);
		int       result  = -1;
		long      bSize   = (long) Math.ceil((double)metadata.size()/3.0D);
		BlockInfoList bList   = null;
		
		try{
			if(currDir == null) {
			    result = Result.FAILURE;
			} else if(currDir.existFile(tgtName)) {
			    result = Result.FILEALREADYEXISTS;
			} else {
			    list.nexts();
			    
	            bList = new BlockInfoList(bSize);
	            for(int i=0; i<4; i++) {
	                ServerInfo info = list.get(i);
	                
	                info.addSize(bSize);
	                bList.add(new BlockInfo(info.getHostName(), info.getPort(), info.getID()));
	            }
			    
			    currDir.setFile(new FileDFS(tgtName, currDir, metadata, bList), accTime);
                result = Result.SUCCESS;
			}
		} catch(IndexOutOfBoundsException e) {
			System.out.println("number of data servers is less than 4");
			result = Result.SERVERFAULT;
		}
		
		Message   msg = new Message( result, BlockInfoList.toBytes(bList) );
		
		if(result == Result.SUCCESS) {
			dt.print();
		} else {
			System.out.println("create file failured");
			return Message.toBytes(msg);
		}
		
		return Message.toBytes(msg);
    }

    private byte[] delete(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        Path     currPath = Paths.get((String)ois.readObject());
        String   tgtName  = (String)ois.readObject();
        long     accTime  = ois.readLong();
    	
		System.out.println("Request for delete file: ");
        System.out.println(currPath.toString()+"/"+tgtName);

		Directory currDir = dt.openDirectory(currPath, accTime);
		int       result  = -1;
		BlockInfoList bList   = null;
		
		if(currDir == null) {
            result = Result.FAILURE;
        } else if(!currDir.existFile(tgtName)) {
            result = Result.FILENOTEXISTS;
        } else {
            FileDFS target = currDir.getFile(tgtName);
                
            if( ( target.isLoked() ) &&
                ( System.currentTimeMillis()-target.getLastAccTime() ) <= 30*1000) 
            {
                result = Result.FILELOCKED;
            } else {   
                currDir.removeFile(tgtName, accTime);
                bList = target.getBlockList();
                result = Result.SUCCESS;
                
            }
        }
		
		
		Message   msg = new Message( result, BlockInfoList.toBytes(bList) );
		
		if(result == Result.SUCCESS) {
			dt.print();
		} else {
			System.out.println("delete file failured");
		}
		
		return Message.toBytes(msg);
    }

    private byte[] rename(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        Path     currPath = Paths.get((String)ois.readObject());
        String   tgtName  = (String)ois.readObject();
        String   newName  = (String)ois.readObject();
        long     accTime  = ois.readLong();
        
		System.out.println("Request for rename file: ");
        System.out.println(currPath.toString()+"/"+tgtName+" to " + newName);

        Directory currDir = dt.openDirectory(currPath, accTime);
        int       result  = -1;
        
        if(currDir == null) {
            result = Result.FAILURE;
        } else if(!currDir.existFile(tgtName)) {
            result = Result.FILENOTEXISTS;
        } else if(currDir.existFile(newName)) {
            result = Result.FILEALREADYEXISTS;
        } else {
            FileDFS   target = currDir.getFile(tgtName);
            
            if( ( target.isLoked() ) &&
                ( System.currentTimeMillis()-target.getLastAccTime() ) <= 30*1000) 
            {
                result = Result.FILELOCKED;
            } else {
                currDir.renameFile(tgtName, newName, accTime);
                result = Result.SUCCESS;
            }
        }
        
		Message msg = new Message( result, DirEntries.toBytes(currDir.getDirEntries()) );
		
		if(result == Result.SUCCESS) {
			dt.print();
		} else {
			System.out.println("rename file failured");
		}
		
		return Message.toBytes(msg);
    }

    private byte[] open(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        Path     currPath = Paths.get((String)ois.readObject());
        String   tgtName  = (String)ois.readObject();
        long     accTime  = ois.readLong();
        
        System.out.println("Request for open(read) file: ");
        System.out.println(currPath.toString()+"/"+tgtName);
        
        Directory currDir = dt.openDirectory(currPath, accTime);
        int       result  = -1;
        FileDFS   target  = null;
        BlockInfoList bList   = null;
		
        if(currDir == null) {
            result = Result.FAILURE;
        } else if(!currDir.existFile(tgtName)) {
            result = Result.FILENOTEXISTS;
        } else {
            target = currDir.getFile(tgtName);
            if(target.isLokedW()) {
                result = Result.FILELOCKED;
            } else {
                target.lockR();
                bList = target.getBlockList();
                result = Result.SUCCESS;
                
            }
        }
		
		Message msg = new Message( result, BlockInfoList.toBytes(bList) );

		return Message.toBytes(msg);
    }

    private byte[] append(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        Path     currPath = Paths.get((String)ois.readObject());
        String   tgtName  = (String)ois.readObject();
        long     accTime  = ois.readLong();
        
        System.out.println("Request for open(write) file: ");
        System.out.println(currPath.toString()+"/"+tgtName);
        
        Directory currDir = dt.openDirectory(currPath, accTime);
        int       result  = -1;
        FileDFS   target  = null;
        BlockInfoList bList   = null;
        
        if(currDir == null) {
            result = Result.FAILURE;
        } else if(!currDir.existFile(tgtName)) {
            result = Result.FILENOTEXISTS;
        } else {
            target = currDir.getFile(tgtName);
            if(target.isLoked()) {
                result = Result.FILELOCKED;
            } else {
                target.lockW();
                bList = target.getBlockList();
                result = Result.SUCCESS;
            }
        }
        
        Message msg = new Message( result, BlockInfoList.toBytes(bList) );

        return Message.toBytes(msg);
    }

    private byte[] close(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	Path path;

    	path = Paths.get((String)ois.readObject());
		System.out.println("Request for close file: ");
		System.out.println(path.toString());
		
		return (new String("CLOSE!")).getBytes();
    }
   
    private byte[] updateAccess(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        Path     currPath = Paths.get((String)ois.readObject());
        LockList lockList = (LockList)ois.readObject();
        long     accTime  = ois.readLong();

        System.out.println("Request for update access");
        System.out.println(currPath);
        lockList.print();

        int       result  = -1;
        Directory target  = dt.getDirectory(currPath);
        Message   message = null;
        
        try {
            while(!target.getName().equals("root")) {
                target.getMetadata().setLastAccessTime(accTime);
                target = target.getParent();
            }
            
            int max =lockList.size();
            for(int i = 0; i<max; i++) {
                FileDFS file = dt.getFile(Paths.get( lockList.get(i)));
                file.getMetadata().setLastAccessTime(accTime);
            }
            
            result = Result.SUCCESS;
            message = new Message(result);
        } catch(NullPointerException e) {
            result = Result.FAILURE;
            message = new Message(result, DirEntries.toBytes(dt.getRoot().getDirEntries()));
        }

        return Message.toBytes(message);
    }
    
    private byte[] join(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	String hostName = (String)ois.readObject();
    	int    port     = ois.readInt();
    	long   capacity = ois.readLong();
        long   accTime  = ois.readLong();
    	
		System.out.println("Request for join: ");
		
		list.add(new ServerInfo(hostName, port, capacity, accTime));
		list.print();
		
		Message msg = new Message(Result.SUCCESS);
		
		return Message.toBytes(msg);
    }
    
    private byte[] keepAlive(ObjectInputStream ois) throws ClassNotFoundException, IOException  {
        String hostName = (String)ois.readObject();
        int    port     = ois.readInt();
        long   accTime  = ois.readLong();
        
        System.out.println("Request for keep alive: ");
        System.out.println(hostName+":"+port);
        
        ServerInfo info = list.get(hostName, port);
        info.setLastAccTime(accTime);
        
        return null;
    }
    
    public void run() {
    	System.out.println("running!");
    	while(true);
    }
}