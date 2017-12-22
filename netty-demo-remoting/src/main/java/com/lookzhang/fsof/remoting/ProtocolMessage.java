package com.lookzhang.fsof.remoting;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;

/**
 * 协议消息
 * @author Jax.Gong
 * date:2015年5月25日 内部通信协议包
 * <pre>
 * +-------------------------------------------------------------------------------+
 * |                        包头（二进制数据）                           |   包体    |
 * |命令类型| 版本号 | 长度  | 校验CRC | 服务器ID |  包序号  |    预备     |   数据    |
 *  ver(2)  cmd(2)  len(4)   crc(4)   svrid(4)   sn(4)    reserve(12)    json(N)
 * +-----------------------------------------------------------------------------+
 * </pre>
 */
public final class ProtocolMessage {

    /**
     * 协议包头
     */
    private ProtocolHeader protocolHeader = new ProtocolHeader();

    /**
     * 包体json串
     */
    private String json;

    /**
     * utf-8编码
     */
    private static final String JSON_CHARSET = "UTF-8";

    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(ProtocolMessage.class);

    public ProtocolMessage() {
    }

    public ProtocolHeader getProtocolHeader() {
        return protocolHeader;
    }

    public void setProtocolHeader(ProtocolHeader protocolHeader) {
        this.protocolHeader = protocolHeader;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    /**
     * @return byte[]
     * @throws UnsupportedEncodingException 编码异常
     */
    @Deprecated
    public byte[] packDataToByteArray() throws UnsupportedEncodingException {
        return packDataToByteBuf().array();
    }

    /**
     * @param bytes 解析二进制数据
     * @throws UnsupportedEncodingException 编码异常
     */
    @Deprecated
    public void analysisPktFromByteArray(byte[] bytes) throws UnsupportedEncodingException {
        ByteBuf byteBuf = Unpooled.buffer(bytes.length);
        byteBuf.writeBytes(bytes);
        analysisPktFromByteBuf(byteBuf);
    }

    /**
     * 将当前对象的属性转化成ByteBuf
     * @return ByteBuf
     * @throws UnsupportedEncodingException 编码异常
     */
    public ByteBuf packDataToByteBuf() throws UnsupportedEncodingException {
        if (null == protocolHeader) {
            logger.error("包头为空");
            return null;
        }
        if (null == json) {
            logger.warn("包体为空");
            return null;
        }
        //获取包体二进制数据
        byte[] entityData = json.getBytes("UTF-8");
        //设置报文长度,报文长度=包体长度
        protocolHeader.setLen(entityData.length);
        int pktLength = ProtocolHeader.PROTO_HEAD_LENGTH + entityData.length;
        ByteBuf buf = Unpooled.buffer(pktLength);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.writeShort(protocolHeader.getCmd());
        buf.writeShort(protocolHeader.getVer());
        buf.writeInt(protocolHeader.getLen());
        buf.writeInt(protocolHeader.getCrc());
        buf.writeInt(protocolHeader.getSvrid());
        buf.writeInt(protocolHeader.getSeq());
        //包头结束，从包体开始
        buf.writerIndex(ProtocolHeader.PROTO_HEAD_LENGTH);
        buf.writeBytes(entityData, 0, entityData.length);
        return buf;
    }

    /**
     * 将ByteBuf数据解析并且填充到当前对象的属性中
     * @param buf ByteBuf
     * @throws UnsupportedEncodingException 编码异常
     */
    public void analysisPktFromByteBuf(ByteBuf buf) throws UnsupportedEncodingException {
        int readableBytes = buf.readableBytes();
        if(readableBytes < ProtocolHeader.PROTO_HEAD_LENGTH){
            logger.error("包长不正确");
            return;
        }
        buf.order(ByteOrder.BIG_ENDIAN);
        short ver = buf.readShort();
        short cmd = buf.readShort();
        int len = buf.readInt();
        int crc = buf.readInt();
        int svrid = buf.readInt();
        int sn = buf.readInt();
        byte[] reserve = new byte[ProtocolHeader.PROTO_HEAD_REVERSE_LENGTH];
        buf.readBytes(reserve, 0, ProtocolHeader.PROTO_HEAD_REVERSE_LENGTH);
        //包体长度
        int entityDataLength = readableBytes - ProtocolHeader.PROTO_HEAD_LENGTH;
        byte[] entityData = new byte[entityDataLength];
        buf.readBytes(entityData, 0, entityDataLength);
        String json = new String(entityData, JSON_CHARSET);
        ProtocolHeader protocolHeader = new ProtocolHeader();
        protocolHeader.setVer(ver);
        protocolHeader.setCmd(cmd);
        protocolHeader.setLen(len);
        protocolHeader.setCrc(crc);
        protocolHeader.setSvrid(svrid);
        protocolHeader.setSeq(sn);
        //构建新的对象传回
        this.setJson(json);
        this.setProtocolHeader(protocolHeader);
    }

    @Override
    public String toString() {
        return "ProtocolMessage{" +
                "protocolHeader=" + protocolHeader +
                ", json='" + json + '\'' +
                '}';
    }
}
