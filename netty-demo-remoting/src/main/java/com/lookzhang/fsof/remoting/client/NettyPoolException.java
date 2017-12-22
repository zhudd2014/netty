package com.lookzhang.fsof.remoting.client;

/**
 * @author Jax
 * @version 2015/11/20.
 */
public class NettyPoolException extends RuntimeException {
    public NettyPoolException() {
        super();
    }

    public NettyPoolException(String message) {
        super(message);
    }

    public NettyPoolException(String message, Throwable cause) {
        super(message, cause);
    }

    public NettyPoolException(Throwable cause) {
        super(cause);
    }

    protected NettyPoolException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
//        super(message, cause, enableSuppression, writableStackTrace);
        super(message, cause);
    }
}
