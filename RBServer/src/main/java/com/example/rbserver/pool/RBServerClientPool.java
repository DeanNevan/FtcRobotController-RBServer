package com.example.rbserver.pool;


import android.util.Log;

import com.example.rbserver.client.ClientTool;
import com.example.rbserver.client.RBClient;
import com.example.rbserver.server.RBServer;

import java.util.HashMap;

public class RBServerClientPool {
    private volatile static RBServerClientPool singleton;
    public static RBServerClientPool getSingleton() {
        if (singleton == null) {
            synchronized (RBServerClientPool.class) {
                if (singleton == null) {
                    singleton = new RBServerClientPool();
                }
            }
        }
        return singleton;
    }
    public RBServer server;

    public void init(RBServer server){
        this.server = server;
        Log.d("RBServer", String.format("服务器 客户端实体ID池初始化"));
    }
    private static HashMap<String, RBClient> clients = new HashMap<>();

    public RBClient getClientViaID(String id){
        return clients.get(id);
    }

    public RBClient addClient(){
        int idx = RBServerClientIDXPool.getSingleton().getId();
        RBClient client = new RBClient(ClientTool.parseIDXtoClientID(idx));
        clients.put(client.client_id, client);

        return client;
    }

    public HashMap<String, RBClient> getClients() {
        return clients;
    }

    public void removeClientViaID(String id){
        RBClient n = getClientViaID(id);
        if (n == null){
            return;
        }
        clients.remove(id);
        int idx = ClientTool.parseClientIDtoIDX(id);
        RBServerClientIDXPool.getSingleton().freeId(idx);
    }

    public int getClientsSize(){
        return clients.size();
    }
}
