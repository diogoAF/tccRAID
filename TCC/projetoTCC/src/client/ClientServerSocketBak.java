package client;

import dt.file.Block;
import dt.file.BlockInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.data.ServerConsole;


public class ClientServerSocketBak {
	
	public static void main(String[] args)  {
            try {
                sendFile();
            } catch (IOException ex) {
                Logger.getLogger(ClientServerSocketBak.class.getName()).log(Level.SEVERE, null, ex);
            }
            
    }
        
    private static Socket connectSocket(BlockInfo blockInfo) throws IOException{
        String hostName = blockInfo.getHostName();
        int portNumber = blockInfo.getPort();
        Socket clientSocket = new Socket(hostName, portNumber);
        System.out.println("O cliente se conectou ao servidor na porta " + clientSocket.getPort());
        return clientSocket;
    }
    
    private static File openFile(){
        String filePath;

        Scanner scanner = new Scanner(System.in);
        System.out.println();    

        System.out.println("Informe o path do arquivo.");
        System.out.print(">");
        filePath  = scanner.nextLine();
        scanner.close();
        return new File(filePath);
    }
        
    private static void sendFile() throws IOException {
        File newFile = openFile();
        InputStream fileInputStream = new FileInputStream(newFile);
        
        Integer bytesRead;
        Integer bytesReadTotal = 0;
        byte [] fileContent = new byte[(int)newFile.length()];
        while ((bytesRead = fileInputStream.read(fileContent)) > 0){
                bytesReadTotal += bytesRead;
            }
        fileInputStream.close();
        
        for(int i = 0; i < 3; i++){
            BlockInfo blockInfo; 
            blockInfo = getFileBlockInfo(i);
            Socket clientSocket = connectSocket(blockInfo);

            Block block = new Block(newFile.length(),blockInfo.getBlockID(),newFile.getName());

            OutputStream out = clientSocket.getOutputStream();

            block.setBytes(fileContent, 0, bytesReadTotal);
            out.write(Block.toBytes(block));

            System.out.println("Arquivo enviado com sucesso!");

            out.close();
            clientSocket.close();
        }
    }
        
    private static BlockInfo getFileBlockInfo(int index){
        return new BlockInfo("127.0.0.1",20000+index,100L);
    }
}