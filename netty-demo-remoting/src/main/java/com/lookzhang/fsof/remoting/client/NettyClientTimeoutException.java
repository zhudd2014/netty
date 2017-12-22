package com.lookzhang.fsof.remoting.client;

/**
 * @author Jax
 * @version 2015/11/20.
 */
public class NettyClientTimeoutException extends RuntimeException {

    public NettyClientTimeoutException() {
        super();
    }

    public NettyClientTimeoutException(String message) {
        super(message);
    }

    public NettyClientTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public NettyClientTimeoutException(Throwable cause) {
        super(cause);
    }

    protected NettyClientTimeoutException(String message, Throwable cause,
                                          boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause);
//        super(message, cause, enableSuppression, writableStackTrace);
    }
}
