package com.lookzhang.fsof.remoting.util;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

/**
 * 判断Channel是否存活，如果不是活跃的，会主动断开
 * @author Jax
 * @version 2015/11/20.
 */
public class NettyChannelAliveUtil {

    /**
     * Closes the specified channel after all queued write requests are flushed.
     * @param ch channel
     */
    public static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
