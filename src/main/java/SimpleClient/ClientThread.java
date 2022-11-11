package SimpleClient;

/*
 *
 * AUTHOR : Zhang gong
 */

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class ClientThread extends Thread {

    private long totalByte;
    private long totalFiles;
    private String servname;
    private String server;
    private int Port;
    private String[] filename=new String[10];
    private long testTime;

    public static int bytesToInt(byte[] a){
        int ans=0;
        for(int i=0;i<4;i++){
            ans<<=8;//左移 8 位
            ans|=a[3-i];//保存 byte 值到 ans 的最低 8 位上
        }
        return ans;
    }
    public ClientThread(String servname,String[] filename,long testTime,int Port,String server)
    {
        this.servname=servname;
        this.testTime=testTime;
        this.filename=filename;
        this.Port=Port;
        this.server=server;
    }

    public void run() {

        long date = System.currentTimeMillis();
        try {
            while(true){
                for(int i=0;i<filename.length;i++){
                    if (System.currentTimeMillis()-date>= testTime*1000)Thread.currentThread().stop();
                    sendARequest(server,servname,Port,filename[i]);
                    if(i==2){
                        i=-1;
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendARequest(String server,String servname,int Port,String filename) {
        try {
            // create read stream to get input


                Socket connSock = new Socket(InetAddress.getByName(server), Port);
                synchronized (connSock) {
                    OutputStream os = connSock.getOutputStream();//outputstream 用于输出
                    String msg = "GET " + filename + " HTTP/1.0\r\n" +
                            "Host: " + "test1"+"\r\n"
                            + "\r\n";
                    byte[] ByteOfMsg =  msg.getBytes();
                    int msgLen=ByteOfMsg.length;
                    totalFiles++;totalByte+=msgLen;
                    os.write(msg.getBytes());
                    System.out.println("request:" + filename);
                    os.close();
                }


        } catch (Exception e) {
        }
    } // end of serveARequest
    long getByte()
    {return totalByte;}
    long getFiles()
    {return totalFiles;}


} // end ClientThread