package cn.bdqfork.core.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @date 2019-02-16
 */
public class MethodInvocation {
    private Object target;
    private Method method;
    private Object[] args;
    private MethodProxy proxy;

    public MethodInvocation(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    public MethodInvocation(Object target, Method method, Object[] args, MethodProxy proxy) {
        this.target = target;
        this.method = method;
        this.args = args;
        this.proxy = proxy;
    }

    public Object proceed() throws Throwable {
        if (proxy != null) {
            return proxy.invoke(target, args);
        } else {
            return method.invoke(target, args);
        }
    }

}
