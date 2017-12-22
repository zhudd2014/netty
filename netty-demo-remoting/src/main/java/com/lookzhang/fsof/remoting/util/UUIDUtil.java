package com.lookzhang.fsof.remoting.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

/**
 * 产生唯一串工具类
 * @author Jax
 * @version 2015/11/20.
 */
public class UUIDUtil {

    /**
     *
     */
    private static final int DEFAULT_RAMDOMLENGTH = 6;

    /**
     * 创建长度为6的uuid
     * @return String
     */
    public static String generateUUID(){
        return generateUUID(DEFAULT_RAMDOMLENGTH);
    }

    /**
     * 创建uuid
     * @param ramdomLength 随机数长度
     * @return String
     */
    public static String generateUUID(int ramdomLength){
        String random = RandomStringUtils.randomAlphanumeric(ramdomLength);
        return random + "-" + UUID.randomUUID().toString();
    }

}
