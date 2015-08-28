package server.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import request.RequestType;
import bftsmart.tom.ServiceProxy;

public class ServerData {
    private ServiceProxy proxy;

    public ServerData(int id) {
        proxy = new ServiceProxy(id);
    }
    
    public byte[] join() throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	byte[] bytes = null;
    	
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeInt(RequestType.JOIN);
		oos.flush();
		bytes = this.proxy.invokeOrdered(out.toByteArray());
			
    	return bytes;
    }
    
    
    public byte[] executeOrdered(byte[] request){
        return this.proxy.invokeOrdered(request);
    }    
    
    public byte[] executeUnordered(byte[] request){
        return this.proxy.invokeUnordered(request);
    }
    
}
