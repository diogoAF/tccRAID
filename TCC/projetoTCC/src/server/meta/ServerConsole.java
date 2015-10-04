package server.meta;

public class ServerConsole {
    
    public static void main(String[] args){
    	if(args.length < 1) {
            System.out.println("Use: java ServerConsole <processId>");
            System.exit(-1);
        }
    	new ServerDFS(Integer.parseInt(args[0]));
   
        
    }
}