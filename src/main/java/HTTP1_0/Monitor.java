package HTTP1_0;

/*
 *
 * AUTHOR : Zhang gong
 */

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.net.*;
import java.text.SimpleDateFormat;

public class Monitor {

    //private ServerSocket welcomeSocket;
    private Socket helloSocket;

    public final static int THREAD_COUNT = 3;
    private MonitorThread[] threads;
    private List<Socket> connSockPool;
    private static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String serverResponse;

    /* Constructor: starting all threads at once */
    public Monitor(int ClientPort) {

        OutputStream os = null;

        try {
            // create server socket
            Socket helloSocket = new Socket(InetAddress.getByName("127.0.0.1"), ClientPort);

            System.out.println("Client started; Sending to  " + ClientPort);

            os = helloSocket.getOutputStream();//outputstream 用于输出
            LocalDateTime time = LocalDateTime.now();
            String localTime = df.format(time);
            String state="start";
            state="close";
            String greeting = "GET \\load HTTP/1.0\r\n" +
                    "Host: zg\r\n" +
                    "Heartbeat: "+state+"\r\n"+
                    "If-Modified-Since: " + localTime + "\r\n" +
                    "User-Agent: monitor" + "\r\n" +
                    "this is heartbeat function!" + "\r\n"
                    + "\r\n";
            os.write(greeting.getBytes());

            //connSockPool = new Vector<Socket>();

            // create thread pool
            threads = new MonitorThread[THREAD_COUNT];

            // start all threads 这里先不测试   稍后添加多线程
//            for (int i = 0; i < threads.length; i++) {
//                threads[i] = new ClientThread(connSockPool);
//                threads[i].start();
//            }
            InputStream in = helloSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            serverResponse = reader.readLine();
            System.out.println(serverResponse);

            while (!serverResponse.equals("")) {
                serverResponse = reader.readLine();
                System.out.println(serverResponse);
            }
            String serverResponse = reader.readLine();
            System.out.println(serverResponse);
        } catch (Exception e) {
            System.out.println("Connecting failed.");
        } // end of catch
        finally {
            //4、释放资源
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

        Monitor client = new Monitor(Port);
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