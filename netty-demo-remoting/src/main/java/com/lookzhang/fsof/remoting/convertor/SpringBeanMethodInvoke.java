package com.lookzhang.fsof.remoting.convertor;

import com.lookzhang.fsof.remoting.util.AopTargetUtils;
import org.springframework.context.ApplicationContext;

/**
 * spring注解方法代理类
 * @author Jax
 * @version 2015/11/20.
 */
public class SpringBeanMethodInvoke extends AbstractMethodInvoke{

    /**
     * spring context
     */
    private ApplicationContext context;

    public SpringBeanMethodInvoke(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Object getClassObject(String serviceName) throws Exception {
        Object proxy = context.getBean(serviceName);
        return AopTargetUtils.getTarget(proxy);
    }
}
