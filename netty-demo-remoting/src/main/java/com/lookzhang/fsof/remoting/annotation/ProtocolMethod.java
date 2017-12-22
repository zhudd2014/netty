package com.lookzhang.fsof.remoting.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 协议方法注解
 * Copyright by lookzhang
 * @author Jax
 * @version 2015/11/20.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProtocolMethod {
    String name() default "";
    String mode() default "";
}
