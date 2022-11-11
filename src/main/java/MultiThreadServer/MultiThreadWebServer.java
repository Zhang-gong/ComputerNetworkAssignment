package MultiThreadServer;

/**
 * * XMU CNNS Class Demo Basic Web Server
 **/



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

class MultiThreadWebServer {

    private ServerSocket welcomeSocket;
    public static int serverPort = 6789;
    //public static String WWW_ROOT = "/home/httpd/html/zoo/classes/cs433/";
    public static String WWW_ROOT = "D:\\code\\JAVA\\assignment3\\src\\";
    public final static int THREAD_COUNT = 3;
    private ServiceThread[] threads;
    private List<Socket> connSockPool;
    public static long cacheSize=1024*1024;
    private static String ConfFile;
    public static int fullSpace;
    public static Map<String, String> ServernameToDocumentRoot = new HashMap<String, String>();// ServerName和DocumentRoot的映射
    public static Map<String, byte[]> filenameToCache = new HashMap<String, byte[]>();


   public  MultiThreadWebServer(int serverPort) {

        try {
            // create server socket
            welcomeSocket = new ServerSocket(serverPort);
            System.out.println("Server started; listening at " + serverPort);

            connSockPool = new Vector<Socket>();

            // create thread pool
            threads = new ServiceThread[THREAD_COUNT];

            // start all threads
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new ServiceThread(connSockPool);
                threads[i].start();
            }
        } catch (Exception e) {
            System.out.println("Server construction failed.");
        } // end of catch

    } // end of Server

    public static void main(String args[]) throws Exception {

        ConfFile = args[1];
        FileInputStream inputStream = new FileInputStream(WWW_ROOT + "conf\\" + ConfFile);
        BufferedReader bfReader = new BufferedReader(new InputStreamReader(inputStream));
        String newLine = null;
        newLine = bfReader.readLine();

        String documentRoot = null;
        String serverName = null;
        // 读取配置文件
        while (newLine != null) {

            String[] Words = newLine.split("\\s+");
            if (Words.length == 3 && Words[1].equals("DocumentRoot")) {
                documentRoot = Words[2];
            } else if (Words.length == 3 && Words[1].equals("ServerName")) {
                serverName = Words[2];
            } else if (Words[0].equals("Listen")) {
                serverPort = Integer.parseInt(Words[1]);
            } else if (Words[0].equals("CacheSize")) {
                cacheSize = Integer.parseInt(Words[1]) * 1024;
            } //else if (Words[0].startsWith("<") || Words[0].startsWith("#")) {

            if (ServernameToDocumentRoot.get(serverName) == null) {
                ServernameToDocumentRoot.put(serverName, documentRoot);
            }
            newLine = bfReader.readLine();
        }

        // create server socket
        MultiThreadWebServer server=new MultiThreadWebServer(serverPort);
        server.run();
  /*      ServerSocket listenSocket = new ServerSocket(serverPort);
        System.out.println("server listening at: " + listenSocket);
        System.out.println("server www root: " + WWW_ROOT);

        while (true) {

            try {

                // take a ready connection from the accepted queue
                Socket connectionSocket = listenSocket.accept();
                System.out.println("\nReceive request from " + connectionSocket + "\r\n");

                // process a request
                MultiThreadRequestHandler wrh =
                        new MultiThreadRequestHandler(connectionSocket, WWW_ROOT);

                wrh.processRequest();

            } catch (Exception e) {
                System.out.println("MultiThreadRequestHandler error");
            }
        } // end of while (true)*/

    } // end of main

    public void run() {

        while (true) {
            try {
                // accept connection from connection queue
                Socket connSock = welcomeSocket.accept();
                System.out.println("Main thread retrieve connection from "
                        + connSock);

                // how to assign to an idle thread?
                synchronized (connSockPool) {
                    connSockPool.add(connSock);
                    connSockPool.notifyAll();
                } // end of sync
            } catch (Exception e) {
                System.out.println("Accept thread failed.");
            } // end of catch
        } // end of while

    } // end of run
    public static byte[] cachefuction(String fileName) {
        File fileInfo = new File(fileName);
        int BytesOfFileSize = (int) fileInfo.length();
        if (filenameToCache.get(fileName) == null)//没有该文件,放入cache中
        {
            if (fullSpace + BytesOfFileSize <= cacheSize) {
                try {
                    FileInputStream fileStream = new FileInputStream(fileName);
                    byte[] fileInBytes = new byte[BytesOfFileSize];
                    fileStream.read(fileInBytes);
                    filenameToCache.put(fileName, fileInBytes);
                    fullSpace =fullSpace + BytesOfFileSize;
                    return null;
                } catch (Exception e) {
                }
            }
            return null;
        } else {
            return filenameToCache.get(fileName);//这里是文件存在于cache中,直接返回文件
        }
    } // end of class WebServer
}