package com.lookzhang.fsof.remoting.server;

/**
 * 自定义关闭钩子接口
 * @author Jax
 * @version 2015/11/20.
 */
public interface ShutdownHook {

    /**
     * @throws Exception 关闭异常
     */
    void shutdown() throws Exception;
}
