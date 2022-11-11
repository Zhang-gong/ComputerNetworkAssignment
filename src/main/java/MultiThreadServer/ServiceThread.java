package MultiThreadServer;

/*
 *
 * XMU CNNS CLass Demo
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

import static MultiThreadServer.MultiThreadWebServer.WWW_ROOT;

public class ServiceThread extends Thread {

    private List<Socket> pool;

    public ServiceThread(List<Socket> pool) {
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

            try {
                MultiThreadRequestHandler mtR=new MultiThreadRequestHandler(s,WWW_ROOT);
                mtR.processRequest();
            } catch (Exception e) {

                System.out.println("mutiThreadRQHandler error");
                throw new RuntimeException(e);

            }

        } // end while

    } // end run

//    private void serveARequest(Socket connSock) {
//        try {
//            // create read stream to get input
//            System.out.println("clientSentence1");
//            BufferedReader inFromClient =
//                    new BufferedReader(new InputStreamReader(connSock.getInputStream()));
//            String clientSentence = inFromClient.readLine();
//            System.out.println("clientSentence2");
//            // process input
//            String capitalizedSentence = clientSentence.toUpperCase() + '\n';
//
//            // send reply
//            DataOutputStream outToClient = new DataOutputStream(connSock.getOutputStream());
//            outToClient.writeBytes(capitalizedSentence);
//        } catch (Exception e) {
//        }
//    } // end of serveARequest

} // end ServiceThread