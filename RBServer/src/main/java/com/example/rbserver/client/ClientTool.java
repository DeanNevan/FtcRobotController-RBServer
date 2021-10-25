package com.example.rbserver.client;

public class ClientTool {
    public static int parseClientIDtoIDX(String id){
        return Integer.parseInt(id);
    }

    public static String parseIDXtoClientID(int idx){
        return String.valueOf(idx);
    }
}
