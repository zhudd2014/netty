package com.lookzhang.fsof.remoting;

/**
 * provider消息体对象
 * @author Jax
 * @version 2015/11/20.
 */
public class JsonBodyProvider {

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
     * provider返回结果
     */
    private Object result;

    /**
     * 兼容旧协议参数
     */
    private Boolean oldMethodMode;

    public JsonBodyProvider() {
    }

    public JsonBodyProvider(JsonBodyConsumer jsonBodyConsumer){
        this.service = jsonBodyConsumer.getService();
        this.environment = jsonBodyConsumer.getEnvironment();
        this.group = jsonBodyConsumer.getGroup();
        this.version = jsonBodyConsumer.getVersion();
        this.method = jsonBodyConsumer.getMethod();
    }

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

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Boolean getOldMethodMode() {
        return oldMethodMode;
    }

    public void setOldMethodMode(Boolean oldMethodMode) {
        this.oldMethodMode = oldMethodMode;
    }

    @Override
    public String toString() {
        return "JsonBodyProvider{" +
                "service='" + service + '\'' +
                ", environment='" + environment + '\'' +
                ", group='" + group + '\'' +
                ", version='" + version + '\'' +
                ", method='" + method + '\'' +
                ", result=" + result +
                '}';
    }
}
