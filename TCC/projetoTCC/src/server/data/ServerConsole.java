package server.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ServerConsole {
    
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Use: java DataServerTeste <processId> <verbose>");
            System.exit(-1);
        }
        boolean verbose = false;
        if(args.length > 1) {
            verbose = true;
            System.out.println("verbose");
        }
        
        try {
            File config       = new File("config/hosts.config");
            BufferedReader br = new BufferedReader(new FileReader(config));
            
            
            String str = null;
            while((str = br.readLine()) != null) {
                if(str.startsWith(args[0]))
                        break;
            }
            br.close();
            
            if(str.isEmpty()) {
                System.out.println("Server information not found");
                System.exit(-1);
            }
            String[] strs = str.split(" ");
            
            new ServerData( Integer.parseInt(args[0]), strs[1], Integer.parseInt(strs[2]), 1000000000L, verbose );
        
        } catch (IOException e) {
            System.out.println("Erro");
            System.exit(-1);
        }
    }

}
