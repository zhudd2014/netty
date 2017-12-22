package com.lookzhang.fsof.remoting.client;

import com.lookzhang.fsof.remoting.handler.ClientProtocolInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Jax
 * @version 2015/11/20.
 */
public class NettyClientFactory extends BasePooledObjectFactory<NettyClient> {

    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(NettyClientFactory.class);

    /**
     * server端ip
     */
    private String ip;

    /**
     * server端口
     */
    private int port;

    /**
     * 开启epoll模式, 开启=true
     */
    private boolean epoll = false;

    /**
     * Netty 客户端启动辅助类
     */
    private Bootstrap bootstrap;

    /**
     * channel pipeline
     */
    private ChannelInitializer channelInitializer;

    /**
     * Netty ChannelOptions
     */
    private Map<ChannelOption<?>, Object> tcpChannelOptions;

    public NettyClientFactory(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void setEpoll(boolean epoll) {
        this.epoll = epoll;
    }

    public void setChannelInitializer(ChannelInitializer channelInitializer) {
        this.channelInitializer = channelInitializer;
    }

    public void setTcpChannelOptions(Map<ChannelOption<?>, Object> tcpChannelOptions) {
        this.tcpChannelOptions = tcpChannelOptions;
    }

    /**
     * 创建 Bootstrap
     *
     * @param channelInitializer pipeline
     * @return Bootstrap
     */
    public Bootstrap createBootStrap(ChannelInitializer channelInitializer) {
        Bootstrap bootstrap = new Bootstrap();
        if (epoll) {
            //TODO
//            bootstrap.group(new EpollEventLoopGroup());
//            bootstrap.channel(EpollSocketChannel.class);
            bootstrap.group(new NioEventLoopGroup());
            bootstrap.channel(NioSocketChannel.class);
        }
        else {
            bootstrap.group(new NioEventLoopGroup());
            bootstrap.channel(NioSocketChannel.class);
        }
        bootstrap.handler(new LoggingHandler(LogLevel.INFO)).handler(channelInitializer);
        if (tcpChannelOptions == null || tcpChannelOptions.isEmpty()) {
            tcpChannelOptions = tcpChannelOptions();
        }
        for (Map.Entry<ChannelOption<?>, Object> entry : tcpChannelOptions.entrySet()) {
            ChannelOption channelOption = entry.getKey();
            Object value = entry.getValue();
            bootstrap.option(channelOption, value);
        }
        return bootstrap;
    }

    /**
     * @return Map
     */
    public Map<ChannelOption<?>, Object> tcpChannelOptions() {
        Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>();
        options.put(ChannelOption.SO_KEEPALIVE, true);
        options.put(ChannelOption.TCP_NODELAY, true);
        return options;
    }

    /**
     * @return NettyClient
     * @throws InterruptedException 线程中断
     */
    @Override
    public NettyClient create()
        throws InterruptedException {
        NettyClient client = null;
        Channel channel = null;
        try {
            client = new NettyClient();
            channel = createChannel();
            client.setChannel(channel);
        }
        catch (Exception e) {
            logger.error("connect error", e);
        }
        return client;
    }

    /**
     * @return Channel
     */
    public Channel createChannel() {
        if (bootstrap == null) {
            if (channelInitializer != null) {
                bootstrap = createBootStrap(channelInitializer);
            }
            else {
                bootstrap = createBootStrap(new ClientProtocolInitializer());
            }
        }
        ChannelFuture channelFuture = bootstrap.connect(ip, port).syncUninterruptibly();
        return channelFuture.channel();
    }

    @Override
    public PooledObject<NettyClient> wrap(NettyClient client) {
        return new DefaultPooledObject<NettyClient>(client);
    }

    @Override
    public boolean validateObject(PooledObject<NettyClient> p) {
        return super.validateObject(p);
    }

    /**
     *
     */
    @PreDestroy
    public void destroy() {
        if (bootstrap != null) {
            bootstrap.group().shutdownGracefully().syncUninterruptibly();
        }
    }

    /**
     *
     * @param p 池对象
     * @throws Exception 销毁对象异常
     */
    @Override
    public void destroyObject(PooledObject<NettyClient> p)
        throws Exception {
        NettyClient client = p.getObject();
        client.close();
    }

}
