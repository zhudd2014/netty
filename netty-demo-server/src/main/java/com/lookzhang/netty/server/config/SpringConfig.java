package com.lookzhang.netty.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


/**
 *
 * @author Jax
 * @version 2015/11/25.
 * @since 2015/10/15
 */
@Configuration
@Import({NettyConfig.class})
@ComponentScan(
    basePackages = "com.lookzhang.netty.server",//配置注解扫描的包路径
    useDefaultFilters = false,//关闭默认的过滤器
    includeFilters = {//配置过滤器扫描注解
        @Filter(type = FilterType.ANNOTATION, value = Component.class),
        @Filter(type = FilterType.ANNOTATION, value = Service.class)})
//因为spring4使用了jdk1.8的@Repeatable注解,jdk<1.8在编译的时候会产生一个警告,但是不会影响运行
@PropertySources(
    value = {
        //这是一个数组,如果有多个properties文件在这里增加@PropertySource属性
        @PropertySource("classpath:config.properties")})
public class SpringConfig {

    /**
     * 配合@PropertySources加载properties文件，并且通过@Value注解注入属性
     * 注意static关键字一定要加上，Spring启动的时候会优先执行static声明的方法，
     * 确保能正确注入@Value,@Autowired,@Resource,@PostConstruct注解声明的属性
     * http://www.java-allandsundry.com/2013/07/spring-bean-and-propertyplaceholderconf.html
     * @return PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
