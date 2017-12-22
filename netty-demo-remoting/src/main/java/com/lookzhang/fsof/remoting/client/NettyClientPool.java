package com.lookzhang.fsof.remoting.client;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import javax.annotation.PreDestroy;
import java.io.Closeable;


/**
 * 连接池
 *
 * @author Jax
 * @version 2015/11/20.
 */
public class NettyClientPool implements Closeable {

    /**
     * pool对象
     */
    private GenericObjectPool<NettyClient> pool;

    public NettyClientPool() {
    }

    // @Override
    @PreDestroy
    public void close() {
        closeInternalPool();
    }

    public boolean isClosed() {
        return this.pool.isClosed();
    }

    public NettyClientPool(final GenericObjectPoolConfig poolConfig,
                           PooledObjectFactory<NettyClient> factory) {
        initPool(poolConfig, factory);
    }

    /**
     * 初始化连接池
     * @param poolConfig 连接池配置
     * @param factory NettyClient工厂
     */
    public void initPool(final GenericObjectPoolConfig poolConfig,
                         PooledObjectFactory<NettyClient> factory) {
        if (this.pool != null) {
            try {
                closeInternalPool();
            }
            catch (Exception e) {
            }
        }
        AbandonedConfig abandonedConfig = new AbandonedConfig();
        this.pool = new GenericObjectPool<NettyClient>(factory, poolConfig, abandonedConfig);
    }

    /**
     * 从连接池获取实例
     * @return NettyClient
     */
    public NettyClient getResource() {
        try {
            return pool.borrowObject();
        }
        catch (Exception e) {
            throw new NettyConnectionException("Could not get a resource from the pool", e);
        }
    }

    /**
     * 将对象归还到连接池
     * @param resource 待释放的对象
     */
    public void returnResourceObject(final NettyClient resource) {
        if (resource == null) {
            return;
        }
        try {
            pool.returnObject(resource);
        }
        catch (Exception e) {
            throw new NettyConnectionException("Could not return the resource to the pool", e);
        }
    }

    /**
     * 将不可用的连接池对象归还到池中，并且销毁
     * @param resource 待释放的对象
     */
    public void returnBrokenResource(final NettyClient resource) {
        if (resource != null) {
            returnBrokenResourceObject(resource);
        }
    }

    /**
     * 归还连接池对象
     * @param resource 待释放的对象
     */
    public void returnResource(final NettyClient resource) {
        if (resource != null) {
            try {
                returnResourceObject(resource);
            }
            catch (NettyConnectionException e) {
                e.printStackTrace();
                returnBrokenResource(resource);
            }
        }
    }

    /**
     * 销毁连接池
     */
    public void destroy() {
        closeInternalPool();
    }

    /**
     * 将不可用的连接池对象归还到池中，并且销毁
     * @param resource 待释放的对象
     */
    protected void returnBrokenResourceObject(final NettyClient resource) {
        try {
            pool.invalidateObject(resource);
        }
        catch (Exception e) {
            throw new NettyConnectionException("Could not return the resource to the pool", e);
        }
    }

    /**
     * 关闭内部池对象
     */
    protected void closeInternalPool() {
        try {
            pool.close();
        }
        catch (Exception e) {
            throw new NettyConnectionException("Could not destroy the pool", e);
        }
    }

    /**
     * 返回当前活跃对象数
     * @return int
     */
    public int getNumActive() {
        if (this.pool == null || this.pool.isClosed()) {
            return -1;
        }
        return this.pool.getNumActive();
    }

    /**
     * 返回当前空闲对象数
     * @return int
     */
    public int getNumIdle() {
        if (this.pool == null || this.pool.isClosed()) {
            return -1;
        }
        return this.pool.getNumIdle();
    }

    /**
     * 获取当前等待对象数
     * @return int
     */
    public int getNumWaiters() {
        if (this.pool == null || this.pool.isClosed()) {
            return -1;
        }
        return this.pool.getNumWaiters();
    }
}
