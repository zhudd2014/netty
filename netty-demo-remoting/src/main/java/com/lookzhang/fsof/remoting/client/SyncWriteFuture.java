package com.lookzhang.fsof.remoting.client;


import com.lookzhang.fsof.remoting.JsonBodyProvider;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * @author Jax
 * @version 2015/11/20.
 */
public class SyncWriteFuture implements WriteFuture<JsonBodyProvider> {

    /**
     * 同步辅助
     */
    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * response响应
     */
    private JsonBodyProvider response;

    /**
     * 包序号seq
     */
    private final int seq;

    /**
     * 写操作结果
     */
    private boolean writeResult;

    /**
     * 异常
     */
    private Throwable cause;

    public SyncWriteFuture(int seq) {
        this.seq = seq;
        this.writeResult = true;
    }

    public Throwable cause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public boolean isWriteSuccess() {
        return writeResult;
    }

    public void setWriteResult(boolean result) {
        this.writeResult = result;
    }

    public int getSeq() {
        return seq;
    }

    public JsonBodyProvider getResponse() {
        return response;
    }

    public void setResponse(JsonBodyProvider response) {
        this.response = response;
        latch.countDown();
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return true;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return false;
    }

    public JsonBodyProvider get()
        throws InterruptedException, ExecutionException {
        latch.await();
        return response;
    }

    public JsonBodyProvider get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        if (latch.await(timeout, unit)) {
            return response;
        }
        return null;
    }
}
