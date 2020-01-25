package cn.bdqfork.aop.proxy.javassist;

import cn.bdqfork.aop.proxy.AopProxy;
import cn.bdqfork.aop.proxy.AopProxySupport;
import cn.bdqfork.aop.proxy.TargetClassAware;

import java.util.Arrays;
import java.util.List;

/**
 * @author bdq
 * @since 2020/1/14
 */
public class JavassistProxy implements AopProxy {
    private AopProxySupport support;

    public JavassistProxy(AopProxySupport support) {
        this.support = support;
    }

    @Override
    public Object getProxy() {
        return getProxy(null);
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        Class<?> targetClass = support.getBeanClass();
        if (classLoader == null) {
            classLoader = targetClass.getClassLoader();
        }
        return Proxy.newProxyInstance(classLoader, support.getInterfaces().toArray(new Class[0]), new JavassistInvocationHandler(support));
    }
}
