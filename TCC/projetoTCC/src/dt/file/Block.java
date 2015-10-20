package dt.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Block implements Serializable {
    public byte[] bytes;
    private long ID;
    private String fileName;
    
    public Block(long ID, byte[] bytes){
        this.ID    = ID;
        this.bytes = bytes;
    }
    
    public Block(long bytesLength, long ID, String fileName){
        this.bytes = new byte[(int)bytesLength];
        this.ID = ID;
        this.fileName = fileName;
    }
    
    public Block(long bytesLength, long ID){
        this.bytes = new byte[(int)bytesLength];
        this.ID = ID;
    }
    
    public byte[] getBytes() {
        return bytes;
    }
    
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public void setBytes(byte[] bytes,int start_offset, int end_offset) {
        int j = 0;
        for(int i = start_offset; i < end_offset; i++){
            this.bytes[j] = bytes[i];
            j++;
        }
    }

    public long getID() {
        return ID;
    }

    public String getFileName() {
        return fileName;
    }
    
    public static byte[] toBytes(Block block) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream    oos =  null;
        byte[] bytes;
        try{
                    oos = new ObjectOutputStream(bos);
            
                    oos.writeObject(block);
                    bytes = bos.toByteArray(); 
                    oos.close();
                    bos.close();
                    return bytes;
        } catch(IOException e) {
                    throw new IOException("Erro ao converter a classe Block em bytes");
        }
    }
    
    public static Block toBlock(byte[] bytes) throws IOException {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream    ois = new ObjectInputStream(bis);
            
            Block block = (Block)ois.readObject();
            return block;
        } catch (ClassNotFoundException | IOException e) {
                    //System.out.println("Errou!");
                    //return null;
                    //throw new IOException("Nao foi possível converter os bytes informados na classe Block ");
                    e.printStackTrace();
                    System.exit(0);
                    
        }
                return null;
    }

}
