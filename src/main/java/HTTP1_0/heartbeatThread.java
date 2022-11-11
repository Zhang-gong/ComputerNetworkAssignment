package HTTP1_0;

import java.net.SocketException;

import static HTTP1_0.WebRequestHandler._HEARTBEAT;

public class heartbeatThread extends Thread {

    private static int _DEFAULT = 30;
    private static int settime = 10000;
    private static int stopTime = _DEFAULT; // shared state

    private long lastHeartbeat;

    //private boolean isWorking;
    public void run() {
        //isWorking=_HEARTBEAT;
        if (_HEARTBEAT == true) {
            receivedHeartbeat();
        } else if (_HEARTBEAT == false) {
            System.out.println("heatbeatSTOP!");
            Thread.currentThread().stop();
        }
        while (stopTime > 0) {

            stopTime--;
            if (_HEARTBEAT == false) {
                System.out.println("heatbeatSTOP!");
                Thread.currentThread().stop();

            }

            try {

                Thread.sleep(1000);

                int hh = stopTime / 60 / 60 % 60;

                int mm = stopTime / 60 % 60;

                int ss = stopTime % 60;

                System.out.println("wait for hearbeat:" + hh + ":" + mm + ":" + ss);
                if (stopTime == 0) {
                    balance();
                }

            } catch (InterruptedException e) {

                e.printStackTrace();

            } catch (SocketException e) {
                throw new RuntimeException(e);
            }

        }
    }

    void receivedHeartbeat() {
        System.out.println("the socket is OK");
        stopTime = _DEFAULT;
    }

    void balance() throws SocketException {//负载均衡的两种方式:减小排队时间,增大队列容量
        System.out.println("can't receive heart beat!Start to reduce socket timeout-time");
        if (WebRequestHandler.timeOutTime > 160) {
            WebRequestHandler.timeOutTime /= 2;
        }
        if (BasicWebServer.BACKLOG < 8096) {
            BasicWebServer.BACKLOG *= 2;
        }
    }
}
