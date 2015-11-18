package client;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import bftsmart.tom.util.Storage;

public class ClientTest {

    public static final int READ  = 0;
    public static final int WRITE = 1;
    
    private static int VALUE_SIZE = 1024;
    public static int initId = 0;
    
    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("Usage: ... ClientTest <process id> <num. threads> <type of operations> <number of operations> <interval>");
            System.exit(-1);
        }

        initId = Integer.parseInt(args[0]);
        int numThreads = Integer.parseInt(args[1]);

        int opsType   = Integer.parseInt(args[2]);
        int numberOfOps = Integer.parseInt(args[3]);
        
        int interval = Integer.parseInt(args[4]);
        
        long[] values = new long[numThreads];
        Client[] c = new Client[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Launching client " + (initId + i));
            c[i] = new ClientTest.Client(values, i, opsType, numberOfOps, interval);
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

        Arrays.sort(values);
        double maxTime = values[values.length-1]* 0.000000001; //segundos
        
        int numOperacoes = numThreads * numberOfOps;
        
        int throughput = (int) (numOperacoes/maxTime);
        
        System.out.println("Throughput: "+throughput);;
        System.exit(0);
    }
    
    static class Client extends Thread {
        ClientDFS cdfs;
        long[] values;
        int id;
        int index;
        int opsType;
        int numberOfOps;

        int interval;
        
        byte data;
        
        public Client() {
        }
        
        public Client(long[] values, int index, int opsType, int numberOfOps, int interval) {
            this.values = values;
            
            this.index = index;
            this.id = initId + index;
            this.opsType = opsType;
            this.numberOfOps = numberOfOps;

            this.interval = interval;
            
            this.cdfs = new ClientDFS(id, true);
            
            
        }
        
        public void run() {
            
            //faz umas 20 operacoes para esquentar o sistema
            
            Storage st = new Storage(numberOfOps);
            int req = 0;
            long last_send_instant;
            long start_time = System.nanoTime();
            for (int i = 0; i < numberOfOps; i++, req++) {
                System.out.print("Sending req " + req + "...");
                
               

                try {
                    switch(opsType) {
                    case(READ):
                        last_send_instant = System.nanoTime();
                        cdfs.open("test_"+id+"_"+i);
                        st.store(System.nanoTime() - last_send_instant);
                        break;
                        
                    case(WRITE):
                        last_send_instant = System.nanoTime();
                        cdfs.create("test", "test_"+id+"_"+i);
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
            values[index] = System.nanoTime() - start_time;
            
            
            if (id == initId) {
                System.out.println(this.id + " // Average time for " + numberOfOps + " executions (-10%) = " + st.getAverage(true) / 1000 + " us ");
                System.out.println(this.id + " // Standard desviation for " + numberOfOps + " executions (-10%) = " + st.getDP(true) / 1000 + " us ");
                System.out.println(this.id + " // Average time for " + numberOfOps + " executions (all samples) = " + st.getAverage(false) / 1000 + " us ");
                System.out.println(this.id + " // Standard desviation for " + numberOfOps + " executions (all samples) = " + st.getDP(false) / 1000 + " us ");
                System.out.println(this.id + " // Maximum time for " + numberOfOps + " executions (all samples) = " + st.getMax(false) / 1000 + " us ");
            }
            
        }
    }
}
