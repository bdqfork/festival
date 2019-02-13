package cn.bdqfork.ioc.container;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author bdq
 * @date 2019-02-13
 */
public class MethodInjectorAttribute {
    private Method method;
    private List<ParameterInjectorData> parameterInjectorDatas;

    public MethodInjectorAttribute(Method method, List<ParameterInjectorData> parameterInjectorDatas) {
        this.method = method;
        this.parameterInjectorDatas = parameterInjectorDatas;
    }

    public Method getMethod() {
        return method;
    }

    public List<ParameterInjectorData> getParameterInjectorDatas() {
        return parameterInjectorDatas;
    }
}
