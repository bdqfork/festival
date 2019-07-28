package cn.bdqfork.core.container;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 注入方法描述
 *
 * @author bdq
 * @date 2019-07-27
 */
public class MethodAttribute {
    /**
     * 注入方法
     */
    private Method method;
    /**
     * 方法参数描述
     */
    private List<ParameterAttribute> args;
    /**
     * 是否强制需要
     */
    private boolean isRequired;

    public MethodAttribute(Method method, List<ParameterAttribute> args, boolean isRequired) {
        this.method = method;
        this.args = args;
        this.isRequired = isRequired;
    }

    public Method getMethod() {
        return method;
    }

    public List<ParameterAttribute> getArgs() {
        return args;
    }

    public boolean isRequired() {
        return isRequired;
    }

}
