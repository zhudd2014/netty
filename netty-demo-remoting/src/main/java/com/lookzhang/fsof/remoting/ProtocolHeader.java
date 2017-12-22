package com.lookzhang.fsof.remoting;

/**
 * 协议包头
 * @author Jax
 * @version 2015/11/20.
 */
public class ProtocolHeader {

    /**
     * 包头长度(32byte)
     */
    public static final int PROTO_HEAD_LENGTH = 32;
    /**
     * 预留字段长度
     */
    public static final int PROTO_HEAD_REVERSE_LENGTH = 12;

    /**
     * 命令类型(2byte)，供内部使用， 必填
     */
    private short cmd = 1;

    /**
     * 版本信息，供内部使用(2byte)
     */
    private short ver = 1;

    /**
     * 报文长度(4byte) = 包体字符串长度(不定长)
     */
    private int len;

    /**
     * CRC校验(4byte)，供内部使用
     */
    private int crc = 0;

    /**
     * 服务器id
     */
    private int svrid;

    /**
     * 包序号
     */
    private int seq;

    /**
     * 预留12个byte的数据，预备扩展使用
     */
    private byte[] reserve;

    public short getCmd() {
        return cmd;
    }

    public void setCmd(short cmd) {
        this.cmd = cmd;
    }

    public short getVer() {
        return ver;
    }

    public void setVer(short ver) {
        this.ver = ver;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getCrc() {
        return crc;
    }

    public void setCrc(int crc) {
        this.crc = crc;
    }

    public int getSvrid() {
        return svrid;
    }

    public void setSvrid(int svrid) {
        this.svrid = svrid;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public byte[] getReserve() {
        return reserve;
    }

    public void setReserve(byte[] reserve) {
        this.reserve = reserve;
    }

    @Override
    public String toString() {
        return "{" +
                "cmd=" + cmd +
                ", ver=" + ver +
                ", len=" + len +
                ", crc=" + crc +
                ", svrid=" + svrid +
                ", seq=" + seq +
                '}';
    }
}
