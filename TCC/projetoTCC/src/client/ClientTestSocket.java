package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;


public class ClientTestSocket {
	
	public static void main(String[] args) throws IOException {
            String hostName = "127.0.0.1";
            String filePath; 
            int portNumber = 4400;
            File newFile;
            BufferedReader reader = null;
            
            Scanner scanner = new Scanner(System.in);
            System.out.println();
            
            Socket clientSocket = new Socket(hostName, portNumber);
            System.out.println("O cliente se conectou ao servidor!");
            
            System.out.println("Informe o path do arquivo.");
            System.out.print(">");
            filePath  = scanner.nextLine();
            newFile = new File(filePath);
            reader = new BufferedReader(new FileReader(newFile));
            PrintStream outStream = new PrintStream(clientSocket.getOutputStream());

            outStream.println(filePath);
            while (reader.ready()) {
              outStream.println(reader.readLine());
            }

            outStream.close();
            scanner.close();
            clientSocket.close();
    }
}