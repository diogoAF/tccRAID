package server.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import bftsmart.tom.ServiceProxy;
import request.RequestType;
import result.ResultType;

public class MetadataModule extends Thread {
    private ServiceProxy proxy;
    private String       hostName;
    private int          port;
    
    public MetadataModule(int id) {
        proxy    = new ServiceProxy(id);
    }
    
    public MetadataModule(int id, String hostName, int port, long capacity) {
    	proxy = new ServiceProxy(id);
    	try {
    	    this.hostName = hostName;
    	    this.port     = port;
    	    
			join(hostName, port, capacity);
		} catch (IOException e) {
			System.out.println("can't connect to metadata server");
			System.exit(-1);
		}
    }
	
	private void join(String hostName, int port, long capacity) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.JOIN);
		oos.writeObject(hostName);
		oos.writeInt(port);
		oos.writeLong(capacity);
        oos.writeLong(System.currentTimeMillis());
		oos.flush();
		
		byte[] bytes = this.proxy.invokeUnordered(out.toByteArray());

        ByteArrayInputStream in  = new ByteArrayInputStream(bytes);
        ObjectInputStream    ois = new ObjectInputStream(in);
        
        int result = ois.readInt();
        
        if(result != ResultType. SUCCESS) {
            throw new IOException();
        }
	}
	
    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

}
