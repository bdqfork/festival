package cn.bdqfork.aop.proxy.javassist;

import cn.bdqfork.aop.proxy.AopProxySupport;
import cn.bdqfork.aop.proxy.DefaultProxyInvocationHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class JavassistInvocationHandler extends DefaultProxyInvocationHandler implements InvocationHandler {

    public JavassistInvocationHandler(AopProxySupport support) {
        super(support);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invoke(method, args);
    }

}
