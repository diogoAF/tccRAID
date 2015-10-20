package server.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ServerConsole {
    
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Use: java DataServerTeste <processId>");
            System.exit(-1);
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
            
            System.out.println(str);
            
            String[] strs = str.split(" ");
            
            new ServerData( Integer.parseInt(args[0]), strs[1], Integer.parseInt(strs[2]), 1000L );
        
        } catch (IOException e) {
            System.out.println("Erro");
            System.exit(-1);
        }
    }

}
