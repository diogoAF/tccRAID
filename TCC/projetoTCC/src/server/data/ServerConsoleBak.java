package server.data;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import dt.file.Block;


public class ServerConsoleBak {
    public static final int BUFFER_SIZE = 16*1024;
    private String hostName;
    private long capacity;
    private int port;
    
    public static void main(String[] args) throws IOException, Exception {       
    	if(args.length < 1) {
            System.out.println("Use: java ServerConsole <processId>");
            System.exit(-1);
    	}
    	//ServerData d = new ServerData(Integer.parseInt(args[0]));
    	ServerConsoleBak serverConsole = new ServerConsoleBak();
        
        serverConsole.start(Integer.parseInt(args[0]));

    }
    
    private void start(int portNumber) throws IOException, Exception{        
        Scanner sc   = new Scanner(System.in);
        do{
            try { 
                //Decide a porta do Servidor
                this.port = 20000 + portNumber;
                this.port = 20010;
                ServerSocket serverSocket = new ServerSocket(this.port);
                System.out.println("Aguardando cliente...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado do IP "+clientSocket.getInetAddress().
                        getHostAddress()); // imprime o ip do cliente
                
                saveFile(clientSocket);
                
            } catch (IOException ex) { Logger.getLogger(ServerConsoleBak.class.getName()).log(Level.SEVERE, null, ex); }
        
        }while(!sc.nextLine().equalsIgnoreCase("exit"));
    }

    private void saveFile(Socket clientSocket) throws Exception {        
        InputStream in = null;
        OutputStream out = null;
        Scanner fileName = null;
        Block block = null;
        
        try {
            //fileName = new Scanner(clientSocket.getInputStream());
            in = clientSocket.getInputStream();
        } catch (IOException ex) {
            System.out.println("Não conseguiu coletar o socket input stream. ");
        }

        try {
            //out = new FileOutputStream("C:\\Users\\Diogo\\Documents\\UnB\\TCC\\tccRAID\\TCC\\projetoTCC\\testeSaida.txt");
            //System.out.println("Nome do arquivo: " + in.toString());
            out = new FileOutputStream("testeSaida" + this.port + ".txt");
        } catch (FileNotFoundException ex) {
            System.out.println("Arquivo não encontrado. ");
        }

        byte[] bytes = new byte[BUFFER_SIZE];

        int count;
        while ((count = in.read(bytes)) > 0) {
            block = Block.toBlock(bytes);
            out.write(block.bytes, 0, block.bytes.length);
            //out.write(bytes, 0, count);
            System.out.println("[Porta " + this.port + "] Recebendo " + count + " bytes");
        }

        out.close();
        in.close();
        clientSocket.close();
       
        System.out.println("Arquivo " + block.getFileName() + " copiado com sucesso!");
    }
    
    public static void throwException(String message) throws Exception{
        throw new Exception(message);
    }
    
}
