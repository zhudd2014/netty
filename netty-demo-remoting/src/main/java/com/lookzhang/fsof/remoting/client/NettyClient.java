package com.lookzhang.fsof.remoting.client;

import com.alibaba.fastjson.JSON;
import com.lookzhang.fsof.remoting.JsonBodyConsumer;
import com.lookzhang.fsof.remoting.JsonBodyProvider;
import com.lookzhang.fsof.remoting.ProtocolMessage;
import com.lookzhang.fsof.remoting.handler.ProtocolClientHandler;
import com.lookzhang.fsof.remoting.util.SeqUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Netty客户端，该客户端实现了fsof的网络层协议
 * @author Jax
 * @version 2015/11/20.
 */
public class NettyClient {

    /**
     * 超慢耗时
     */
    private static final long VERY_SLOW_TIMEMILLIS = 5000;

    /**
     * 慢耗时
     */
    private static final long SLOW = 1000;

    /**
     * 默认超时时间
     */
    private static final int TIMEOUT = 3000;

    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(NettyClient.class);

    /**
     * Netty channel
     */
    private Channel channel;

    /**
     * 短连接=false, 长连接=true
     */
    private boolean keepAlive = false;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    /**
     * 关闭
     * @throws InterruptedException 线程中断
     */
    @PreDestroy
    public void close() throws InterruptedException {
        channel.close();
    }

    @Override
    public String toString() {
        String localAddress = channel != null && channel.localAddress()
                                                 != null ? channel.localAddress().toString() : "";
        return "localAddress:" + localAddress;
    }

    /**
     * 以nio的方式发送消息
     * @param protocolMessage 协议消息对象
     * @return ChannelFuture
     */
    private ChannelFuture sendAsync(ProtocolMessage protocolMessage) {
        Channel channel = getChannel();
        if (channel == null) {
            throw new IllegalStateException("Channel is not opened");
        }
        if (!channel.isWritable()) {
            throw new IllegalStateException("Channel is not writable");
        }
        return channel.writeAndFlush(protocolMessage);
    }

    /**
     * 同步发送消息
     * @param timeoutMillis 等待响应的超时时间(毫秒)
     * @param jsonBodyConsumer consumer消息体对象
     * @return JsonBodyProvider 返回provider端消息体对象
     * @throws TimeoutException 到达超时时间抛出此异常
     */
    public JsonBodyProvider sendSync(int timeoutMillis, JsonBodyConsumer jsonBodyConsumer)
        throws TimeoutException {
        JsonBodyProvider response = null;
        String json = JSON.toJSONString(jsonBodyConsumer);
        ProtocolMessage protocolMessage = new ProtocolMessage();
        final int seq = SeqUtil.generate();
        protocolMessage.getProtocolHeader().setSeq(seq);
        protocolMessage.setJson(json);
        final WriteFuture<JsonBodyProvider> writeFuture = new SyncWriteFuture(seq);
        logger.info("客户端{}发送数据{}", channel.localAddress(), protocolMessage);
        ChannelFuture channelFuture = sendAsync(protocolMessage).syncUninterruptibly();
        ProtocolClientHandler handler = channelFuture.channel().pipeline().get(
            ProtocolClientHandler.class);
        final Map<Integer, WriteFuture<JsonBodyProvider>> futureMap = handler.getFutureMap();
        futureMap.put(seq, writeFuture);
        channelFuture.addListener(new ChannelFutureListener() {
//            @Override
            public void operationComplete(ChannelFuture future)
                throws Exception {
                writeFuture.setWriteResult(future.isSuccess());
                writeFuture.setCause(future.cause());
                // 失败删除
                if (!writeFuture.isWriteSuccess()) {
                    futureMap.remove(seq);
                }
            }
        });
        long methodExecuteTime = 0;
        try {
            long beginTime = System.currentTimeMillis();
            response = writeFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
            long endTime = System.currentTimeMillis();
            methodExecuteTime = endTime - beginTime;
        }
        catch (InterruptedException e) {
            logger.error("NettyClientError", e);
        }
        catch (ExecutionException e) {
            logger.error("NettyClientError", e);
        }
        finally {
            if (methodExecuteTime <= SLOW) {
                logger.info("[normal]调用接口耗时{}毫秒,结果:", methodExecuteTime);
            }
            else if (methodExecuteTime >= VERY_SLOW_TIMEMILLIS) {
                logger.info("[very_slow]调用接口耗时{}毫秒,结果:", methodExecuteTime);
            }
            else {
                logger.info("[slow]调用接口耗时{}毫秒,结果:", methodExecuteTime);
            }
            if (!keepAlive) {
                try {
                    close();
                }
                catch (InterruptedException e) {
                    logger.error("NettyClientError", e);
                }
            }
            return response;
        }
    }

    /**
     * 同步发送消息(采用默认的超时设置)
     * @param jsonBodyConsumer consumer消息体对象
     * @return JsonBodyProvider 返回provider端消息体对象
     * @throws TimeoutException 到达超时时间抛出此异常
     */
    public JsonBodyProvider sendSync(JsonBodyConsumer jsonBodyConsumer)
        throws TimeoutException {
        return sendSync(TIMEOUT, jsonBodyConsumer);
    }

    /**
     * 判断连接是否正常,如果正常返回true,否则返回false
     * @return boolean
     */
    public boolean isConnected() {
        Channel channel = getChannel();
        return channel != null && channel.isActive();
    }

}
