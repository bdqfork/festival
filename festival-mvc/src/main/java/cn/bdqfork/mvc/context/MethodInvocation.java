package cn.bdqfork.mvc.context;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/26
 */
public class MethodInvocation implements Serializable {
    private String methodName;
    private Class<?>[] argumentClasses;
    private Object[] arguments;

    public MethodInvocation() {
    }

    public MethodInvocation(Method method, Object[] arguments) {
        this.methodName = method.getName();
        this.argumentClasses = method.getParameterTypes();
        this.arguments = arguments;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getArgumentClasses() {
        return argumentClasses;
    }

    public void setArgumentClasses(Class<?>[] argumentClasses) {
        this.argumentClasses = argumentClasses;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
}
