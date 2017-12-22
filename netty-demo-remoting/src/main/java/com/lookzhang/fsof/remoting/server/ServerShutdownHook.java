package com.lookzhang.fsof.remoting.server;

import io.netty.bootstrap.ServerBootstrap;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;


/**
 * Server关闭钩子
 *
 * @author Jax
 * @version 2015/11/20.
 */
@Slf4j
public class ServerShutdownHook {


    /**
     * Netty辅助类
     */
    private ServerBootstrap bootstrap;

    /**
     * 关闭钩子
     */
    private ShutdownHook shutdownHook;

    public ServerShutdownHook() {
    }

    public ServerShutdownHook(ServerBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void setBootstrap(ServerBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void setShutdownHook(ShutdownHook shutdownHook) {
        this.shutdownHook = shutdownHook;
    }

    /**
     * 注册钩子
     */
    @PostConstruct
    public void registerHook() {
        //注册关闭钩子,确保所有线程执行完毕再关闭服务
        //强杀进程无法触发钩子
        log.info("注册关闭钩子线程");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    if (shutdownHook != null) {
                        log.info("开始关闭业务io线程池.....");
                        shutdownHook.shutdown();
                    }
                    log.info("开始关闭boss线程池.....");
                    bootstrap.group().shutdownGracefully().sync();
                    log.info("boss线程池关闭成功");
                    log.info("开始关闭worker线程池.....");
                    bootstrap.childGroup().shutdownGracefully().sync();
                    log.info("worker线程池关闭成功");
                    log.info("Netty服务器关闭成功");
                }
                catch (Exception e) {
                    log.error("关闭钩子执行出现异常", e);
                }
            }
        });
    }

}
