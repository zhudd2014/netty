package com.lookzhang.fsof.remoting.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 *
 * @author Jax
 * @version 2015/11/20.
 */
public class ChannelReconnectListener implements ChannelFutureListener {

    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(ChannelReconnectListener.class);

    /**
     * client工厂
     */
    private final NettyClientFactory nettyClientFactory;

    /**
     * 映射的client对象
     */
    private final NettyClient client;

    /**
     * 重连间隔
     */
    private final int reconnectInterval;

    /**
     * 重连状态
     */
    private final AtomicBoolean disconnectRequested = new AtomicBoolean(false);

    /**
     * 计划任务线程池
     */
    private final ScheduledExecutorService executorService;

    public ChannelReconnectListener(NettyClient client, NettyClientFactory nettyClientFactory,
                                    int reconnectInterval,
                                    ScheduledExecutorService executorService) {
        this.reconnectInterval = reconnectInterval;
        this.executorService = executorService;
        this.nettyClientFactory = nettyClientFactory;
        this.client = client;
    }

    /**
     * 开启重连
     */
    public void requestReconnect() {
        disconnectRequested.set(false);
    }

    /**
     * 关闭重连
     */
    public void requestDisconnect() {
        disconnectRequested.set(true);
    }

//    @Override
    public void operationComplete(ChannelFuture future)
        throws Exception {
        final Channel channel = future.channel();
        logger.debug("Client connection was closed to {}", channel.remoteAddress());
        channel.disconnect();
        scheduleReconnect();
    }

    /**
     * 重连
     */
    public void scheduleReconnect() {
        if (!disconnectRequested.get()) {
            logger.trace("连接失败, 将会在 {} 毫秒中重连", reconnectInterval);
            executorService.schedule(new Runnable() {
//                @Override
                public void run() {
                    Channel newChannel = nettyClientFactory.createChannel();
                    client.setChannel(newChannel);
                }
            }, reconnectInterval, TimeUnit.MILLISECONDS);
        }
    }
}
