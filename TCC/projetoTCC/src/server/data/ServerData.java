package server.data;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerData extends Thread{
    private boolean verbose;
    
    private String hostName;
    private int    port;
    private long   capacity;
    
    @SuppressWarnings("unused")
    private MetadataModule metaModule;

    public ServerData(int id, String host, int port, long cap, boolean ver) {
        this.hostName = host;
        this.port     = port;
        this.capacity = cap;
        this.verbose  = ver;
        
        this.metaModule = new MetadataModule(id, this.hostName, this.port, this.capacity);
        

        ServerSocket serverSocket = null;
        String dirName = Integer.toString(id);
        try {
            File dir = new File(dirName);
            
            dir.mkdir();
            
            serverSocket = new ServerSocket(this.port);
            
            while(true) {
                //System.out.println("Aguardando cliente...");
                Socket clientSocket = serverSocket.accept();
                
                Operation op = new Operation(clientSocket, dirName);
                op.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
       
    }
}
