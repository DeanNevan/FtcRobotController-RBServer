package com.example.rbserver.client;

import android.util.Log;

import com.example.rbserver.pool.RBServerClientIDXPool;
import com.example.rbserver.pool.RBServerClientPool;
import com.example.rbserver.util.DateUtil;

import java.util.*;

public class RBServerClientHeartBeatManager {
    static final int MAX_TIMEOUT_SECONDS = 20;
    static final int MONITOR_SLEEP_TIME = 1000;

    private volatile static RBServerClientHeartBeatManager singleton;
    public static RBServerClientHeartBeatManager getSingleton() {
        if (singleton == null) {
            synchronized (RBServerClientHeartBeatManager.class) {
                if (singleton == null) {
                    singleton = new RBServerClientHeartBeatManager();
                }
            }
        }
        return singleton;
    }

    private static void clientHeartBeatTimeout(String id){
        Log.d("RBServer", String.format("客户端实体id：%s 客户端实体心跳超时", id));
        heartBeats.remove(id);
        RBServerClientPool.getSingleton().removeClientViaID(id);
    }

    private static HashMap<String, Integer> heartBeats = new HashMap<>();

    public RBClient newClient(){
        return RBServerClientPool.getSingleton().addClient();
    }

    public RBClient clientHeartBeat(String id){
        int idx = ClientTool.parseClientIDtoIDX(id);

        RBClient n;
        if (idx < RBServerClientIDXPool.MIN){
            n = newClient();
            heartBeats.put(n.client_id, DateUtil.getSecondTimestamp(new Date()));
            return n;
        }
        n = RBServerClientPool.getSingleton().getClientViaID(id);
        if (n == null){
            n = RBServerClientPool.getSingleton().addClient();
        }
        heartBeats.put(n.client_id, DateUtil.getSecondTimestamp(new Date()));
        return n;
    }

    public void activeMonitor(){
        threadHeatBeatsMonitor.active = true;
    }

    public void inactiveMonitor(){
        threadHeatBeatsMonitor.active = false;
    }

    public void startMonitor(){
        activeMonitor();
        threadHeatBeatsMonitor.start();
    }

    public void stopMonitor(){
        inactiveMonitor();
        threadHeatBeatsMonitor.interrupt();
    }

    private ThreadHeatBeatsMonitor threadHeatBeatsMonitor = new ThreadHeatBeatsMonitor();



    static class ThreadHeatBeatsMonitor extends Thread {
        boolean active = false;
        boolean stopFlag = false;
        public void run() {
            while (!stopFlag){
                try {
                    sleep(MONITOR_SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!active) continue;
                int currentTimeStampSecond = DateUtil.getSecondTimestamp(new Date());
                //System.out.println("currentTimeStampSecond:" + currentTimeStampSecond);
                Iterator iter = heartBeats.entrySet().iterator();
                Vector<String> timeoutKeys = new Vector<>();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String key = (String) entry.getKey();
                    int lastTimeSecond = (int) entry.getValue();
                    int timeInterval = currentTimeStampSecond - lastTimeSecond;
//                    System.out.println("key:" + key);
//                    System.out.println("timeInterval:" + timeInterval);
//                    System.out.println();
                    if (timeInterval > MAX_TIMEOUT_SECONDS){
                        timeoutKeys.add(key);
                        //clientHeartBeatTimeout(key);
                    }
                }
                iter = timeoutKeys.iterator();
                while (iter.hasNext()) {
                    String id = (String) iter.next();
                    clientHeartBeatTimeout(id);
                }
            }
        }
        public void willStop(){
            stopFlag = true;
        }
    }
}
