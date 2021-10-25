package com.example.rbserver.server;


import android.system.Os;
import android.util.Log;

import com.example.rbserver.client.RBClient;
import com.example.rbserver.client.RBServerClientHeartBeatManager;
import com.example.rbserver.pool.RBServerClientPool;
import com.example.rbserver.protobuf.RBMessage;
import com.example.rbserver.util.DateUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class RBServer extends Thread{
    private volatile static RBServer singleton;
    public static IRBLinearOpMode opMode = null;
    private int port = 8888;

    public static RBServer getSingleton() {
        if (singleton == null) {
            synchronized (RBServer.class) {
                if (singleton == null) {
                    singleton = new RBServer();
                }
            }
        }
        return singleton;
    }

    public void startServer(){
        ThreadRBServer threadRBServer = new ThreadRBServer();
        threadRBServer.start();
    }

    public void doStartServer() throws InterruptedException {
        Log.d("RBServer", String.format("RBServer服务器获取端口...ok"));
        Runtime.getRuntime().addShutdownHook(this);
        RBServerClientHeartBeatManager.getSingleton().startMonitor();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup wokerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,wokerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new RBServerInitializer());

            ChannelFuture channelFuture = serverBootstrap.bind(getSingleton().port).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            wokerGroup.shutdownGracefully();
        }
        Log.d("RBServer", String.format("RBServer服务器启动...ok"));
    }

    public static class ThreadRBServer extends Thread{
        public void run(){
            try {
                getSingleton().doStartServer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void preHandleResponse(RBMessage.Response.Builder responseBuilder){
        responseBuilder.setTimestamp(DateUtil.getSecondTimestamp(new Date()));
    }

    protected void preHandleResponse(RBMessage.Response response){
        response.toBuilder().setTimestamp(DateUtil.getSecondTimestamp(new Date()));
    }

    public void broadcast(RBMessage.Response.Builder responseBuilder){
        preHandleResponse(responseBuilder);
        HashMap<String, RBClient> clients = RBServerClientPool.getSingleton().getClients();
        Iterator iterator = clients.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            String client_id = (String) entry.getKey();
            RBClient client = (RBClient) entry.getValue();
            ResponseWriter.writeResponse(client.ctx, responseBuilder.build());
        }
    }

    public void broadcast(RBMessage.Response response){
        preHandleResponse(response);
        HashMap<String, RBClient> clients = RBServerClientPool.getSingleton().getClients();
        Iterator iterator = clients.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            String client_id = (String) entry.getKey();
            RBClient client = (RBClient) entry.getValue();
            ResponseWriter.writeResponse(client.ctx, response);
        }
    }

    public void sendToClient(String client_id, RBMessage.Response.Builder responseBuilder){
        preHandleResponse(responseBuilder);
        RBClient client = RBServerClientPool.getSingleton().getClientViaID(client_id);
        if (client != null){
            ResponseWriter.writeResponse(client.ctx, responseBuilder.build());
        }
    }

    public void sendToClient(String client_id, RBMessage.Response response){
        preHandleResponse(response);
        RBClient client = RBServerClientPool.getSingleton().getClientViaID(client_id);
        if (client != null){
            ResponseWriter.writeResponse(client.ctx, response);
        }
    }

    public void registerOpMode(IRBLinearOpMode _opMode){
        opMode = _opMode;
    }

    public void run() {
        closeServer();
    }

    public void closeServer(){
        Log.d("RBServer", String.format("RBServer服务器关闭"));
    }

    private Properties properties;

}
