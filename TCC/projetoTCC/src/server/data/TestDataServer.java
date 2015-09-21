package server.data;

import java.io.IOException;

public class TestDataServer {
	
	public static void main(String[] args) throws IOException {
    	if(args.length < 1) {
            System.out.println("Use: java ClientConsole <processId>");
            System.exit(-1);
    	}
    	MetadataModule m  = new MetadataModule(Integer.parseInt(args[0]));

    	String hostName = "127.0.0.1";
    	int    port     = 2000;
    	long   capacity = 1000L;
    	
    	m.join(hostName, port, capacity);
    }

}
