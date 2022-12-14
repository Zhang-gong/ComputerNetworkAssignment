package HTTP1_0;

/**
 * * XMU CNNS Class Demo Basic Web Server
 **/

import java.io.*;
import java.net.*;
import java.util.*;

class BasicWebServer {

    public static int serverPort = 6789;
    //public static String WWW_ROOT = "/home/httpd/html/zoo/classes/cs433/";
    public static String WWW_ROOT = "D:\\code\\JAVA\\assignment3\\src\\";
    public static long cacheSize=1024*1024;
    private static String ConfFile;
    public static int fullSpace;
    public static Map<String, String> ServernameToDocumentRoot = new HashMap<String, String>();// ServerName和DocumentRoot的映射
    public static Map<String, byte[]> filenameToCache = new HashMap<String, byte[]>();
    public static int  BACKLOG=128;

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
        ServerSocket listenSocket = new ServerSocket(serverPort,BACKLOG);
        System.out.println("server listening at: " + listenSocket);
        System.out.println("server www root: " + WWW_ROOT);

        while (true) {

            try {

                // take a ready connection from the accepted queue
                Socket connectionSocket = listenSocket.accept();
                System.out.println("\nReceive request from " + connectionSocket + "\r\n");

                // process a request
                WebRequestHandler wrh =
                        new WebRequestHandler(connectionSocket, WWW_ROOT);

                wrh.processRequest();

            } catch (Exception e) {
                System.out.println("WebRequestHandler error");
            }
        } // end of while (true)

    } // end of main

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