package server.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServerConsole {
    
    public static void main(String[] args) throws IOException {
        int portNumber;
        
    	if(args.length < 1) {
            System.out.println("Use: java ServerConsole <processId>");
            System.exit(-1);
    	}
    	//ServerData d = new ServerData(Integer.parseInt(args[0]));
    	ServerConsole serverConsole = new ServerConsole();
        
        //Decide a porta do Servidor
        portNumber = 4400 + Integer.parseInt(args[0]);
        
        serverConsole.initServerSocket(portNumber);
    	

    }
    
    private void initServerSocket(int portNumber) throws IOException{
        Path filePath;
        String fileName;
        
    	try { 
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Aguardando cliente...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado do IP "+clientSocket.getInetAddress().
                    getHostAddress()); // imprime o ip do cliente
            /*PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);*/
            
            Scanner scanner = new Scanner(clientSocket.getInputStream());

            fileName = scanner.nextLine() + "saida.txt";
            filePath = Paths.get(fileName);
            BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE);
            
            while (scanner.hasNextLine()) {
              //System.out.println(scanner.nextLine());
              writer.write(scanner.nextLine());
            }
            
            System.out.println("Arquivo criado!");
            writer.close();
            
            /*BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));*/
            
            //Fecha as conexões
            scanner.close();
            clientSocket.close();
            serverSocket.close();
                       
        } catch (IOException ex) { Logger.getLogger(ServerConsole.class.getName()).log(Level.SEVERE, null, ex); }
        
    }
    
}
