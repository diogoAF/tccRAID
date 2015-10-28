package server.meta;

public class ServerConsole {
    
    public static void main(String[] args){
    	if(args.length < 3) {
            System.out.println("Use: java ServerConsole <processId> <raidType> <nServers>");
            System.exit(-1);
        }
    	new ServerMeta(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
   
        
    }
}