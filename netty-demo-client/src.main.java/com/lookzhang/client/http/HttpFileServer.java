package com.lookzhang.client.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {

//    private static final String DEFAULT_URL = "/src/com/look/netty";
//
//    public void run(final int port, final String url) {
//        EventLoopGroup bossGroup = new NioEventLoopGroup();
//        EventLoopGroup workGroup = new NioEventLoopGroup();
//
//
//        ChannelInitializer<SocketChannel> fileServerHandler = new ChannelInitializer<SocketChannel>() {
//
//            protected void initChannel(SocketChannel socketChannel) throws Exception {
//                socketChannel.pipeline().addLast("http-decoder", new HttpRequestDecoder());
//                socketChannel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
//                socketChannel.pipeline().addLast("http-encoder", new HttpRequestEncoder());
//                socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
//                socketChannel.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url));
//            }
//        };
//
//        try {
//            ServerBootstrap b = new ServerBootstrap();
//            b.group(bossGroup, workGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .childHandler(fileServerHandler);
//
//
//            ChannelFuture future = b.bind("127.0.0.1", port).sync();
//
//            System.out.println("HTTP文件服务器已启动，网址是 ：http://127.0.0.1:" + port + url);
//
//            future.channel().closeFuture().sync();
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            bossGroup.shutdownGracefully();
//            workGroup.shutdownGracefully();
//        }
//    }
//
//    public static void main(String[] args) {
//        int port = 8080;
//        new HttpFileServer().run(port, DEFAULT_URL);
//    }
}
