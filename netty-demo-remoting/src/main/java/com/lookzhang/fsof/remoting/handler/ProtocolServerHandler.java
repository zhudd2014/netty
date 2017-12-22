package com.lookzhang.fsof.remoting.handler;

import com.lookzhang.fsof.remoting.ProtocolMessage;
import com.lookzhang.fsof.remoting.concurrency.ServiceMethodInvokeTask;
import com.lookzhang.fsof.remoting.convertor.SpringBeanMethodInvoke;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ExecutorService;


/**
 * Server core handler
 *
 * @author Jax
 * @version 2015/11/20.
 */
@Slf4j
public class ProtocolServerHandler extends SimpleChannelInboundHandler<ProtocolMessage> {

    /**
     * 线程超时时间
     */
    private Integer threadTimeOut;

    /**
     * SpringBeanMethodInvoke
     */
    private SpringBeanMethodInvoke invoker;

    /**
     * spring context
     */
    private ApplicationContext applicationContext;


    /**
     * 调用业务方法的线程池
     */
    private ExecutorService businessLogicExecutor;

    /**
     * @param applicationContext spring context
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        invoker = new SpringBeanMethodInvoke(applicationContext);
    }

    public void setExecutorService(ExecutorService bussinessLogicExecutor) {
        this.businessLogicExecutor = bussinessLogicExecutor;
    }

    public void setThreadTimeOut(int threadTimeOut) {
        this.threadTimeOut = threadTimeOut;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ProtocolMessage msg) {
        log.info("接收客户端{}响应:{}", ctx.channel().remoteAddress().toString(), msg);
        //从Netty的IO线程，切换至业务的IO线程池中，保证网络IO的高吞吐
        ServiceMethodInvokeTask task = new ServiceMethodInvokeTask();
        task.setCtx(ctx);
        task.setMsg(msg);
        task.setInvoker(invoker);
        task.setThreadTimeOut(threadTimeOut);
        task.setExecutorService(businessLogicExecutor);
        businessLogicExecutor.submit(task);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)
        throws Exception {
        log.info("{} Channel is active", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)
        throws Exception {
        log.info("{} Channel is disconnected", ctx.channel().remoteAddress());
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
        throws Exception {
        log.error(ctx.channel().remoteAddress() + " exceptionCaught", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
        throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent)evt;
            if (e.state() == IdleState.READER_IDLE) {
                log.info("{} Channel 读超时, 断开", ctx.channel().remoteAddress());
                ctx.close(); //读空闲，断开连接
            }
            else if (e.state() == IdleState.WRITER_IDLE) {
                //ctx.writeAndFlush(new PingMessage());
            }
        }
    }

}
