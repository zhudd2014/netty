package com.lookzhang.fsof.remoting.util;

import org.apache.commons.lang3.RandomUtils;

/**
 * tcp包序号生成工具
 * @author Jax
 * @version 2015/11/20.
 */
public class SeqUtil {

    /**
     * @return int seq
     */
    public static int generate(){
        return RandomUtils.nextInt(100, 9999999);
    }
}
