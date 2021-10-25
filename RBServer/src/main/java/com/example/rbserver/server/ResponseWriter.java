package com.example.rbserver.server;

import com.example.rbserver.protobuf.RBMessage;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

public class ResponseWriter {
    public static void writeResponse(ChannelHandlerContext ctx, String msg) {
        ctx.writeAndFlush(msg);
    }

    public static void writeResponse(ChannelHandlerContext ctx, byte[] msg) {
        FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(msg));
        res.headers().set(HttpHeaderNames.CONTENT_LENGTH, msg.length);
        ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    public static boolean writeResponse(ChannelHandlerContext ctx, RBMessage.Response msg) {
        if (ctx == null) return false;
        if (!ctx.isRemoved()){
            ctx.writeAndFlush(msg);
            return true;
        }
        return false;
    }
}
