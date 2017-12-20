package com.lookzhang.client.http;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpResponseStatus.*;

public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
//        if (!request.getDecoderResult().isSuccess()) {
//            sendError(ctx, BAD_REQUEST);
//            return;
//        }
//
//        if (request.getMethod() != HttpMethod.GET) {
//            sendError(ctx, METHOD_NOT_ALLOWED);
//            return;
//        }
//
//        final String uri = request.getUri();
//        final String path = sanitizeUrl(uri);
//
//        if (path == null) {
//            sendError(ctx, FORBIDDEN);
//            return;
//        }
//
//        File file = new File(path);
//        if (file.isHidden() || !file.exists()) {
//            sendError(ctx, NOT_FOUND);
//            return;
//        }
//
//        //此处省略代码。。。
//        if (!file.isFile()) {
//            sendError(ctx, FORBIDDEN);
//            return;
//        }
//
//        RandomAccessFile randomAccessFile = null;
//
//        try {
//            randomAccessFile = new RandomAccessFile(file, "r");
//        } catch (FileNotFoundException fnfe) {
//            sendError(ctx, NOT_FOUND);
//            return;
//        }
//
//        long fileLength = randomAccessFile.length();
//        HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, OK);
//        setContentLength(httpResponse, fileLength);
//        setContentTypeHeader(httpResponse, file);
//
//        if (isKeepAlive(request)) {
//            httpResponse.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
//        }
//        ctx.write(httpResponse);
//
//        ChannelFuture sendFileFuture;
//
//        sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile, 0, fileLength, 8192), ctx.newProgressivePromise());
//        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
//            public void operationProgressed(ChannelProgressiveFuture channelProgressiveFuture, long l, long l1) throws Exception {
//
//            }
//
//            public void operationComplete(ChannelProgressiveFuture channelProgressiveFuture) throws Exception {
//
//            }
//        });
//    }
//
//
//    private String sanitizeUri(String uri) {
//
//        try {
//            uri = URLDecoder.decode(uri, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        uri = uri.replace('/', File.separatorChar);
//
//        if(){
//
//        }
    }
}
