package MultiThreadServer;

/**
 * * XMU CNNS Class Demo Basic Web Server
 **/

import HTTP1_0.heartbeatThread;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static MultiThreadServer.MultiThreadWebServer.ServernameToDocumentRoot;
import static MultiThreadServer.MultiThreadWebServer.cachefuction;

class MultiThreadRequestHandler {

    static boolean _DEBUG = true;
    static boolean _HEARTBEAT=false;
    static boolean isLoad=false;
    static int reqCount = 0;
    static boolean mobile = false;
    static String DocumentRoot = "zg";
    static String defaultDocumentRoot = "zg";
    static LocalDateTime ifModifiedSince;
    public static int timeOutTime=10*1024;
    static LocalDateTime lastModified;
    static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String WWW_ROOT;
    public static Socket connSocket;
    BufferedReader inFromClient;
    DataOutputStream outToClient;
    static boolean isStart;
    heartbeatThread heart=new heartbeatThread();

    String urlName;
    String fileName;
    File fileInfo;


    public MultiThreadRequestHandler(Socket connectionSocket,
                                     String WWW_ROOT) throws Exception {
        reqCount++;
        this.WWW_ROOT = WWW_ROOT;
        this.connSocket = connectionSocket;
        this.connSocket.setSoTimeout(timeOutTime);//设置超时时间

        inFromClient =
                new BufferedReader(new InputStreamReader(connSocket.getInputStream()));

        outToClient =
                new DataOutputStream(connSocket.getOutputStream());

    }


    public void processRequest()//在这里正式开始处理request
    {


        try {
            mapURL2File();
            if(isLoad)
            {
/*                heartbeatThread heart=new heartbeatThread();*/

                heartbeatHeader();
                heartbearBody();
                connSocket.close();
                if(_HEARTBEAT==true)
                {
                    heart.start();
                }
                if(_HEARTBEAT==false) {
                    heart.stop();//true 启动
                }
            }
            else if (fileInfo != null) // found the file and knows its info
            {
                outputResponseHeader();
                outputResponseBody();
                connSocket.close();
            } // dod not handle error


        } catch (Exception e) {
            outputError(400, "Server error");
        }


    } // end of processARequest

    private void mapURL2File() throws Exception {
        isLoad=false;
        mobile=true;
        String requestMessageLine = inFromClient.readLine();
        DEBUG("Request " + reqCount + ": " + requestMessageLine);

        // process the request
        String[] request = requestMessageLine.split("\\s");

        if (request.length < 2 || !request[0].equals("GET")) {
            outputError(500, "Bad request");
            return;
        }

        // parse URL to retrieve file name
        urlName = request[1];
        urlName=urlName.replace("/","\\");
        if (urlName.startsWith("\\") == true)
            urlName = urlName.substring(1);

        // debugging
        if (_DEBUG) {
            String line = inFromClient.readLine();
            while (!line.equals("")) {
                DEBUG("Header: " + line);
                String[] Words = line.split("\\s+");
                switch (Words[0]) {
                    case "If-Modified-Since:": {
                        String ifMS = Words[1] + " " + Words[2];
                        ifModifiedSince = LocalDateTime.parse(ifMS, df);
                    }
                    case "User-Agent:": {
                        if (line.contains("iphone")) {
                            mobile = true;
                        }

                    }
                    case "Host:": {
                        String hostName = Words[1];
                        DocumentRoot = ServernameToDocumentRoot.get(hostName);
                        if(DocumentRoot==null)
                        {
                            DocumentRoot=defaultDocumentRoot;
                        }
                    }
                    case "Heartbeat:":{
                        String heartState=Words[1];
                        if(heartState.equals("close"))
                        {
                            _HEARTBEAT=false;
                        }
                        else if(heartState.equals("start"))
                        {
                            _HEARTBEAT=true;
                        }

                    }
                }
                line = inFromClient.readLine();
            }
        }

        // map to file name
        if (urlName.endsWith("\\")) {
            if (mobile == true) {
                urlName += "m_index.html";
            } else
                urlName += "index.html";
        }
        else if(urlName.endsWith(".py")){
            fileName = WWW_ROOT + DocumentRoot +"\\"+ urlName;
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("python",fileName);
            //Process process = null;
            //process = processBuilder.start();
            //BufferedReader bfReader=new BufferedReader(new InputStreamReader(process.getInputStream()));
            int temp=urlName.lastIndexOf("\\");//更改请求文件为cgi.html
            String temp_1=urlName.substring(0,temp);
            urlName=temp_1+"\\cgi.html";
        }
        else if(urlName.endsWith("load"))
        {
            isLoad=true;
            return;
        }
        fileName = WWW_ROOT + DocumentRoot +"\\"+ urlName;
        DEBUG("Map to File name: " + fileName);

        fileInfo = new File(fileName);
        if (!fileInfo.isFile()) {
            outputError(404, "Not Found");
            fileInfo = null;
        }

    } // end mapURL2file


    private void outputResponseHeader() throws Exception {
        InetAddress host = connSocket.getInetAddress();
        outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
//        outToClient.writeBytes("Set-Cookie: MyCool433Seq12345\r\n");
        outToClient.writeBytes("Server: " + host.getHostAddress() + "\r\n");
        if (urlName.endsWith(".jpg"))
            outToClient.writeBytes("Content-Type: image/jpeg\r\n");
        else if (urlName.endsWith(".gif"))
            outToClient.writeBytes("Content-Type: image/gif\r\n");
        else if (urlName.endsWith(".html") || urlName.endsWith(".htm"))
            outToClient.writeBytes("Content-Type: text/html\r\n");
        else
            outToClient.writeBytes("Content-Type: text/plain\r\n");
        if(ifModifiedSince!=null) {
            Long tempIfMS = ifModifiedSince.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            Updatelastmodified();
            Long tempLastM = lastModified.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            if (tempLastM > tempLastM) {
                outToClient.writeBytes("Last-Modified:" + df.format(lastModified));
            }
        }
    }

    private void Updatelastmodified() throws Exception {

        FileInputStream inputStream = new FileInputStream(WWW_ROOT + "/conf/lastModified.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String LastM = bufferedReader.readLine();
        lastModified = LocalDateTime.parse(LastM, df);
        bufferedReader.close();
        inputStream.close();
    }

    private void writelastmodified() throws Exception {
        BufferedWriter bfwriter = new BufferedWriter(new FileWriter(WWW_ROOT + "/conf/lastModified.txt"));
        LocalDateTime time = LocalDateTime.now();
        String localTime = df.format(time);
        bfwriter.write(localTime);
        bfwriter.close();
    }

    private void outputResponseBody() throws Exception {

        int numOfBytes = (int) fileInfo.length();
        outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
        outToClient.writeBytes("\r\n");

        // send file content
        byte[] fileInBytes = new byte[numOfBytes];
        fileInBytes = cachefuction(fileName);
        if (fileInBytes == null) {
            try {
                FileInputStream fileStream = new FileInputStream(fileName);
                fileInBytes = new byte[numOfBytes];
                fileStream.read(fileInBytes);
                fileStream.close();
            } catch (FileNotFoundException ex) {
            }
        }
        outToClient.write(fileInBytes, 0, numOfBytes);
        outToClient.writeBytes("\r\n");
    }

    void outputError(int errCode, String errMsg) {
        try {
            outToClient.writeBytes("HTTP/1.0 " + errCode + " " + errMsg + "\r\n");
        } catch (Exception e) {
        }
    }

//    static void heartbeat()
//    {
//        long date = System.currentTimeMillis();
//        if(System.currentTimeMillis()-date>= 30*1000)
//        {
//            _HEARTBEAT=true;//开启心跳监测功能
//        }
//    }
private void heartbeatHeader() throws Exception {
    InetAddress host = connSocket.getInetAddress();
    outToClient.writeBytes("HTTP/1.0 200 heartbeat start\r\n");
//        outToClient.writeBytes("Set-Cookie: MyCool433Seq12345\r\n");
//    outToClient.writeBytes("Server: " + host.getHostAddress() + "\r\n");
//    if(ifModifiedSince!=null) {
//        Long tempIfMS = ifModifiedSince.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//        Updatelastmodified();
//        Long tempLastM = lastModified.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//        if (tempLastM > tempLastM) {
//            outToClient.writeBytes("Last-Modified:" + df.format(lastModified));
//        }
//        }
}
    private void heartbearBody() throws Exception {

        outToClient.writeBytes("\r\n");

    }

    static void DEBUG(String s) {
        if (_DEBUG)
            System.out.println(s);
    }

    //映射路径



}

