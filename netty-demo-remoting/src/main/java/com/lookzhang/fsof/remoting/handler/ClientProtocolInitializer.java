package com.lookzhang.fsof.remoting.handler;

import com.lookzhang.fsof.remoting.codec.ProtocolByteBufCodec;
import com.lookzhang.fsof.remoting.codec.ProtocolLengthFieldDecoder;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * NettyClient 的pipeline实现
 * @author Jax
 * @version 2015/11/20.
 */
public class ClientProtocolInitializer extends AbstractProtocolInitializer{

    @Override
    protected void initDuplexPipeline(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ProtocolLengthFieldDecoder());
        pipeline.addLast(new ProtocolByteBufCodec());
        pipeline.addLast(new ProtocolClientHandler());
    }

//    @Override
    public void shutdown() throws Exception {

    }
}
