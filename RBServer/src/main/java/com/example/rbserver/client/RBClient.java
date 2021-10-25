package com.example.rbserver.client;

import io.netty.channel.ChannelHandlerContext;

public class RBClient {

    public enum State {
        UNKNOWN, CONNECTED, DISCONNECTED
    }
    private State state = State.UNKNOWN;
    public String client_id = "";
    public ChannelHandlerContext ctx;

    public RBClient(String client_id){
        this.client_id = client_id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

}
