package com.lookzhang.fsof.remoting.handler;

import com.alibaba.fastjson.JSON;
import com.lookzhang.fsof.remoting.JsonBodyProvider;
import com.lookzhang.fsof.remoting.ProtocolMessage;
import com.lookzhang.fsof.remoting.client.WriteFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 缓存NettyClient接收到的消息，并且回写到Future中
 *
 * @author Jax
 * @version 2015/11/20.
 */
public class ProtocolClientHandler extends SimpleChannelInboundHandler<ProtocolMessage> {

    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(ProtocolClientHandler.class);

    /**
     * seq和FutureTask绑定关系
     */
    private Map<Integer, WriteFuture<JsonBodyProvider>> futureMap = new ConcurrentHashMap<Integer, WriteFuture<JsonBodyProvider>>();

    public Map<Integer, WriteFuture<JsonBodyProvider>> getFutureMap() {
        return futureMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage msg)
        throws Exception {
        logger.info("接收服务端{}响应:{}", ctx.channel().remoteAddress(), msg);

        try {
            JsonBodyProvider jsonBodyProvider = JSON.parseObject(msg.getJson(),
                JsonBodyProvider.class);
            int seq = msg.getProtocolHeader().getSeq();
            if (futureMap.containsKey(seq)) {//有发送的seq
                WriteFuture future = futureMap.get(seq);
                future.setResponse(jsonBodyProvider);
            }
        }
        catch (Exception e) {
            logger.error("ProtocolServerHandler error", e);
        }
        finally {
            //释放消息 Netty handler中流转的对象采用了自己实现的引用计数器 如果不透传到下一个handler就应该在当前handler释放
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)
        throws Exception {
        logger.info("Channel {} -> {} is active", ctx.channel().localAddress(),
            ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)
        throws Exception {
        logger.info("Channel {} -> {} is inactive", ctx.channel().localAddress(),
            ctx.channel().remoteAddress());
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
        throws Exception {
        logger.error(
            "Channel " + ctx.channel().localAddress() + " -> " + ctx.channel().remoteAddress()
            + " is error", cause);
        ctx.close();
    }
}
