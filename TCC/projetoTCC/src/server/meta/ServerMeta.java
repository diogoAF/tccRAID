package server.meta;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import request.RequestType;
import result.ResultType;
import server.ServerInfo;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;

public class ServerMeta extends DefaultSingleRecoverable {
    private boolean verbose;
    
    private int raidType;
    private int nServers;
    
	private DirectoryTree dt;
	private ServerList    list;
	
	public ServerMeta(int id){
         new ServiceReplica(id, this, this);
         dt   = new DirectoryTree();
         list = new ServerList(); 
    }

    public ServerMeta(int id, int raid, int n, boolean verbose){
        switch(raid) {
        case(RaidType.RAID0):
            // Mesma operacao para RAID1
        case(RaidType.RAID1):
            if(n<2) {
                if(verbose)
                    System.out.println("Number of servers should be at least 2");
                System.exit(-1);
            }
            break;
        case(RaidType.RAID5):
            if(n<4) {
                if(verbose)
                    System.out.println("Number of servers should be at least 4");
                System.exit(-1);
            }
            break;    
        default:
            System.out.println("Unknown RAID type "+raid);
            System.exit(-1);
            break;
        }
        
        this.raidType = raid; 
        this.nServers = n;
         
        this.dt   = new DirectoryTree();
        this.list = new ServerList();
        this.verbose = verbose;
        
        new ServiceReplica(id, this, this);
    }
        
    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        if(verbose){
            System.out.println();
            System.out.println("executeOrdered");
        }

        byte[] resultBytes = null;

		try {
	        ByteArrayInputStream in  = new ByteArrayInputStream(command);
			ObjectInputStream    ois = new ObjectInputStream(in);
			
			int reqType = ois.readInt();

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

				default:
                                        if(verbose)
                                            System.out.println("Unknown request number " + reqType);
					break;
			}
			
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
        return resultBytes;
    }

    @Override
    public byte[] executeUnordered(byte[] command, MessageContext msgCtx) {
        if(verbose){
            System.out.println();
            System.out.println("executeUnordered");
        }

        byte[] resultBytes = null;

        try {
            ByteArrayInputStream in  = new ByteArrayInputStream(command);
            ObjectInputStream    ois = new ObjectInputStream(in);
            
            int reqType = ois.readInt();

            switch(reqType) {
                case RequestType.OPENROOT:
                    resultBytes = openRoot();
                    break;

                case RequestType.CLOSEDIR:
                    resultBytes = closeDir(ois);
                    break;

                case RequestType.UPDATEDIR:
                    resultBytes = updateDir(ois);
                    break;

                case RequestType.FAILURE:
                    resultBytes = failure(ois);
                    break;

                case RequestType.JOIN:
                    resultBytes = join(ois);
                    break;

                default:
                    if(verbose)
                        System.out.println("Unknown request number " + reqType);
                    break;
            }
            
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
        return resultBytes;
    }

    private byte[] openRoot() throws IOException {
        if(verbose)
            System.out.println("Request for open root directory");
        
        Directory  currDir = dt.getRoot();
        int        result  = -1;

        DirEntries entries;
        if(currDir == null) {
            entries = null;
            result  = ResultType.FAILURE;
        } else {
            entries = currDir.getDirEntries();
            result  = ResultType.SUCCESS;
        }
        if(verbose){
            if(result == ResultType.SUCCESS) {
                    System.out.println("open root done");
            } else {
                    System.out.println("open root failed");
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);

        oos.writeInt(result);
        oos.writeObject(entries);
        oos.flush();
        
        return out.toByteArray();
    }
    
    private byte[] criateDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        String   currPath = (String)ois.readObject();
        String   tgtName  = (String)ois.readObject();
        Metadata metadata = (Metadata)ois.readObject();
        long     accTime  = ois.readLong();

        if(verbose){
            System.out.println("Request for create directory: ");
            System.out.println(currPath+"/"+tgtName);
        }
        
        Directory currDir = dt.openDirectory(currPath, accTime);
        int       result  = -1;

        if(currDir == null) {
            currDir = dt.getRoot();
            result  = ResultType.FAILURE;
        } else if(currDir.existDir(tgtName)) {
            result = ResultType.DIRALREADYEXISTS;
        } else {
            currDir.setDirectory(new Directory(tgtName, currDir, metadata), accTime);
            result = ResultType.SUCCESS;
        }
            if(verbose){
		if(result == ResultType.SUCCESS) {
                        dt.print();
		} else {
                        System.out.println("create directory failed");
		}
            }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(result);
        oos.writeObject(currDir.getDirEntries());
        oos.flush();
        
        return out.toByteArray();
    }
    
    private byte[] deleteDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        String   currPath = (String)ois.readObject();
        String   tgtName  = (String)ois.readObject();
        long     accTime  = ois.readLong();

        if(verbose){
            System.out.println("Request for delete directory: ");
            System.out.println(currPath+"/"+tgtName);
        }
        
        Directory currDir = dt.openDirectory(currPath, accTime);
        int       result  = -1;
        
        if(currDir == null) {
            currDir = dt.getRoot();
            result  = ResultType.FAILURE;
        } else if(!currDir.existDir(tgtName)) {
            result = ResultType.DIRNOTEXISTS;
        } else {
            Directory target = currDir.getDirectory(tgtName);
            
            if( ( target.isLoked() ) && 
                ( System.currentTimeMillis()-target.getLastAccTime() ) <= 30*1000) 
            {
                result = ResultType.DIRLOCKED;
            } else {
                currDir.removeDirectory(tgtName, accTime);
                result = ResultType.SUCCESS;
            }
        }
            if(verbose){
		if(result == ResultType.SUCCESS) {
                        dt.print();
		} else {
			System.out.println("delete directory failed");
		}
            }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(result);
        oos.writeObject(currDir.getDirEntries());
        oos.flush();
        
        return out.toByteArray();
    }
    
    private byte[] renameDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        String   currPath = (String)ois.readObject();
        String   tgtName  = (String)ois.readObject();
        String   newName  = (String)ois.readObject();
        long     accTime  = ois.readLong();

        if(verbose){
            System.out.println("Request for rename directory: ");
            System.out.println(currPath+"/"+tgtName+" to "+newName);
        }
        
        Directory currDir = dt.openDirectory(currPath, accTime);
        int       result  = -1;

        if(currDir == null) {
            currDir = dt.getRoot();
            result  = ResultType.FAILURE;
        } else if(!currDir.existDir(tgtName)) {
            result = ResultType.DIRNOTEXISTS;
        } else if(currDir.existDir(newName)) {
            result = ResultType.DIRALREADYEXISTS;
        } else {
            Directory target = currDir.getDirectory(tgtName);
            
            if( ( target.isLoked() ) &&
                ( System.currentTimeMillis()-target.getLastAccTime() ) <= 30*1000) 
            {
                    result = ResultType.DIRLOCKED;
            }
            currDir.renameDirectory(tgtName, newName, accTime);
            result = ResultType.SUCCESS;
        }
            if(verbose){
		if(result == ResultType.SUCCESS) {
                        dt.print();
		} else {
			System.out.println("rename directory failed");
		}
            }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(result);
        oos.writeObject(currDir.getDirEntries());
        oos.flush();
        
        return out.toByteArray();
    }
    
    private byte[] openDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        String   currPath = (String)ois.readObject();
        String   tgtName  = (String)ois.readObject();
        long     accTime  = ois.readLong();

        if(verbose){
            System.out.println("Request for open directory: ");
            System.out.println(currPath+"/"+tgtName);
        }

        Directory currDir = dt.openDirectory(currPath, accTime);
        int       result  = -1;
		
        if(currDir == null) {
            currDir = dt.getRoot();
            result  = ResultType.FAILURE;
        } else if(!currDir.existDir(tgtName)) {
            result = ResultType.DIRNOTEXISTS;
        } else {
            currDir = currDir.getDirectory(tgtName);
            currDir.lockR();
            result  = ResultType.SUCCESS;
        }
            if(verbose){
		if(result == ResultType.SUCCESS) {
                    System.out.println("open directory done");
		} else {
			System.out.println("open directory failed");
		}
            }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(result);
        oos.writeObject(currDir.getDirEntries());
        oos.flush();
        
        return out.toByteArray();
    }
 
    private byte[] closeDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        String currPath = (String)ois.readObject();
    	
        if(verbose){
            System.out.println("Request for close directory: ");
            System.out.println(currPath);
        }
		
		Directory currDir = dt.getDirectory(currPath);
        int       result  = -1;
		
        if(currDir == null) {
            currDir = dt.getRoot();
            result  = ResultType.FAILURE;
        } else {
            currDir.unlock();
            currDir = currDir.getParent();
            result  = ResultType.SUCCESS;
        }
        
        if(verbose){
		if(result == ResultType.SUCCESS) {
                    System.out.println("close directory done");
		} else {
			System.out.println("close directory failed");
		}
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(result);
        oos.writeObject(currDir.getDirEntries());
        oos.flush();
        
        return out.toByteArray();
    }

    private byte[] updateDir(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	String currPath = (String)ois.readObject();
        long   accTime  = ois.readLong();
    	
        if(verbose){
		System.out.println("Request for update directory: ");
		System.out.println(currPath);
        }

		Directory currDir = dt.openDirectory(currPath, accTime);
		int       result  = -1;
		
		if(currDir == null) {
            currDir = dt.getRoot();
		    result  = ResultType.FAILURE;
		} else {
		    result = ResultType.SUCCESS;
		}
		if(verbose){
                    if(result == ResultType.SUCCESS) {
                        System.out.println("update directory done");
                    } else {
                            System.out.println("update directory failed");
                    }
                }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(result);
        oos.writeObject(currDir.getDirEntries());
        oos.flush();
        
        return out.toByteArray();
    }

    private byte[] failure(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        int       opType = ois.readInt();
        BlockInfo info   = (BlockInfo)ois.readObject();
        
        if(verbose)
            System.out.println("Reporting data server error");
        
            
        String hostName = info.getHostName();
        int    port     = info.getPort();
        
        list.remove(hostName, port);
        
        if(verbose)
            list.print();
        
        if(opType == RequestType.CREATE) {
            String currPath = (String)ois.readObject();
            String tgtName  = (String)ois.readObject();

            Directory currDir = dt.getDirectory(currPath);
            
            currDir.removeFile(tgtName);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeObject(dt.getRoot().getDirEntries());
        oos.flush();

        return out.toByteArray();
    }
    
    private byte[] create(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        String   currPath = (String)ois.readObject();
    	String   tgtName  = (String)ois.readObject();
    	Metadata metadata = (Metadata)ois.readObject();
    	long     accTime  = ois.readLong();
    	
        if(verbose){
		System.out.println("Request for create file: ");
		System.out.println(currPath+"/"+tgtName);
        }

        Directory currDir = dt.openDirectory(currPath, accTime);
		int       result      = -1;
		long      blockSize   = 0;
		
		BlockInfoList bList = null;
		
		switch(raidType) {
        case(RaidType.RAID0):
            blockSize = (long) Math.ceil((double)metadata.size()/(double)(nServers));
            break;
        case(RaidType.RAID1):
            blockSize = metadata.size();
            break;
        case(RaidType.RAID5):
            blockSize = (long) Math.ceil((double)metadata.size()/3.0D);
            break;
        default:
            System.out.println("Unknown RAID type");
            System.exit(-1);
            break;
        }
		
		try{
			if(currDir == null) {
			    currDir = dt.getRoot();
			    result  = ResultType.FAILURE;
			} else if(currDir.existFile(tgtName)) {
			    result = ResultType.FILEALREADYEXISTS;
			} else {
			    list.nexts(nServers);
			    
	            bList = new BlockInfoList(blockSize, raidType, nServers);
	            for(int i=0; i<nServers; i++) {
	                ServerInfo info = list.get(i);
	                
	                info.addSize(blockSize);
	                bList.add(new BlockInfo(info.getHostName(), info.getPort(), info.getID()));
	            }
			    
			    currDir.setFile(new FileDFS(tgtName, currDir, metadata, bList), accTime);
                result = ResultType.SUCCESS;
			}
		} catch(IndexOutOfBoundsException e) {
                        if(verbose)
                            System.out.println("number of data servers is not enough: "+nServers+" servers");
			result = ResultType.SERVERFAULT;
		}
		
                if(verbose){
                    if(result == ResultType.SUCCESS) {
                            dt.print();
                            list.print();
                    } else {
                            System.out.println("create file failed");
                    }
                }
		
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(result);
        oos.writeObject(currDir.getDirEntries());
        oos.writeObject(bList);
        oos.flush();
        
        return out.toByteArray();
    }

    private byte[] delete(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        String   currPath = (String)ois.readObject();
        String   tgtName  = (String)ois.readObject();
        long     accTime  = ois.readLong();
    	
        if(verbose){
            System.out.println("Request for delete file: ");
            System.out.println(currPath+"/"+tgtName);
        }

		Directory currDir = dt.openDirectory(currPath, accTime);
		int       result  = -1;
		BlockInfoList bList   = null;
		
		if(currDir == null) {
            currDir = dt.getRoot();
            result  = ResultType.FAILURE;
        } else if(!currDir.existFile(tgtName)) {
            result = ResultType.FILENOTEXISTS;
        } else {
            FileDFS target = currDir.getFile(tgtName);
                
            if( ( target.isLoked() ) &&
                ( System.currentTimeMillis()-target.getLastAccTime() ) <= 30*1000) 
            {
                result = ResultType.FILELOCKED;
            } else {   
                currDir.removeFile(tgtName, accTime);
                bList = target.getBlockList();
                
                int  n    = bList.getNServers();
                long size = bList.getBlockSize();
                for(int i=0;i<n;i++) {
                    BlockInfo  bInfo = bList.get(i);
                    ServerInfo sInfo = list.get(bInfo.getHostName(), bInfo.getPort());
                    
                    sInfo.subSize(size);
                }
                
                result = ResultType.SUCCESS;
            }
        }
	    if(verbose){
		if(result == ResultType.SUCCESS) {
                    dt.print();
                    list.print();
		} else {
			System.out.println("delete file failed");
		}
            }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(result);
        oos.writeObject(currDir.getDirEntries());
        oos.writeObject(bList);
        oos.flush();
        
        return out.toByteArray();
    }

    private byte[] rename(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        String   currPath = (String)ois.readObject();
        String   tgtName  = (String)ois.readObject();
        String   newName  = (String)ois.readObject();
        long     accTime  = ois.readLong();
        
        if(verbose){
            System.out.println("Request for rename file: ");
            System.out.println(currPath+"/"+tgtName+" to " + newName);
        }

        Directory currDir = dt.openDirectory(currPath, accTime);
        int       result  = -1;
        
        if(currDir == null) {
            currDir = dt.getRoot();
            result  = ResultType.FAILURE;
        } else if(!currDir.existFile(tgtName)) {
            result = ResultType.FILENOTEXISTS;
        } else if(currDir.existFile(newName)) {
            result = ResultType.FILEALREADYEXISTS;
        } else {
            FileDFS   target = currDir.getFile(tgtName);
            
            if( ( target.isLoked() ) &&
                ( System.currentTimeMillis()-target.getLastAccTime() ) <= 30*1000) 
            {
                result = ResultType.FILELOCKED;
            } else {
                currDir.renameFile(tgtName, newName, accTime);
                result = ResultType.SUCCESS;
            }
        }
		if(verbose){
                    if(result == ResultType.SUCCESS) {
                            dt.print();
                    } else {
                            System.out.println("rename file failed");
                    }
                }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(result);
        oos.writeObject(currDir.getDirEntries());
        oos.flush();
        
        return out.toByteArray();
    }

    private byte[] open(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        String   currPath = (String)ois.readObject();
        String   tgtName  = (String)ois.readObject();
        long     accTime  = ois.readLong();
        
        if(verbose){
            System.out.println("Request for open(read) file: ");
            System.out.println(currPath+"/"+tgtName);
        }
        
        Directory currDir   = dt.openDirectory(currPath, accTime);
        int       result    = -1;
        long      fileSize  = 0;
        FileDFS   target    = null;
        BlockInfoList bList = null;
		
        if(currDir == null) {
            currDir = dt.getRoot();
            result  = ResultType.FAILURE;
        } else if(!currDir.existFile(tgtName)) {
            result = ResultType.FILENOTEXISTS;
        } else {
            target = currDir.getFile(tgtName);
            if( ( target.isLokedW() ) &&
                ( System.currentTimeMillis()-target.getLastAccTime() ) <= 30*1000 ) 
            {
                result = ResultType.FILELOCKED;
            } else {
                target.lockR();
                bList  = target.getBlockList();
                fileSize = target.getMetadata().size();
                result = ResultType.SUCCESS;
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(result);
        oos.writeObject(currDir.getDirEntries());
        oos.writeObject(bList);
        oos.writeLong(fileSize);
        oos.flush();
        
        return out.toByteArray();
    }

    private byte[] append(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        String   currPath = (String)ois.readObject();
        String   tgtName  = (String)ois.readObject();
        long     accTime  = ois.readLong();
        
        if(verbose){
            System.out.println("Request for open(write) file: ");
            System.out.println(currPath+"/"+tgtName);
        }
        
        Directory currDir = dt.openDirectory(currPath, accTime);
        int       result  = -1;
        FileDFS   target  = null;
        BlockInfoList bList   = null;
        
        if(currDir == null) {
            currDir = dt.getRoot();
            result  = ResultType.FAILURE;
        } else if(!currDir.existFile(tgtName)) {
            result = ResultType.FILENOTEXISTS;
        } else {
            target = currDir.getFile(tgtName);
            if( ( target.isLoked() ) &&
                ( System.currentTimeMillis()-target.getLastAccTime() ) <= 30*1000 ) 
            {
                result = ResultType.FILELOCKED;
            } else {
                target.lockW();
                bList = target.getBlockList();
                result = ResultType.SUCCESS;
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(result);
        oos.writeObject(currDir.getDirEntries());
        oos.writeObject(bList);
        oos.flush();
        
        return out.toByteArray();
    }

    @SuppressWarnings("unused")
    private byte[] close(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        String tgtPath = (String)ois.readObject();
        String tgtName = (String)ois.readObject();
        long   accTime = ois.readLong();
        
        if(verbose){
            System.out.println("Request for close file: ");
            System.out.println(tgtPath+"/"+tgtName);
        }
        
        Directory currDir = dt.getDirectory(tgtPath);
        int       result  = -1;
        
        if(currDir == null) {
            currDir = dt.getRoot();
            result  = ResultType.FAILURE;
        } else if(!currDir.existFile(tgtName)) {
            result = ResultType.FILENOTEXISTS;
        } else {
            FileDFS target = currDir.getFile(tgtName);
            target.unlock();
            result = ResultType.SUCCESS;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(result);
        oos.writeObject(currDir.getDirEntries());
        oos.flush();
        
        return out.toByteArray();
    }
   
    private byte[] updateAccess(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        Path     currPath = Paths.get((String)ois.readObject());
        LockList lockList = (LockList)ois.readObject();
        long     accTime  = ois.readLong();

        if(verbose){
            System.out.println("Request for update access");
            System.out.println(currPath.toString());
            lockList.print();
        }

        int       result  = -1;
        Directory currDir = dt.getDirectory(currPath);
        
        
        try {
            while(!currDir.getName().equals("root")) {
                currDir.getMetadata().setLastAccessTime(accTime);
                currDir = currDir.getParent();
            }
            
            int max =lockList.size();
            for(int i = 0; i<max; i++) {
                FileDFS file = dt.getFile(lockList.get(i));
                file.getMetadata().setLastAccessTime(accTime);
            }
            
            result = ResultType.SUCCESS;
        } catch(NullPointerException e) {
            result = ResultType.FAILURE;
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(result);
        oos.writeObject(dt.getRoot().getDirEntries());
        oos.flush();
        
        return out.toByteArray();
    }
    
    private byte[] join(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	String hostName = (String)ois.readObject();
    	int    port     = ois.readInt();
    	long   capacity = ois.readLong();
        long   accTime  = ois.readLong();
    	
        if(verbose)
            System.out.println("Request for join: ");

        int result = ResultType.SUCCESS;
        
		list.add(new ServerInfo(hostName, port, capacity, accTime));
                if(verbose)
                    list.print();

        
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream    oos = new ObjectOutputStream(out);
        
        oos.writeInt(result);
        oos.flush();
        
        return out.toByteArray();
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

}