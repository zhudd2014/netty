package com.lookzhang.fsof.remoting.util;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;


public class AopTargetUtils {
    /**
     * 获取 目标对象
     * 
     * @param proxy
     *            代理对象
     * @return 原对象
     * @throws Exception 异常
     */
    public static Object getTarget(Object proxy)
        throws Exception {
        if (!AopUtils.isAopProxy(proxy)) {
            return proxy;// 不是代理对象
        }
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            return getJdkDynamicProxyTargetObject(proxy);
        }
        else { // cglib
            return getCglibProxyTargetObject(proxy);
        }
    }
    /**
     * 获取被Cglib代理过的原对象
     * 
     * @param proxy 代理对象
     * @return 原对象
     * @throws Exception 异常
     * @see #getCglibProxyTargetObject
     */
    private static Object getCglibProxyTargetObject(Object proxy)
        throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target =
            ((AdvisedSupport)advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
        return target;
    }
    /**
     * 获取被JdkDynamicProxy代理过的原对象
     * 
     * @param proxy 代理对象
     * @return 原对象
     * @throws Exception 异常
     * @see #getJdkDynamicProxyTargetObject
     */
    private static Object getJdkDynamicProxyTargetObject(Object proxy)
        throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy)h.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target = ((AdvisedSupport)advised.get(aopProxy)).getTargetSource().getTarget();
        return target;
    }
}
