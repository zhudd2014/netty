package com.lookzhang.fsof.remoting.convertor;

/**
 * 反射方法异常
 * @author Jax
 * @version 2015/11/20.
 */
public class MethodInvokeException extends RuntimeException {

    public MethodInvokeException(Throwable cause) {
        super(cause);
    }

    public MethodInvokeException(String message) {
        super(message);
    }
}
