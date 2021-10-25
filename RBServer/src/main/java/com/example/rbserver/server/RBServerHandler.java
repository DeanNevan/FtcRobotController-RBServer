package com.example.rbserver.server;

import android.util.Log;

import com.example.rbserver.client.ClientTool;
import com.example.rbserver.client.RBClient;
import com.example.rbserver.client.RBServerClientHeartBeatManager;
import com.example.rbserver.pool.RBServerClientPool;
import com.example.rbserver.protobuf.RBMessage;
import com.example.rbserver.util.DateUtil;

import java.util.Date;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RBServerHandler extends SimpleChannelInboundHandler<RBMessage.Request> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RBMessage.Request msg){
        if (msg.getType() == RBMessage.Type.HEARTBEAT){
            RBClient client = RBServerClientPool.getSingleton().getClientViaID(msg.getClientID());
            RBServerClientHeartBeatManager.getSingleton().clientHeartBeat(client.client_id);
            RBMessage.Response.Builder responseBuilder = RBMessage.Response.newBuilder();
            responseBuilder
                    .setTimestamp(DateUtil.getSecondTimestamp(new Date()))
                    .setRequestId(msg.getRequestId())
                    .setType(RBMessage.Type.HEARTBEAT);
            response(client, responseBuilder);
        }
        if (msg.getType() == RBMessage.Type.CONNECT){
            RBClient client = RBServerClientHeartBeatManager.getSingleton().clientHeartBeat(ClientTool.parseIDXtoClientID(0));
            Log.d("RBServer", String.format("服务器 通道id:%s 用户请求连接，分配的客户端id：%s", ctx.channel().id(), client.client_id));
            client.ctx = ctx;
            client.setState(RBClient.State.CONNECTED);

            RBMessage.ResponseConnect.Builder responseConnect = RBMessage.ResponseConnect.newBuilder();

            RBMessage.Response.Builder responseBuilder = RBMessage.Response.newBuilder();
            responseBuilder.setType(RBMessage.Type.CONNECT);
            responseBuilder.setRequestId(msg.getRequestId()).setClientID(msg.getClientID());

            responseConnect.setResult(true);
            responseBuilder.setResponseConnect(responseConnect);

            response(client, responseBuilder);
        }
        if (msg.getType() == RBMessage.Type.DISCONNECT){
            RBClient client = RBServerClientPool.getSingleton().getClientViaID(msg.getClientID());
            if (client != null){
                Log.d("RBServer", String.format("服务器 通道id:%s 用户请求断开连接，分配的客户端id：%s", ctx.channel().id(), client.client_id));
                client.ctx = ctx;
                client.setState(RBClient.State.DISCONNECTED);

                RBMessage.ResponseDisconnect.Builder responseDisconnect = RBMessage.ResponseDisconnect.newBuilder();

                RBMessage.Response.Builder responseBuilder = RBMessage.Response.newBuilder();
                responseBuilder.setType(RBMessage.Type.DISCONNECT);
                responseBuilder.setRequestId(msg.getRequestId()).setClientID(msg.getClientID());

                responseDisconnect.setResult(true);
                responseBuilder.setResponseDisconnect(responseDisconnect);

                response(client, responseBuilder);
            }
        }
        if (msg.getType() == RBMessage.Type.ROBOT_REQUEST){
            Log.d("RBServer", "ROBOT_REQUEST");
            if (RBServer.opMode != null){
                Log.d("RBServer", "ROBOT_REQUEST to opMode");
                RBServer.opMode.handleRBServerRobotRequest(msg);
            }
            else{
                RBClient client = RBServerClientPool.getSingleton().getClientViaID(msg.getClientID());

                RBMessage.ResponseRobotRequest.Builder responseRobotRequest = RBMessage.ResponseRobotRequest.newBuilder();
                responseRobotRequest
                        .setWords("OpMode尚未运行")
                        .setResult(false);
                RBMessage.Response.Builder responseBuilder = RBMessage.Response.newBuilder();
                responseBuilder
                        .setType(RBMessage.Type.ROBOT_REQUEST)
                        .setRequestId(msg.getRequestId())
                        .setClientID(msg.getClientID())
                        .setResponseRobotRequest(responseRobotRequest);
                response(client, responseBuilder);
            }
        }
    }

    void response(RBClient client, RBMessage.Response.Builder builder){
        if (client == null) {
            return;
        }
        builder.setClientID(client.client_id);
        boolean result = ResponseWriter.writeResponse(client.ctx, builder.build());
        if (!result) Log.e("RBServer", "发送消息错误");
    }


}
