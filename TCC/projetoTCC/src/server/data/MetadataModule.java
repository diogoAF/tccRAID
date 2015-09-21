package server.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import bftsmart.tom.ServiceProxy;
import message.Message;
import request.RequestType;

public class MetadataModule {
    private ServiceProxy proxy;
    
    public MetadataModule(int id) {
        proxy    = new ServiceProxy(id);
    }
    
	
	public int join(String hostName, int port, long capacity) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream    oos = new ObjectOutputStream(out);
		
		oos.writeInt(RequestType.JOIN);
		oos.writeObject(hostName);
		oos.writeInt(port);
		oos.writeLong(capacity);
		oos.flush();
		
		byte[] bytes = this.proxy.invokeOrdered(out.toByteArray());
		Message  msg = Message.toMessage(bytes);
		int   result = msg.getResult();

		
		return result;
	}
	
}
