package com.lookzhang.netty.server;

import com.lookzhang.netty.server.config.SpringConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;


/**
 * 
 * @author Jax
 * @version 2015/11/23.
 */
public class Main {

    /**
     * @param args 参数
     */
    public static void main(String[] args) {
        AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(
            SpringConfig.class);
        ctx.registerShutdownHook();
    }
}
