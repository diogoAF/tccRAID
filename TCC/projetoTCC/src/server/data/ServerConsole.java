package server.data;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServerConsole {
    public static final int BUFFER_SIZE = 16*1024;
    
    public static void main(String[] args) throws IOException, Exception {
        int portNumber;
        
    	if(args.length < 1) {
            System.out.println("Use: java ServerConsole <processId>");
            System.exit(-1);
    	}
    	//ServerData d = new ServerData(Integer.parseInt(args[0]));
    	ServerConsole serverConsole = new ServerConsole();
        
        //Decide a porta do Servidor
        portNumber = 4400 + Integer.parseInt(args[0]);
        
        serverConsole.start(portNumber);

    }
    
    private void start(int portNumber) throws IOException, Exception{        
        Scanner sc   = new Scanner(System.in);
        do{
            try { 
                ServerSocket serverSocket = new ServerSocket(portNumber);
                System.out.println("Aguardando cliente...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado do IP "+clientSocket.getInetAddress().
                        getHostAddress()); // imprime o ip do cliente
                
                saveFile(clientSocket);
                
            } catch (IOException ex) { Logger.getLogger(ServerConsole.class.getName()).log(Level.SEVERE, null, ex); }
        
        }while(!sc.nextLine().equalsIgnoreCase("exit"));
    }

    private void saveFile(Socket clientSocket) throws Exception {        
        InputStream in = null;
        OutputStream out = null;
        Scanner fileName = null;

        try {
            //fileName = new Scanner(clientSocket.getInputStream());
            in = clientSocket.getInputStream();
        } catch (IOException ex) {
            System.out.println("Não conseguiu coletar o socket input stream. ");
        }

        try {
            //out = new FileOutputStream("C:\\Users\\Diogo\\Documents\\UnB\\TCC\\tccRAID\\TCC\\projetoTCC\\testeSaida.txt");
            System.out.println("Nome do arquivo: " + in.toString());
            out = new FileOutputStream("testeSaida.txt");
        } catch (FileNotFoundException ex) {
            System.out.println("Arquivo não encontrado. ");
        }

        byte[] bytes = new byte[BUFFER_SIZE];

        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        out.close();
        in.close();
        clientSocket.close();
        
        /*
        ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
        FileOutputStream fileOutputStream = null;
        byte [] buffer = new byte[BUFFER_SIZE];
        
        //Lê o nome do arquivo
        Object object = ois.readObject();
        
        if(object instanceof String){
            //fileOutputStream = new FileOutputStream(object.toString());
            System.out.println("Nome do arquivo recebido: " + object.toString());
            String fileName = "C:\\Users\\Diogo\\Documents\\UnB\\TCC\\tccRAID\\TCC\\projetoTCC\\testeSaida.txt";
            FileOutputStream out = new FileOutputStream(fileName);
        } else {
            throwException("Não foi informado o nome do arquivo.");
        }
        
        //Percorre todo o arquivo
        Integer bytesRead = 0;
        
        do{
            object = ois.readObject();
            
            if(!(object instanceof Integer)){
                throwException("Não foi recebido a quantidade de bytes.");
            }
            
            bytesRead = (Integer) object;
            System.out.println("Bytes lidos no Server: " + bytesRead);
            object = ois.readObject();
            
            if(!(object instanceof byte[])){
                throwException("Não foi recebido um vetor de bytes.");
            }
            
            buffer = (byte[]) object;
            
            //Escreve dado recebido no arquivo de saida
            fileOutputStream.write(buffer,0,bytesRead);
        } while (bytesRead == BUFFER_SIZE);
        
        System.out.println("Arquivo recebido com sucesso!");
        
        fileOutputStream.close();
        ois.close();
        oos.close();
        */
        System.out.println("Arquivo copiado com sucesso!");
    }
    
    public static void throwException(String message) throws Exception{
        throw new Exception(message);
    }
    
}
