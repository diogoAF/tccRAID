package server.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DataServerTeste {
	
	public static void main(String[] args) throws IOException {
    	if(args.length < 1) {
            System.out.println("Use: java DataServerTeste <processId>");
            System.exit(-1);
    	}
    	
    	File config = new File("config/hosts.config");
        BufferedReader br = new BufferedReader(new FileReader(config));
        String str;
        while((str = br.readLine()) != null) {
            if(str.startsWith(args[0]))
                    break;
        }
        System.out.println(str);
        
        String[] strs = str.split(" ");
        
        
    	MetadataModule m;
    	String hostName = strs[1];
    	int    port     = Integer.parseInt(strs[2]);
    	long   capacity = 1000L;
    	
    	m  = new MetadataModule(Integer.parseInt(args[0]), hostName, port, capacity);
    	System.out.println("continue!");
    }

}
