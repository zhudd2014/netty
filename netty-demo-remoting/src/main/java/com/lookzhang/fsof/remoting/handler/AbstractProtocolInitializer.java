package com.lookzhang.fsof.remoting.handler;

import com.lookzhang.fsof.remoting.server.ShutdownHook;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;


/**
 * pipeline抽象类
 *
 * @author Jax
 * @version 2015/11/20.
 */
public abstract class AbstractProtocolInitializer extends ChannelInitializer<SocketChannel>
    implements ShutdownHook {

    @Override
    protected void initChannel(SocketChannel ch)
        throws Exception {
        initDuplexPipeline(ch);
    }

    /**
     * @param ch channel
     */
    protected abstract void initDuplexPipeline(SocketChannel ch);

}
