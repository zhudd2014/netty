package com.lookzhang.fsof.remoting.util;

import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.beanutils.ConvertUtilsBean;


/**
 * 类型转换工具
 *
 * @author Jax
 * @version 2015/11/20.
 */
public final class TypeConvertUtil {

    /**
     * 转换对象辅助类
     */
    private static ConvertUtilsBean CONVERT_UTILS_BEAN = BeanUtilsBean2.getInstance()
        .getConvertUtils();

    /**
     * @param in      待转型对象
     * @param inClass 转换类型
     * @param <T>     T
     * @return 转换后对象
     */
    public static <T> T convert(Object in, Class<T> inClass) {
        return (T)CONVERT_UTILS_BEAN.convert(in, inClass);
    }
}
