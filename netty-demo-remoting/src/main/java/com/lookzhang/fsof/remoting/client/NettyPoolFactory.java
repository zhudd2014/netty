package com.lookzhang.fsof.remoting.client;

import org.springframework.beans.factory.InitializingBean;


/**
 * @author Jax
 * @version 2015/11/20.
 */
public class NettyPoolFactory implements InitializingBean {

    /**
     * 启用pool
     */
    private boolean usePool = true;

    /**
     * pool配置
     */
    private NettyPoolConfig poolConfig = new NettyPoolConfig();

    /**
     * pool对象
     */
    private NettyClientPool pool;

    /**
     * NettyClientFactory
     */
    private NettyClientFactory nettyClientFactory;

    public NettyPoolFactory() {
    }

    public NettyPoolFactory(NettyPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

    public NettyPoolFactory(NettyPoolConfig poolConfig, NettyClientFactory nettyClientFactory) {
        this.poolConfig = poolConfig;
        this.nettyClientFactory = nettyClientFactory;
    }

    /**
     * 创建连接池对象
     * @return NettyClientPool
     */
    protected NettyClientPool createNettyConnectionPool() {
        return new NettyClientPool(poolConfig, nettyClientFactory);
    }

    /**
     * 获取连接
     * @return NettyClient
     */
    public NettyClient getClient() {
        try {
            if (pool != null && usePool) {
                return pool.getResource();
            }
            else if (pool == null && usePool) {
                pool = createNettyConnectionPool();
            }
            return nettyClientFactory.create();
        }
        catch (Exception e) {
            throw new NettyPoolException("Cannot get client from pool", e);
        }
    }

    public void setUsePool(boolean usePool) {
        this.usePool = usePool;
    }

    public void setPoolConfig(NettyPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

    public void setNettyClientFactory(NettyClientFactory nettyClientFactory) {
        this.nettyClientFactory = nettyClientFactory;
    }

    //@Override
    public void afterPropertiesSet()
        throws Exception {
        if (usePool) {
            pool = createNettyConnectionPool();
        }
    }

    public NettyClientPool getPool() {
        return pool;
    }

}
