package com.lookzhang.fsof.remoting;

/**
 * consumer消息体对象
 * @author Jax
 * @version 2015/11/20.
 */
public class JsonBodyConsumer {

    /**
     * 服务名
     */
    private String service;

    /**
     * 环境
     */
    private String environment;

    /**
     * 分组
     */
    private String group;

    /**
     * 版本号
     */
    private String version;

    /**
     * 方法名
     */
    private String method;

    /**
     * 参数
     */
    private Object params;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "JsonBodyConsumer{" +
                "service='" + service + '\'' +
                ", environment='" + environment + '\'' +
                ", group='" + group + '\'' +
                ", version='" + version + '\'' +
                ", method='" + method + '\'' +
                ", params='" + params + '\'' +
                '}';
    }
}
