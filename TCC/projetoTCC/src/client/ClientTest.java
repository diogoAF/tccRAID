package client;

import java.io.IOException;

import bftsmart.tom.util.Storage;

public class ClientTest {

    private static final char READ  = 'r';
    private static final char WRITE = 'w';
    
    @SuppressWarnings("unused")
    private static int VALUE_SIZE = 1024;
    public  static int initId = 0;
    
    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("Use: java ClientTest <processId> <r|w> <1|100|1000> <n threads> <n ops>");
            System.exit(-1);
        }

        initId = Integer.parseInt(args[0]);
        
        char opsType = args[1].charAt(0);
        if(opsType != 'r' && opsType != 'w') {
            System.out.println("Tipo de operacao desconhecido "+opsType);
            System.exit(-1);
        }
        
        String fileSize    = args[2];
        if(!fileSize.equals("1") && !fileSize.equals("100") && !fileSize.equals("1000")) {
            System.out.println("Tamanho de arquivo deve ser 1, 100 ou 1000");
            System.exit(-1);
        }
        
        int numThreads  = Integer.parseInt(args[3]);
        int numOperations = Integer.parseInt(args[4]);
        
        Client[] c = new Client[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Launching client " + (initId + i));
            c[i] = new ClientTest.Client(initId+i, fileSize, opsType, numOperations);
        }

        for (int i = 0; i < numThreads; i++) {

            c[i].start();
        }

        for (int i = 0; i < numThreads; i++) {

            try {
                c[i].join();
            } catch (InterruptedException ex) {
                ex.printStackTrace(System.err);
            }
        }

        System.exit(0);
    }
    
    static class Client extends Thread {
        ClientDFS cdfs;
        long[] values;
        char opsType;
        int  id;
        
        int  numOperations;
        
        String fileName;
        
        public Client(int id, String fileSize, char opsType, int nOps) {
            this.id = id;
            this.opsType = opsType;
            
            this.numOperations = nOps;
            
            fileName = new String("test"+fileSize);
            
            this.cdfs = new ClientDFS(id, false);
        }
        
        public void run() {
            
            //faz umas 20 operacoes para esquentar o sistema
            
            for (int i = 0; i < 20; i++) {
                try {
                    switch(opsType) {
                    case(READ):
                        cdfs.openDir("dir"+i);
                        break;
                        
                    case(WRITE):
                        cdfs.criateDir("dir"+i);
                        break;
                        
                    default:
                        System.out.println("Unknown operation type "+opsType);
                        System.exit(-1);
                        break;
                    }
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
            
            Storage st = new Storage(numOperations);
            long last_send_instant;
            for (int i = 0; i < numOperations; i++) {
                try {

                    switch(opsType) {
                    case(READ):
                        last_send_instant = System.nanoTime();
                    cdfs.open(fileName+"_"+id+"_"+i);
                        st.store(System.nanoTime() - last_send_instant);
                        break;
                        
                    case(WRITE):
                        last_send_instant = System.nanoTime();
                    cdfs.create(fileName, fileName+"_"+id+"_"+i);
                        st.store(System.nanoTime() - last_send_instant);
                        break;
                        
                    default:
                        System.out.println("Unknown operation type "+opsType);
                        System.exit(-1);
                        break;
                        
                    }
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
            
            if (id == initId) {
                System.out.println(this.id + " // Average time for " + numOperations + " executions (-10%) = " + st.getAverage(true) + " ns ");
                System.out.println(this.id + " // Standard desviation for " + numOperations + " executions (-10%) = " + st.getDP(true) + " ns ");
                System.out.println(this.id + " // Average time for " + numOperations + " executions (all samples) = " + st.getAverage(false) + " ns ");
                System.out.println(this.id + " // Standard desviation for " + numOperations + " executions (all samples) = " + st.getDP(false) + " ns ");
                System.out.println(this.id + " // Maximum time for " + numOperations + " executions (all samples) = " + st.getMax(false) + " ns ");
            }
        }
    }
}
