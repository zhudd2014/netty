package com.lookzhang.fsof.remoting.handler;

import com.lookzhang.fsof.remoting.codec.ProtocolByteBufCodec;
import com.lookzhang.fsof.remoting.codec.ProtocolLengthFieldDecoder;
import com.lookzhang.fsof.remoting.handler.config.IdleStateConfig;
import com.lookzhang.fsof.remoting.handler.config.ServerInitializerConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Server的pipeline
 *
 * @author Jax
 * @version 2015/11/20.
 */
public class ServerProtocolInitializer extends AbstractProtocolInitializer {

    /**
     * Netty Server pipeline initializer
     */
    private ServerInitializerConfig serverInitializerConfig;

    /**
     * 业务io线程池,这里建为可伸缩线程池
     */
    private ExecutorService businessLogicExecutor = Executors.newCachedThreadPool();

    public ServerProtocolInitializer(ServerInitializerConfig serverInitializerConfig) {
        this.serverInitializerConfig = serverInitializerConfig;
    }

    @Override
    protected void initDuplexPipeline(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        IdleStateConfig idleStateConfig = serverInitializerConfig.getIdleStateConfig();
        pipeline.addLast("idleStateHandler",
            new IdleStateHandler(idleStateConfig.getReaderIdleTimeSeconds(),
                idleStateConfig.getWriterIdleTimeSeconds(),
                idleStateConfig.getAllIdleTimeSeconds()));
        pipeline.addLast(new ProtocolLengthFieldDecoder());
        pipeline.addLast(new ProtocolByteBufCodec());
        pipeline.addLast(createProtocolServerHandler());
    }

    /**
     * create handler
     * @return ProtocolServerHandler
     */
    public ProtocolServerHandler createProtocolServerHandler() {
        ProtocolServerHandler protocolServerHandler = new ProtocolServerHandler();
        protocolServerHandler.setThreadTimeOut(serverInitializerConfig.getThreadTimeout());
        protocolServerHandler.setApplicationContext(
            serverInitializerConfig.getApplicationContext());
        protocolServerHandler.setExecutorService(businessLogicExecutor);
        return protocolServerHandler;
    }

//    @Override
    public void shutdown()
        throws Exception {
        businessLogicExecutor.awaitTermination(10, TimeUnit.SECONDS);
    }
}
