package client;

import dt.file.Block;
import dt.file.BlockInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import request.RequestType;


public class ClientServerSocket extends Thread {
    public static final int BUFFER_SIZE = 1024*1024;
    
    Socket    clientSocket;
    BlockInfo blockInfo;
	
	@SuppressWarnings("unused")
    public static void main(String[] args)  {
        try {
            //sendFile();
            BlockInfo blockInfo = new BlockInfo("127.0.0.1",20010,666L);
            ClientServerSocket css = new ClientServerSocket(blockInfo);
           
            File file = new File("test.png");
            //File file = new File("testfile");
            
            FileInputStream     fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            
            byte [] buffer = new byte[(int)file.length()];
            int len = bis.read(buffer);
            fis.close();
            
            Block block = new Block(blockInfo.getID(),buffer);
            
            css.create(block);
            
            byte [] bytes = css.open(block);
            
            //css.open(block, bytes);
            
            file = new File("teste2.png");
            FileOutputStream     fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            
            bos.write(bytes);
            
            bos.flush();
            bos.close();
            
            
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
    }

    public ClientServerSocket(BlockInfo blockInfo) {
        this.blockInfo = blockInfo;
    }

    public void create(Block block) throws ConnectException  {
        send(RequestType.CREATE, block);
    }

    public void delete(Block block) throws ConnectException  {
        send(RequestType.DELETE, block);
    }

    public byte[] open(Block block) throws ConnectException  {
        int triedCount = 0;
        
        while(true) {
            try {
                clientSocket = new Socket(blockInfo.getHostName(), blockInfo.getPort());
                System.out.println("O cliente se conectou ao servidor na porta " 
                                                            + blockInfo.getPort());

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream    oos = new ObjectOutputStream(bos);
                
                oos.writeInt(RequestType.OPEN);
                oos.writeObject(block);
                oos.flush();

                OutputStream out = clientSocket.getOutputStream();
                
                out.write(bos.toByteArray());
                //bos.close();
                
                
                InputStream         is = clientSocket.getInputStream();
                BufferedInputStream in = new BufferedInputStream(is);
                
                while(is.available() == 0);

                byte[] buffer = new byte[BUFFER_SIZE];
                //System.out.println(in.available());
                int length = in.read(buffer);
                System.out.println(length);

                //byte[] bytes= new byte[length];
                //bytes = 
                //clientSocket.close();
                
                return Arrays.copyOfRange(buffer, 0, length);
            } catch(ConnectException | UnknownHostException e) {
                try {
                    Thread.sleep(10*1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    System.exit(-1);
                }
                
                triedCount++;
                if(triedCount>3) {
                    System.out.println("O cliente nao conseguiu conectar no servidor");
                    throw new ConnectException();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
    
    private void send(int ReqType, Block block) throws ConnectException  {
        int triedCount = 0;
        
        while(true) {
            try {
                clientSocket = new Socket(blockInfo.getHostName(), blockInfo.getPort());
                System.out.println("O cliente se conectou ao servidor na porta " 
                                                            + blockInfo.getPort());

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream    oos = new ObjectOutputStream(bos);
                
                oos.writeInt(ReqType);
                oos.writeObject(block);
                oos.flush();

                OutputStream out = clientSocket.getOutputStream();
                
                out.write(bos.toByteArray());
                
                clientSocket.close();
                
                return;
            } catch(ConnectException | UnknownHostException e) {
                try {
                    Thread.sleep(10*1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    System.exit(-1);
                }
                
                triedCount++;
                if(triedCount>3) {
                    System.out.println("O cliente nao conseguiu conectar no servidor");
                    throw new ConnectException();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
    
}