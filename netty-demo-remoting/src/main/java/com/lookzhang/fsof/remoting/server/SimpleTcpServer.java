package com.lookzhang.fsof.remoting.server;

import com.lookzhang.fsof.remoting.handler.ServerProtocolInitializer;
import com.lookzhang.fsof.remoting.handler.config.ServerInitializerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;


/**
 * 基于Spring封装的NettyServer，实现了简单的配置就可以启动实现了协议的TCP Server
 *
 * @author Jax
 * @version 2015/11/20.
 */
@Slf4j
public class SimpleTcpServer implements InitializingBean {


    /**
     * bossGroup线程数
     */
    private int bossCount = 1;

    /**
     * workerGroup线程数，默认取cpu核心数的2倍
     */
    private int workerCount = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 占用端口号
     */
    private int tcpPort = 8999;

    /**
     * tcp套接口排队长度
     */
    private int backlog = 1024;

    /**
     * pipeline配置
     */
    private ServerInitializerConfig serverInitializerConfig;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 是否开启epoll模式 Netty官方建议在Linux环境下开启epoll模式，可以节省资源开销
     */
    private boolean epoll = false;

    /**
     * Spring配置上下文
     */
    private ApplicationContext applicationContext;


    public void setBossCount(int bossCount) {
        this.bossCount = bossCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public void setEpoll(boolean epoll) {
        this.epoll = epoll;
    }

    public void setServerInitializerConfig(ServerInitializerConfig serverInitializerConfig) {
        this.serverInitializerConfig = serverInitializerConfig;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 设置app名称,通常以工程名称命名 see {@link #getAppNameByFilePath}
     *
     * @param appName app名称
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * @return InetSocketAddress
     */
    public InetSocketAddress inetSocketAddress() {
        return new InetSocketAddress(tcpPort);
    }

//    @Override
    public void afterPropertiesSet()
        throws Exception {
        if(applicationContext == null){
            throw new IllegalStateException("没有设置applicationContext");
        }
        start();
    }

    /**
     * 启动服务
     *
     * @throws InterruptedException 中断
     */
    public void start()
        throws InterruptedException {

        if (serverInitializerConfig == null) {
            serverInitializerConfig = new ServerInitializerConfig();
        }
        initMonitor();
        serverInitializerConfig.setApplicationContext(applicationContext);
        ServerProtocolInitializer channelInitializer = new ServerProtocolInitializer(
            serverInitializerConfig);
        ServerBootstrap serverBootstrap = serverBootstrap(channelInitializer);
        InetSocketAddress inetSocketAddress = inetSocketAddress();
        ServerShutdownHook serverShutdownHook = new ServerShutdownHook();
        serverShutdownHook.setBootstrap(serverBootstrap);
        serverShutdownHook.setShutdownHook(channelInitializer);
        serverShutdownHook.registerHook();
        Channel serverChannel = null;
        try {
            //启动Server
            ChannelFuture future = serverBootstrap.bind(inetSocketAddress).sync();
            serverChannel = future.channel();
            serverChannel.closeFuture().sync();
        }
        catch (Exception e) {
            log.error("server error", e);
        }
        finally {
            if (!serverBootstrap.childGroup().isShutdown()) {
                serverBootstrap.childGroup().shutdownGracefully().sync();
            }
            if (!serverBootstrap.group().isShutdown()) {
                serverBootstrap.group().shutdownGracefully().sync();
            }
            if(serverChannel != null){
                serverChannel.close();
            }
        }
    }

    /**
     * 初始化监听器
     */
    public void initMonitor() {
        if (applicationContext != null) {
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)
                applicationContext.getAutowireCapableBeanFactory();

        }
    }

    /**
     * IDE下运行会显示路径错误
     * 该方法在下面工程树状结构时可以自动获取到正确的路径
     * app_path
     * |--source-code.jar
     * |
     * |--config
     * |
     * |--lib
     * |--fsof-remoting-version.jar
     * 如果工程结构不是上面这种结构，请在创建Server的时候设置appName
     * @return String 服务名称
     */
    private String getAppNameByFilePath() {
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation()
            .getPath();
        File file = new File(path).getParentFile().getParentFile();
        String[] array = null;
        if (StringUtils.contains(file.getPath(), "/")) {
            array = StringUtils.split(file.getPath(), "/");
        }
        else if (StringUtils.contains(file.getPath(), "\\")) {
            array = StringUtils.split(file.getPath(), "\\");
        }
        if (array != null) {
            return array[array.length - 1];
        }
        return null;
    }

    /**
     * @return EventLoopGroup
     */
    private EventLoopGroup createBossGroup() {
        if (epoll) {
            return new EpollEventLoopGroup(bossCount);
        }
        else {
            return new NioEventLoopGroup(bossCount);
        }
    }

    /**
     * @return EventLoopGroup
     */
    private EventLoopGroup createWorkerGroup() {
        if (epoll) {
            return new EpollEventLoopGroup(workerCount);
        }
        else {
            return new NioEventLoopGroup(workerCount);
        }
    }

    /**
     * @return Map<ChannelOption<?>, Object>
     */
    private Map<ChannelOption<?>, Object> tcpChannelOptions() {
        Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>();
        options.put(ChannelOption.SO_BACKLOG, backlog);
        options.put(ChannelOption.SO_KEEPALIVE, true);
        options.put(ChannelOption.TCP_NODELAY, true);
        return options;
    }

    /**
     * @param channelInitializer pipeline
     * @return ServerBootstrap
     * @throws InterruptedException 中断
     */
    private ServerBootstrap serverBootstrap(ChannelInitializer channelInitializer)
        throws InterruptedException {
        log.debug("load epoll mode:{}", epoll);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(createBossGroup(), createWorkerGroup());
        if (epoll) {
            serverBootstrap.channel(EpollServerSocketChannel.class);
        }
        else {
            serverBootstrap.channel(NioServerSocketChannel.class);
        }
        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
        serverBootstrap.childHandler(channelInitializer);
        Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
        for (Map.Entry<ChannelOption<?>, Object> entry : tcpChannelOptions.entrySet()) {
            ChannelOption option = entry.getKey();
            Object value = entry.getValue();
            serverBootstrap.option(option, value);
        }
        return serverBootstrap;
    }
}
