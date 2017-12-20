package com.lookzhang.server.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EchoServerHandler extends ChannelHandlerAdapter {

    private static String special_code = "$_";

    private int counter = 0;

    // DelimiterBasedFrameDecoder 处理
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {

        String body = (String) msg;

        System.out.println("The time server receive order ,the counter is :" + ++counter);
        System.out.println("Body is :  " + body);

        body += special_code;

        ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
        ctx.writeAndFlush(echo);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        System.out.printf("get exception");
        ctx.close();
    }

}
