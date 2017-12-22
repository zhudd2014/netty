package com.lookzhang.fsof.remoting.client;


import java.util.concurrent.Future;

/**
 * Netty客户端异步写消息Future接口
 * @author Jax
 * @version 2015/11/20.
 * @param <T>
 */
public interface WriteFuture<T> extends Future<T>{

    /**
     * 失败异常
     * @return Throwable
     */
    Throwable cause();

    /**
     * @param cause Throwable
     */
    void setCause(Throwable cause);

    /**
     * 是否写入成功
     * @return boolean
     */
    boolean isWriteSuccess();

    /**
     * 设置写入结果
     * @param result boolean
     */
    void setWriteResult(boolean result);

    /**
     * 获取请求的seq
     * @return int
     */
    int getSeq();

    /**
     * 写入服务端响应
     * @param response T
     */
    void setResponse(T response);

    /**
     * 返回服务端响应
     * @return T
     */
    T getResponse();

}
