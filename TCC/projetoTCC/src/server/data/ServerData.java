package server.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import request.RequestType;
import bftsmart.tom.ServiceProxy;

public class ServerData {
    public static final int BUFFER_SIZE = 16*1024;
    private String hostName;
    private long capacity;
    private int port;
    
    private MetadataModule metaModule;
    

    public ServerData(int id) {
        
    }
    
    
}
