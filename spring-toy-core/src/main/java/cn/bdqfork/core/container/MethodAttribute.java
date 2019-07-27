package cn.bdqfork.core.container;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * @author bdq
 * @date 2019-07-27
 */
public class MethodAttribute {
    private Method method;
    private List<ParameterAttribute> args;
    private boolean required;

    public MethodAttribute(Method method, List<ParameterAttribute> args, boolean required) {
        this.method = method;
        this.args = args;
        this.required = required;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public List<ParameterAttribute> getArgs() {
        return args;
    }

    public void setArgs(List<ParameterAttribute> args) {
        this.args = args;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
