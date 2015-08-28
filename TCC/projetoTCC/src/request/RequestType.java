package request;

public class RequestType {
	//Client
	public static final int CREATE	= 1;
	public static final int DELETE	= 2;
	public static final int OPEN	= 3;
	public static final int APPEND	= 4;
	public static final int CLOSE	= 5;
	
	public static final int CREATEDIR = 101;
	public static final int DELETEDIR = 102;
	public static final int OPENDIR   = 103;
	public static final int RENAMEDIR = 104;
	public static final int CLOSEDIR  = 105;
	

	
	//DataServer
	public static final int JOIN = 1001;
	

}
