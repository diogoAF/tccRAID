package server.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import bftsmart.tom.ServiceProxy;
import message.Message;
import request.RequestType;

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
    	start();
    }
	
	public int join(String hostName, int port, long capacity) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.JOIN);
		oos.writeObject(hostName);
		oos.writeInt(port);
		oos.writeLong(capacity);
        oos.writeLong(System.currentTimeMillis());
		oos.flush();
		
		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();

		
		return result;
	}
	
	public void run() {
		System.out.println("running!");
        
		while(true){
	        try {
                Thread.sleep(30*1000);
                
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            ObjectOutputStream oos;
	            oos = new ObjectOutputStream(out);
	            oos.writeInt(RequestType.KEEPALIVE);
	            oos.writeObject(hostName);
	            oos.writeInt(port);
                oos.writeLong(System.currentTimeMillis());
	            oos.flush();
	            
	            byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
	            
	        } catch (IOException | InterruptedException e) {
	            e.printStackTrace();
	            System.exit(0);
	        }
		}
	}
	
}
