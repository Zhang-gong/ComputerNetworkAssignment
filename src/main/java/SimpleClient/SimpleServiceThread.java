package SimpleClient;

/*
 *
 * XMU CNNS CLass Demo
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleServiceThread extends Thread {

    private List<Socket> pool;

    public SimpleServiceThread(List<Socket> pool) {
        this.pool = pool;
    }

    public void run() {

        while (true) {
            // get a new request connection
            Socket s = null;

            synchronized (pool) {
                while (pool.isEmpty()) {
                    try {
                        System.out.println("Thread " + this + " sees empty pool.");
                        pool.wait();
                    }
                    catch (InterruptedException ex) {
                        System.out.println("Waiting for pool interrupted.");
                    } // end of catch
                } // end of while

                // remove the first request
                s = (Socket) pool.remove(0);
                System.out.println("Thread " + this
                        + " process request " + s);
            } // end of extract a request

            serveARequest( s );

        } // end while

    } // end run

    private void serveARequest(Socket connSock) {
        try {
            // create read stream to get input
            System.out.println("start ");
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connSock.getInputStream()));
            String clientSentence = inFromClient.readLine();
            System.out.println(clientSentence);
            System.out.println("finish");
            // process input
            //String capitalizedSentence = clientSentence.toUpperCase() + '\n';

            //no need to send reply
           // DataOutputStream outToClient = new DataOutputStream(connSock.getOutputStream());
            //outToClient.writeBytes(capitalizedSentence);
        } catch (Exception e) {
        }
    } // end of serveARequest

} // end ServiceThread