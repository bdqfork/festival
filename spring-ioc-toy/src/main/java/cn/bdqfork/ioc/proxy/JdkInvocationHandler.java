package cn.bdqfork.ioc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author bdq
 * @date 2019-02-13
 */
public class JdkInvocationHandler implements InvocationHandler {
    private Object target;

    /**
     * 创建代理实例
     *
     * @param target
     * @return
     */
    public Object newProxyInstance(Object target) {
        this.target = target;
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(target, args);
    }
}
