package message;

public class ResultType {
	public static final int FAILURE	= 0;
	public static final int SUCCESS	= 1;
	
	public static final int NOSUCHFILE        = 100;
	public static final int FILEALREADYEXISTS = 101;
	public static final int FILENOTEXISTS     = 102;
	public static final int FILELOCKED        = 103;

	public static final int DIRALREADYEXISTS = 201;
	public static final int DIRNOTEXISTS     = 202;
	public static final int DIRLOCKED        = 203;
	
	
	public static final int SERVERFAULT = 301;
	
    public static final int WRONGINDEX = 401;
}
