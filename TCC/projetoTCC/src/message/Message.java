package message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Message implements Serializable{
	private int               result;
	private ArrayList<byte[]> bytes;
	
	public Message(int result) {
		this.result = result;
		this.bytes  = new ArrayList<byte[]>();
	}
	
	public Message(int result, byte[] bytes) {
		this(result);
		this.bytes.add(bytes);
	}
	
	public int getResult() {
		return result;
	}
	public byte[] getBytes() {
		return bytes.get(0);
	}
	
	public byte[] getBytes(int index) {
		return bytes.get(index);
	}

	public void addBytes(byte[] bytes) {
		this.bytes.add(bytes);
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
	
}
