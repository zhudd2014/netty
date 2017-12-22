package com.lookzhang.fsof.remoting.convertor;

/**
 *
 * @author Jax
 * @version 2015/11/20.
 */
public class ReflectBeanMethodInvoke extends AbstractMethodInvoke{

    @Override
    public Object getClassObject(String serviceName) throws Exception{
        return Class.forName(serviceName);
    }
}
