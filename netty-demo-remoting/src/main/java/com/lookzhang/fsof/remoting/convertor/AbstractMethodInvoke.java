package com.lookzhang.fsof.remoting.convertor;

import com.lookzhang.fsof.remoting.JsonBodyConsumer;
import com.lookzhang.fsof.remoting.JsonBodyProvider;
import com.lookzhang.fsof.remoting.ProtocolMessage;
import com.lookzhang.fsof.remoting.annotation.ProtocolMethod;
import com.lookzhang.fsof.remoting.util.TypeConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;


/**
 * 根据协议消息反射方法
 *
 * @author Jax
 * @version 2015/11/20.
 */
@Slf4j
public abstract class AbstractMethodInvoke {

    /**
     * 反射参数辅助类
     */
    private LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new
        LocalVariableTableParameterNameDiscoverer();

    /**
     * 根据协议消息内容反射方法
     *
     * @param jsonBodyConsumer consumer包体解析到的数据
     * @return JsonBodyProvider 返回provider协议消息体
     * @throws MethodInvokeException 自定义方法反射异常
     */
    public JsonBodyProvider invokeMethod(JsonBodyConsumer jsonBodyConsumer)
        throws MethodInvokeException {
        try {
            JsonBodyProvider jsonBodyProvider = new JsonBodyProvider(jsonBodyConsumer);
            Object service = null;
            String serviceName = jsonBodyConsumer.getService();
            if (StringUtils.isBlank(serviceName)) {
                throw new MethodInvokeException("service为空");
            }
            String methodName = jsonBodyConsumer.getMethod();
            if (StringUtils.isBlank(methodName)) {
                throw new MethodInvokeException("method为空");
            }
            //通过类别名来取类
            service = getClassObject(serviceName);
            if (service == null) {
                throw new MethodInvokeException("service对象不存在");
            }
            //从service对象中查找方法
            Method[] methods = service.getClass().getDeclaredMethods();
            Method callMethod = null;
            String methodMode = "";
            for (Method method : methods) {
                ProtocolMethod protocolMethod = method.getAnnotation(ProtocolMethod.class);
                if (protocolMethod != null) {
                    if (StringUtils.equals(protocolMethod.name(), methodName)) {
                        log.debug("找到方法:" + methodName);
                        callMethod = method;
                        methodMode = protocolMethod.mode();
                        break;
                    }

                }
            }
            if (callMethod == null) {
                StringBuilder stringBuilder = new StringBuilder("方法").append(methodName).append(
                    "不存在");
                throw new MethodInvokeException(stringBuilder.toString());
            }
            //反射参数数组
            Object[] args = null;
            Class[] parameterTypes = callMethod.getParameterTypes();

            if (parameterTypes.length == 0) {//如果没有参数
                Object result = callMethod.invoke(service);
                jsonBodyProvider.setResult(result);
                /**
                 * @TODO 兼容旧接口类型ProtocolMessage，后面应当全部改造不使用ProtocolMessage返回类型
                 */
                if (callMethod.getReturnType() == ProtocolMessage.class) {
                    ProtocolMessage protocolMessage = (ProtocolMessage)result;
                    jsonBodyProvider.setResult(protocolMessage.getJson());
                    return jsonBodyProvider;
                }
                return jsonBodyProvider;
            }

            //下面处理有参数的情况

            //旧模式Map
            if (StringUtils.equals(methodMode, "old")) {
                jsonBodyProvider.setOldMethodMode(true);
                @SuppressWarnings("unchecked") Map<String, Object> params = (Map<String,
                    Object>)jsonBodyConsumer.getParams();
                if (params == null) {
                    throw new MethodInvokeException("params为空");
                }

                String[] paramsNames = parameterNameDiscoverer.getParameterNames(callMethod);
                args = new Object[parameterTypes.length];
                for (int i = 0; i < paramsNames.length; i++) {
                    String paramName = paramsNames[i];
                    Class parameterType = parameterTypes[i];
                    if (params.containsKey(paramName)) {
                        args[i] = TypeConvertUtil.convert(params.get(paramName), parameterType);
                    }
                    else {
                        //参数不存在
                        StringBuilder stringBuilder = new StringBuilder("参数").append(
                            paramName).append("不存在");
                        throw new MethodInvokeException(stringBuilder.toString());
                    }
                }
            }

            //默认数组模式
            if (StringUtils.equals(methodMode, StringUtils.EMPTY)) {
                List<Object> array = (List<Object>)jsonBodyConsumer.getParams();
                if (parameterTypes.length != array.size()) {
                    throw new MethodInvokeException("缺少参数");
                }
                args = array.toArray();
                //                args = new Object[array.size()];
                //                for(int i = 0; i < array.size(); i++){
                //                    Object paramValue = array[i];//参数值
                //                    Class parameterType = parameterTypes[i];//参数类型
                //                    args[i] = TypeConvertUtil.convert(paramValue,
                // parameterType);//转换类型
                //                }
            }

            Object result = callMethod.invoke(service, args);

            /**
             * @TODO 兼容旧接口类型ProtocolMessage，后面应当全部改造不使用ProtocolMessage返回类型
             */
            if (callMethod.getReturnType() == ProtocolMessage.class) {
                ProtocolMessage protocolMessage = (ProtocolMessage)result;
                jsonBodyProvider.setResult(protocolMessage.getJson());
                return jsonBodyProvider;
            }

            jsonBodyProvider.setResult(result);
            return jsonBodyProvider;
        }
        catch (Exception e) {
            throw new MethodInvokeException(e);
        }
    }

    /**
     * 重写此方法来定制加载类的方式
     * @param serviceName service名
     * @return Object service对象
     * @throws Exception 加载失败抛出异常
     */
    public abstract Object getClassObject(String serviceName)
        throws Exception;

}
