package message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Message implements Serializable{
	private int    result;
	private String path;
	private byte[] bytes;
	
	public Message(int result, byte[] bytes) {
		this.result = result;
		this.path   = null;
		this.bytes  = bytes;
	}
	
	public Message(int result, String path, byte[] bytes) {
		this(result, bytes);
		
		this.path = path;
	}
	
	public static byte[] toBytes(Message msg) {
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ObjectOutputStream    oos = new ObjectOutputStream(bos);
	        
	        oos.writeObject(msg);
	        byte[] bytes = bos.toByteArray(); 
	        oos.close();
	        bos.close();
	        return bytes;
		} catch(IOException e) {
			return null;	
		}
	}
	
	public static Message toMessage(byte[] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream    ois = new ObjectInputStream(bis);
			
			Message msg = (Message) ois.readObject();
			return msg;
		} catch (ClassNotFoundException | IOException e) {
			return null;
		}
	}
	
	public int getResult() {
		return result;
	}

	public String getPath() {
		return path;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
}
