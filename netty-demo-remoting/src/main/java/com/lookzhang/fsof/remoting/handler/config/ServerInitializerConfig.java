package com.lookzhang.fsof.remoting.handler.config;

import org.springframework.context.ApplicationContext;


/**
 * @author Jax
 * @version 2015/11/20.
 */
public class ServerInitializerConfig {

    /**
     * spring配置信息上下文
     */
    private ApplicationContext applicationContext;

    /**
     * Netty超时配置
     */
    private IdleStateConfig idleStateConfig = new IdleStateConfig();

    /**
     * IO线程超时时间,根据业务的需要可以自行设置，如果不设置，默认是10s
     */
    private int threadTimeout = 10000;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public IdleStateConfig getIdleStateConfig() {
        return idleStateConfig;
    }

    public void setIdleStateConfig(IdleStateConfig idleStateConfig) {
        this.idleStateConfig = idleStateConfig;
    }

    public int getThreadTimeout() {
        return threadTimeout;
    }

    public void setThreadTimeout(int threadTimeout) {
        this.threadTimeout = threadTimeout;
    }
}
