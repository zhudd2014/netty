package com.lookzhang.netty.server.service;

import com.lookzhang.fsof.remoting.annotation.ProtocolMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2015/10/16.
 *
 * @author Jax
 */
@Service("helloService")//Provider暴露出去的service名称
public class HelloService {

    /**
     * 注意这里参数在consumer那边调用的时候是个数组，要按顺序传
     * @param param1 参数1
     * @param param2 参数2
     * @return Map<String,Object>
     * @throws InterruptedException exception
     */
    @ProtocolMethod(name = "test")
    public Map<String,Object> test(String param1, String param2) throws InterruptedException {
        System.out.println(param1);
        System.out.println(param2);
        Map<String,Object> result = new HashMap<String, Object>();
        result.put("code",0);
        result.put("msg","hello");
        return result;
    }

    /**
     * 参数数组里面放map
     * @param map 参数
     * @return Map<String,Object>
     */
    @ProtocolMethod(name = "testMap")
    public Map<String,Object> testMap(Map<String,String> map){
        System.out.println(map);
        Map<String,Object> result = new HashMap<String, Object>();
        result.put("code",0);
        result.put("msg","hello Map");
        return result;
    }

    /**
     * 无参数调用
     * @return Map<String,Object>
     */
    @ProtocolMethod(name = "testMethodEmptyParams")
    public Map<String,Object> testMethodEmptyParams(){
        Map<String,Object> result = new HashMap<String, Object>();
        result.put("code",0);
        result.put("msg","hello");
        return result;
    }

    /**
     * 测试运行时异常
     * @return Map<String,Object>
     */
    @ProtocolMethod(name = "testException")
    public Map<String,Object> testException(){
        throw new RuntimeException("异常");
    }
}
