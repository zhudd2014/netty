package com.lookzhang.netty.client;


import com.lookzhang.fsof.remoting.JsonBodyConsumer;
import com.lookzhang.fsof.remoting.JsonBodyProvider;
import com.lookzhang.fsof.remoting.client.NettyClient;
import com.lookzhang.fsof.remoting.client.NettyClientFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;


/**
 * Created on 2015/11/3.
 *
 * @author Jax
 */
public class Client {

    /**
     * @param args 参数
     */
    public static void main(String[] args) {
        testArray();
    }

    /**
     * 默认调用方式
     */
    public static void testArray() {
        String ip = "127.0.0.1";
        int port = 8999;
        NettyClientFactory nettyClientFactory = new NettyClientFactory(ip, port);
        JsonBodyConsumer jsonBodyConsumer = new JsonBodyConsumer();
        jsonBodyConsumer.setService("helloService");
        jsonBodyConsumer.setMethod("test");
        List<String> params = new ArrayList<String>();
        params.add("111");
        params.add("222");
        jsonBodyConsumer.setParams(params);
        NettyClient client = null;
        try {
            client = nettyClientFactory.create();
            //设置超时时间5s
            JsonBodyProvider provider = client.sendSync(5000, jsonBodyConsumer);
            if (provider != null) {
                System.out.println(provider.getResult());
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (TimeoutException e) {
            e.printStackTrace(); //超时
        }
        finally {
            client = null;
        }
    }

    /**
     * 数组里传map的方式
     */
    public static void testMap() {
        String ip = "127.0.0.1";
        int port = 8999;
        NettyClientFactory nettyClientFactory = new NettyClientFactory(ip, port);
        JsonBodyConsumer jsonBodyConsumer = new JsonBodyConsumer();
        jsonBodyConsumer.setService("helloService");
        jsonBodyConsumer.setMethod("testMap");
        List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key1", "111");
        map.put("key2", "222");
        params.add(map);
        jsonBodyConsumer.setParams(params);
        NettyClient client = null;
        try {
            client = nettyClientFactory.create();
            //设置超时时间5s
            JsonBodyProvider provider = client.sendSync(5000, jsonBodyConsumer);
            if (provider != null) {
                System.out.println(provider.getResult());
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (TimeoutException e) {
            e.printStackTrace(); //超时
        }
        finally {
            client = null;
        }
    }
}
