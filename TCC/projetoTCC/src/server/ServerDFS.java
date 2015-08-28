package server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import files.Metadata;
import request.RequestType;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;

public class ServerDFS extends DefaultSingleRecoverable {

    public ServerDFS(int id){
         new ServiceReplica(id, this, this);
    }
    
    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        ByteArrayInputStream in = new ByteArrayInputStream(command);
		
        int    reqType		= -1;
		byte[] resultBytes	= null;

        System.out.println("\nexecuteOrdered");

		try {
			ObjectInputStream ois = new ObjectInputStream(in);
			reqType = ois.readInt();

			//System.out.println("Request number: " + reqType);
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
    	Metadata metadata;

    	metadata = (Metadata)ois.readObject();
		System.out.println("Request for create file: ");
		metadata.print();
		
		return (new String("CREATE!")).getBytes();
    }

    private byte[] delete(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	String filepath;
    	
    	filepath = (String)ois.readObject();
		System.out.println("Request for delete file: ");
		System.out.println(filepath);
		
		return (new String("DELETE!")).getBytes();
    }

    private byte[] open(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	String filepath;
    	
    	filepath = (String)ois.readObject();
		System.out.println("Request for open file: ");
		System.out.println(filepath);
		
		return (new String("OPEN!")).getBytes();
    }

    private byte[] append(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    	String filepath;
    	
    	filepath = (String)ois.readObject();
		System.out.println("Request for append file: ");
		System.out.println(filepath);
		
		return (new String("APPEND!")).getBytes();
    }

}