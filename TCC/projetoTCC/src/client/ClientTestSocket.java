package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import server.data.ServerConsole;


public class ClientTestSocket {
	
	public static void main(String[] args) throws IOException {
            String hostName = "127.0.0.1";
            String filePath; 
            int portNumber = 4400;
            File newFile;
            
            Scanner scanner = new Scanner(System.in);
            System.out.println();
            
            Socket clientSocket = new Socket(hostName, portNumber);
            System.out.println("O cliente se conectou ao servidor na porta " + clientSocket.getLocalPort());
            
            System.out.println("Informe o path do arquivo.");
            System.out.print(">");
            filePath  = scanner.nextLine();
            newFile = new File(filePath);
            

            byte [] fileContent = new byte[ServerConsole.BUFFER_SIZE];
            InputStream fileInputStream = new FileInputStream(newFile);
            OutputStream out = clientSocket.getOutputStream();
            
            //out.write(newFile.getName().getBytes(), 0, newFile.getName().getBytes().length);
            
            Integer bytesRead;            
            while ((bytesRead = fileInputStream.read(fileContent)) > 0){
                out.write(fileContent, 0, bytesRead);
            }
            
            System.out.println("Arquivo enviado com sucesso!");
            
            out.close();
            scanner.close();
            clientSocket.close();
            fileInputStream.close();
            
    }
}