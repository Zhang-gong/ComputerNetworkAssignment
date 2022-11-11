package SimpleClient;

/*
 *
 * AUTHOR : Zhang gong
 */
import WaitNotify.ServiceThread;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.*;
import java.net.*;

public class SHTTPTestClient {

    //private ServerSocket welcomeSocket;
    private Socket helloSocket;
    public final static int THREAD_COUNT = 3;
    private ClientThread[] threads;
    private static int Parallel;
    private List<Socket> connSockPool;
    private String serverResponse;
    /* Constructor: starting all threads at once */
    public SHTTPTestClient(String server,String servname,int Port,int Parallel,String filename[],long testTime) {

        //OutputStream os = null;

        try {
            // create server socket
            //Socket helloSocket = new Socket(InetAddress.getByName(server), Port);

            //System.out.println("Client started; Sending to  " + Port);
            //connSockPool = new Vector<Socket>();

            // create thread pool
            threads = new ClientThread[Parallel];

            for (int i = 0; i < threads.length; i++) {
                threads[i] = new ClientThread(servname,filename,testTime,Port,server);
                threads[i].start();
                System.out.println("thread["+i+"] start");
            }



        } catch (Exception e) {
            System.out.println("SHTTPTestClient construction failed.");
        }
    }// end of catch


    public static void main(String[] args) throws InterruptedException {
        String server=args[0];
        String serverName=args[1];
        String[] filename=new String[10];
        int Port = Integer.parseInt(args[2]);
//        int Port = 6789;
//        if (args.length >= 1)
//            Port = Integer.parseInt(args[0]);
        Parallel = Integer.parseInt(args[3]);//parallel of threads
        int i=4;
        while( !Character.isDigit(args[i].charAt(0)))
        {
            filename[i-4]=args[i];
            i++;
        }
        long testTime=Integer.parseInt(args[i]);
        SHTTPTestClient client = new SHTTPTestClient(server,serverName,Port,Parallel,filename,testTime);
        client.run(server,Port,testTime);


    } // end of main

    //
    public void run(String server,int Port,long testTime) throws InterruptedException{

        Thread.sleep(testTime*1000);
        //Date dateTime = new Date();
        //long startTime = System.currentTimeMillis();
        DecimalFormat df = new DecimalFormat("0.00");
        Thread.currentThread().sleep(testTime + 3000);
        try{
            long[] numOfByte = new long[Parallel];
            long[] numOfFile = new long[Parallel];
            for(int i=0;i<Parallel;i++) {

                numOfByte[i] = threads[i].getByte();
                numOfFile[i] = threads[i].getFiles();
                System.out.println("Thread["+i+"] send "+numOfFile[i]+" files and "+numOfByte[i]+" Bytes in "+testTime+"seconds");
                double temp1=numOfFile[i]/(testTime*1.0);
                double temp2=8*numOfByte[i]/(testTime*1024.0);
                String temp3= df.format(temp1);
                String temp4=df.format(temp2);
                System.out.println("Thread["+i+"] send "+temp3+" files/sec and "+temp4+" Kbps");
            }

            } catch (Exception e) {
            } // end of catch

    }

} // end of class