package com.lookzhang.fsof.remoting.client;

/**
 * @author Jax
 * @version 2015/11/20.
 */
public class NettyConnectionException extends RuntimeException{

    public NettyConnectionException() {
        super();
    }

    public NettyConnectionException(String message) {
        super(message);
    }

    public NettyConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NettyConnectionException(Throwable cause) {
        super(cause);
    }
}
