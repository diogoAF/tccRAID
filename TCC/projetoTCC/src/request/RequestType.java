package request;

public class RequestType {
	//Client
	public static final int CREATE	= 1;
	public static final int DELETE	= 2;
	public static final int RENAME	= 3;
	public static final int CLOSE	= 4;
	
	public static final int OPEN	= 10;
	public static final int APPEND	= 11;
	
	public static final int CREATEDIR = 101;
	public static final int DELETEDIR = 102;
	public static final int OPENDIR   = 103;
	public static final int RENAMEDIR = 104;
	public static final int CLOSEDIR  = 105;
	public static final int UPDATEDIR = 106;
	

    public static final int UPDATEACC = 111;
    public static final int OPENROOT  = 112;
    
    public static final int FAILURE = 301;
   /* public static final int CREATEFAILURE = 301;
    public static final int OPENFAILURE   = 302;
    public static final int DELETEFAILURE = 303;
	*/
    
	//DataServer
	public static final int JOIN      = 1001;
	public static final int KEEPALIVE = 1002;
	

}
