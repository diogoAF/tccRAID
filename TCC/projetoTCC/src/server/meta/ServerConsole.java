package server.meta;

public class ServerConsole {
    
    public static void main(String[] args){
    	if(args.length < 3) {
            System.out.println("Use: java ServerConsole <processId> <raidType> <nServers> <verbose>");
            System.exit(-1);
        }
    	boolean verbose = false;
    	if(args.length > 3) {
    	    if(args[3].equalsIgnoreCase("true")){
                verbose = true;
                System.out.println("verbose");
            }
    	}
    	       
    	new ServerMeta(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), verbose);
   
        
    }
}