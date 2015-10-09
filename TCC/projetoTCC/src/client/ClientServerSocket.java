package client;

import dt.file.Block;
import dt.file.BlockInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.data.ServerConsole;


public class ClientServerSocket extends Thread {
    BlockInfo blockInfo;
    
    Socket clientSocket;
	
	public static void main(String[] args)  {
        try {
            //sendFile();
            BlockInfo blockInfo = new BlockInfo("127.0.0.1",20010,666L);
            ClientServerSocket css = new ClientServerSocket(blockInfo);
            String filePath;

            Scanner scanner = new Scanner(System.in);
            System.out.println();    

            System.out.println("Informe o path do arquivo.");
            System.out.print(">");
            filePath  = scanner.nextLine();
            scanner.close();
            File newFile = new File(filePath);
            
            
            
            
            InputStream fileInputStream = new FileInputStream(newFile);
            
            int bytesRead;
            int bytesReadTotal = 0;
            byte [] fileContent = new byte[(int)newFile.length()];
            while ((bytesRead = fileInputStream.read(fileContent)) > 0){
                bytesReadTotal += bytesRead;
            }
            fileInputStream.close();
            
            
            
            Block block = new Block(newFile.length(),blockInfo.getBlockID(),newFile.getName());
            block.setBytes(fileContent, 0, bytesReadTotal);
            
            css.sendFile(block);
            
        } catch (IOException ex) {
            Logger.getLogger(ClientServerSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public ClientServerSocket(BlockInfo blockInfo) {
        this.blockInfo = blockInfo;
    }

    public void sendFile(Block block) throws ConnectException  {
        int triedCount = 0;
        
        while(true) {
            try {
                clientSocket = new Socket(blockInfo.getHostName(), blockInfo.getPort());
                System.out.println("O cliente se conectou ao servidor na porta " + blockInfo.getPort());
                OutputStream out = clientSocket.getOutputStream();
                
                out.write(Block.toBytes(block));

                System.out.println("Arquivo enviado com sucesso!");

                out.close();
                
                break;
            } catch(ConnectException | UnknownHostException e) {
                try {
                    Thread.sleep(10*1000);
                    triedCount++;
                    if(triedCount>3) {

                        System.out.println("O cliente nao conseguiu conectar no servidor");
                        throw new ConnectException();
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        
    }
    
    public void close() throws IOException {
        clientSocket.close();
    }
    
}