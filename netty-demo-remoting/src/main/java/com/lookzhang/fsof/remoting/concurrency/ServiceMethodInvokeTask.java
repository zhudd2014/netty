package com.lookzhang.fsof.remoting.concurrency;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lookzhang.fsof.remoting.JsonBodyConsumer;
import com.lookzhang.fsof.remoting.JsonBodyProvider;
import com.lookzhang.fsof.remoting.ProtocolMessage;
import com.lookzhang.fsof.remoting.convertor.SpringBeanMethodInvoke;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;


/**
 * 业务IO操作封装在此Task中
 *
 * @author Jax
 * @version 2015/11/20.
 */
@Slf4j
public class ServiceMethodInvokeTask implements Runnable {

    /**
     * 默认线程超时时间10s
     */
    static final int DEFAULT_TIME_OUT = 10000;

    /**
     * 请求计时级别5s
     */
    static final int VERY_SLOW_TIMEMILLIS = 5000;

    /**
     * 请求计时级别1s
     */
    static final int SLOW = 1000;

    /**
     * 耗时等级normal
     */
    static final String SPEED_LEVEL_NORMAL = "normal";

    /**
     * 耗时等级slow
     */
    static final String SPEED_LEVEL_SLOW = "slow";

    /**
     * 耗时等级very_slow
     */
    static final String SPEED_LEVEL_VERY_SLOW = "very_slow";

    /**
     * 线程超时时间
     */
    private Integer threadTimeOut;

    /**
     * ChannelHandlerContext
     */
    private ChannelHandlerContext ctx;

    /**
     * 协议数据包
     */
    private ProtocolMessage msg;

    /**
     * SpringBeanMethodInvoke
     */
    private SpringBeanMethodInvoke invoker;

    /**
     * 业务io线程池
     */
    private ExecutorService executorService;


    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setThreadTimeOut(int threadTimeOut) {
        this.threadTimeOut = threadTimeOut;
    }

    public void setInvoker(SpringBeanMethodInvoke invoker) {
        this.invoker = invoker;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void setMsg(ProtocolMessage msg) {
        this.msg = msg;
    }

//    @Override
    public void run() {
        //接收到的包序号
        final JsonBodyConsumer jsonBodyConsumer = JSON.parseObject(msg.getJson(),
            JsonBodyConsumer.class);
        //invokeMethod方法调用的是spring管理的类,非spring管理的类暂时不支持
        //创建异步Task
        final FutureTask<JsonBodyProvider> futureTask = new FutureTask<JsonBodyProvider>(
            new Callable<JsonBodyProvider>() {
//                @Override
                public JsonBodyProvider call()
                    throws Exception {
                    JsonBodyProvider jsonBodyProvider = invoker.invokeMethod(jsonBodyConsumer);
                    return jsonBodyProvider;
                }
            });
        long beginNanoTime = System.nanoTime();
        //提交Task
        executorService.submit(futureTask);
        try {
            int timeout;
            if (threadTimeOut != null) {
                timeout = threadTimeOut;
            }
            else {
                timeout = DEFAULT_TIME_OUT;
            }
            //同步等待结果至超时
            JsonBodyProvider jsonBodyProvider = futureTask.get(timeout, TimeUnit.MILLISECONDS);

            String json = null;
            //兼容旧格式
            if (jsonBodyProvider.getOldMethodMode() != null
                && jsonBodyProvider.getOldMethodMode()) {
                json = JSONObject.toJSONString(jsonBodyProvider.getResult());
            }
            else {//新格式
                json = JSONObject.toJSONString(jsonBodyProvider);
            }
            ProtocolMessage resultMsg = new ProtocolMessage();
            resultMsg.setJson(json);
            //回传包序号
            resultMsg.getProtocolHeader().setSeq(msg.getProtocolHeader().getSeq());
            if (resultMsg != null) {
                Channel channel = ctx.channel();
                if (channel.isWritable()) {
                    channel.writeAndFlush(resultMsg);
                    collectSuccessLog(beginNanoTime, jsonBodyProvider);
                }
                else { //客户端等待超时且已断开连接的情况,虽然业务方法执行成功了，但是客户端并没有得到成功响应
                    log.warn("Channel {} 连接已关闭 ", channel.remoteAddress());
                    collectErrorLog(beginNanoTime, jsonBodyConsumer);
                    ctx.close();
                }
            }
        }
        catch (InterruptedException e) {
            log.error("业务线程中断异常", e);
            collectErrorLog(beginNanoTime, jsonBodyConsumer);
            ctx.close();
        }
        catch (ExecutionException e) {
            log.error("业务线程运行异常", e);
            collectErrorLog(beginNanoTime, jsonBodyConsumer);
            ctx.close();
        }
        catch (TimeoutException e) {
            log.error("业务线程运行超时", e);
            collectErrorLog(beginNanoTime, jsonBodyConsumer);
            //超时终止运行
            futureTask.cancel(true);
            ctx.close();
        }
        catch (Exception e) {
            collectErrorLog(beginNanoTime, jsonBodyConsumer);
            log.error("业务线程调用异常", e);
            ctx.close();
        }
        finally {
            //释放消息 Netty handler中流转的对象采用了引用计数器 如果不透传到下一个handler就应该在当前handler释放
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 收集处理成功的请求日志
     *
     * @param beginTime
     *     方法开始处理时间
     * @param jsonBodyProvider
     *     处理结果
     */
    public void collectSuccessLog(long beginTime, JsonBodyProvider jsonBodyProvider) {
        long methodExecuteNanoTime = System.nanoTime() - beginTime; //纳秒
        long methodExecuteUsTime = methodExecuteNanoTime / 1000; //微秒
        long methodExecuteTime = methodExecuteNanoTime / 1000000; //毫秒
        String speedLevel = null;
        if (methodExecuteTime <= SLOW) {
            speedLevel = SPEED_LEVEL_NORMAL;
        }
        else if (methodExecuteTime >= VERY_SLOW_TIMEMILLIS) {
            speedLevel = SPEED_LEVEL_VERY_SLOW;
        }
        else {
            speedLevel = SPEED_LEVEL_SLOW;
        }
        log.info("[{}]调用接口耗时{}毫秒,结果:{}", speedLevel, methodExecuteTime, jsonBodyProvider);

    }

    /**
     * 收集异常日志
     *
     * @param beginTime
     *     开始处理时间
     * @param jsonBodyConsumer
     *     consumer消息体
     */
    public void collectErrorLog(long beginTime, JsonBodyConsumer jsonBodyConsumer) {
        long methodExecuteNanoTime = System.nanoTime() - beginTime; //纳秒
        long methodExecuteUsTime = methodExecuteNanoTime / 1000; //微秒
    }
}
