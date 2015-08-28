package server.data;

import java.io.IOException;


public class ServerConsole {
	public static void main(String[] args) {
    	if(args.length < 1) {
            System.out.println("Use: java ServerConsole <processId>");
            System.exit(-1);
    	}
    	ServerData d = new ServerData(Integer.parseInt(args[0]));
    	
    	try {
        	byte[] resp;
			
        	resp = d.join();
	        System.out.println("Resposta join: "+new String(resp));

	        
		} catch (IOException e) {
			e.printStackTrace();
            System.exit(-1);
		}
	}
}
