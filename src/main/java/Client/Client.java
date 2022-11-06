package Client;
/*
 *
 * AUTHOR : Zhang gong
 */
import java.io.*;
import java.util.*;
import java.net.*;

public class Client {

    //private ServerSocket welcomeSocket;
    private Socket helloSocket;
    public final static int THREAD_COUNT = 3;
    private ClientThread[] threads;
    private List<Socket> connSockPool;
    private String serverResponse;
    /* Constructor: starting all threads at once */
    public Client(int ClientPort) {

        OutputStream os = null;

        try {
            // create server socket
            Socket helloSocket = new Socket(InetAddress.getByName("127.0.0.1"), ClientPort);

            System.out.println("Client started; Sending to  " + ClientPort);

            os = helloSocket.getOutputStream();//outputstream 用于输出

            String greeting="GET \\src\\file\\file1.html HTTP/1.0\n" +
                    "Host: 127.0.0.1\n" +
                    "this is message!this is message!this is message!"+"\n"
                    +"\n";
            os.write(greeting.getBytes());

            //connSockPool = new Vector<Socket>();

            // create thread pool
            threads = new ClientThread[THREAD_COUNT];

            // start all threads 这里先不测试   稍后添加多线程
//            for (int i = 0; i < threads.length; i++) {
//                threads[i] = new ClientThread(connSockPool);
//                threads[i].start();
//            }
            InputStream in =helloSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            serverResponse = reader.readLine();
            while ( !serverResponse.equals("") ) {
                serverResponse = reader.readLine();
                System.out.println(serverResponse);
            }
            String serverResponse = reader.readLine();
            System.out.println(serverResponse);
        } catch (Exception e) {
            System.out.println("Connecting failed.");
        } // end of catch
        finally {
            //4、释放资源,别忘了哦！！！！
            if (helloSocket != null) {
                try {
                    helloSocket.close();//关闭
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();//关闭
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } // end of Client
    }
    public static void main(String[] args) {
        // see if we do not use default server port
        int Port = 6789;
        if (args.length >= 1)
            Port = Integer.parseInt(args[0]);

        Client client = new Client(Port);
        //client.run();

    } // end of main

    //
//    public void run() {
//
//        while (true) {
//            try {
//                // accept connection from connection queue
//                Socket connSock = helloSocket.accept();
//                System.out.println("Main thread retrieve connection from "
//                        + connSock);
//
//                // how to assign to an idle thread?
//                synchronized (connSockPool) {
//                    connSockPool.add(connSock);
//                    connSockPool.notifyAll();
//                } // end of sync
//            } catch (Exception e) {
//                System.out.println("Accept thread failed.");
//            } // end of catch
//        } // end of while
//
//    } // end of run
//
} // end of class