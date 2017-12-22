package com.lookzhang.netty.server.config;

import com.lookzhang.fsof.remoting.server.SimpleTcpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;


/**
 *
 * @author Jax
 * @version 2015/11/23.
 */
@Configuration
public class NettyConfig {

    /**
     * port
     */
    @Value("${tcp.port}")
    private int tcpPort;

    /**
     * spring context
     */
    @Autowired
    private AbstractApplicationContext context;

    /**
     * 创建tcp服务并且启动
     * @return SimpleTcpServer
     * @throws Exception exception
     */
    @Bean
    public SimpleTcpServer simpleTcpServer()
        throws Exception {
        //创建Server对象
        SimpleTcpServer simpleTcpServer = new SimpleTcpServer();
        //设置spring上下文
        simpleTcpServer.setApplicationContext(context);
        //设置服务占用端口号
        simpleTcpServer.setTcpPort(tcpPort);

        /**
         注释中的配置项，请根据自己的业务需要来设置
         ServerInitializerConfig serverInitializerConfig = new ServerInitializerConfig();
         //设置业务线程处理超时时间(毫秒)，如果超时会强制中断，如果不设置默认是10s
         serverInitializerConfig.setThreadTimeout(10000);
         //客户端3分钟不写消息过来就断开连接
         serverInitializerConfig.getIdleStateConfig().setReaderIdleTimeSeconds(180);
         simpleTcpServer.setServerInitializerConfig(serverInitializerConfig);
         */

        //启动server
        simpleTcpServer.afterPropertiesSet();
        return simpleTcpServer;
    }

}
