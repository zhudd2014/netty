package com.lookzhang.fsof.remoting.handler.config;

/**
 * Netty超时配置类
 *
 * @author Jax
 * @version 2015/11/20.
 */
public class IdleStateConfig {

    /**
     * 读超时时间默认10分钟(600s)
     */
    private int readerIdleTimeSeconds = 600;

    /**
     * 写超时时间,默认禁用
     */
    private int writerIdleTimeSeconds = 0;

    /**
     * 读写混合超时时间，默认禁用
     */
    private int allIdleTimeSeconds = 0;

    public IdleStateConfig() {
    }

    public IdleStateConfig(int allIdleTimeSeconds, int readerIdleTimeSeconds,
                           int writerIdleTimeSeconds) {
        this.allIdleTimeSeconds = allIdleTimeSeconds;
        this.readerIdleTimeSeconds = readerIdleTimeSeconds;
        this.writerIdleTimeSeconds = writerIdleTimeSeconds;
    }

    public int getAllIdleTimeSeconds() {
        return allIdleTimeSeconds;
    }

    public void setAllIdleTimeSeconds(int allIdleTimeSeconds) {
        this.allIdleTimeSeconds = allIdleTimeSeconds;
    }

    public int getReaderIdleTimeSeconds() {
        return readerIdleTimeSeconds;
    }

    public void setReaderIdleTimeSeconds(int readerIdleTimeSeconds) {
        this.readerIdleTimeSeconds = readerIdleTimeSeconds;
    }

    public int getWriterIdleTimeSeconds() {
        return writerIdleTimeSeconds;
    }

    public void setWriterIdleTimeSeconds(int writerIdleTimeSeconds) {
        this.writerIdleTimeSeconds = writerIdleTimeSeconds;
    }
}
