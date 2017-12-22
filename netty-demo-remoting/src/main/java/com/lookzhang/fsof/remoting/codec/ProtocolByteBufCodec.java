package com.lookzhang.fsof.remoting.codec;

import com.lookzhang.fsof.remoting.ProtocolMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;


/**
 * 实现 pipeline中的 
 * {@link ByteBuf} 互转
 * @author Jax
 * @version 2015/11/20.
 */
public class ProtocolByteBufCodec extends ByteToMessageCodec<ProtocolMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtocolMessage msg, ByteBuf out)
        throws Exception {
        ByteBuf buf = msg.packDataToByteBuf();
        if (buf.readableBytes() > 0) {
            out.writeBytes(buf);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
        throws Exception {
        while (in.isReadable()) {
            int outputSizeBefore = out.size();
            int readableBytesBefore = in.readableBytes();
            ProtocolMessage protocolMessage = new ProtocolMessage();
            protocolMessage.analysisPktFromByteBuf(in);
            out.add(protocolMessage);
            int outputSizeAfter = out.size();
            int readableBytesAfter = in.readableBytes();
            boolean didNotDecodeAnything = outputSizeBefore == outputSizeAfter;
            boolean didNotReadAnything = readableBytesBefore == readableBytesAfter;
            if (didNotDecodeAnything && didNotReadAnything) {
                break;
            }
        }
    }
}
