package com.lookzhang.server.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeServerHandler extends ChannelHandlerAdapter {

    private final String requestCode = "QUERY TIME ORDER";

    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {

//        ByteBuf buf = (ByteBuf) msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        String body = new String(req, "UTF-8").substring(0, req.length -
//                System.getProperty("line.separator").length());
        String body = (String) msg;

        System.out.println("The time server receive order ,the counter is :" + ++counter);
        System.out.println("Body is :  " + body);


        String currentTime = requestCode.equalsIgnoreCase(body) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) : "BAD_ORDER";
        currentTime += System.getProperty("line.separator");

        System.out.println("Ret is :  " + currentTime);

        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        System.out.printf("get exception");
        ctx.close();
    }

}
