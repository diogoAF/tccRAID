package server.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import dt.file.Block;
import request.RequestType;

public class Operation extends Thread {
    public static final int BUFFER_SIZE = 1024*1024;

    private boolean verbose;
    private Socket clientSocket;
    private String dir;
    
    public Operation(Socket socket, String dir, boolean verbose) {
        this.clientSocket = socket;
        this.dir = dir;
        this.verbose = verbose;
    }
    
    public void run() {
        if(verbose)
            System.out.println("Cliente conectado do IP "
                +clientSocket.getInetAddress().getHostAddress());
        
        try {
            InputStream in = clientSocket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(in);
            
            int   reqType = ois.readInt();
            Block block   = (Block)ois.readObject();
            
            switch(reqType) {
            case(RequestType.CREATE):
                create(block);
                break;
                
            case(RequestType.DELETE):
                delete(block);
                break;

            case(RequestType.OPEN):
                open(block);
                break;
                
            default:
                if(verbose)
                    System.out.println("Numero de requisicao desconhecido: "+reqType);
                break;
            }

            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
        
    }
    
    private void create(Block block) throws IOException {
        String fileName = new String(dir+"/"+block.getID());
        File   file     = new File(fileName);

        if(!verbose) 
            file     = new File(dir+"/"+0);
        
        FileOutputStream     fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        
        bos.write(block.getBytes());
        
        bos.flush();
        bos.close();
        
        if(verbose)
            System.out.println("Arquivo "+fileName+" criado");
    }

    private void delete(Block block) throws IOException {
        String fileName = new String(dir+"/"+block.getID());
        File   file     = new File(fileName);
        
        if(file.exists()) {
            file.delete();
            if(verbose)
                System.out.println("Arquivo "+fileName+" deletado");
        } else {
            throw new IOException();
        }
    }
    
    private void open(Block block) throws IOException {
        String fileName = new String(dir+"/"+block.getID());
        File   file     = new File(fileName);
        
        if(!verbose)
            fileName = new String(dir+"/"+0);

        FileInputStream     fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        byte[] buffer;
        int length;
        do {
            buffer = new byte[BUFFER_SIZE];
            length = bis.read(buffer);
            if(verbose)
                System.out.println(length);
        } while(length < 0);
        bis.close();
        
        BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());
        
        out.write(buffer, 0 , length);
        out.flush();
       
    }

}
