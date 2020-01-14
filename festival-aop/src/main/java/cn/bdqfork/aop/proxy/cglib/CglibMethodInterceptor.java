package cn.bdqfork.aop.proxy.cglib;

import cn.bdqfork.aop.proxy.AopProxySupport;
import cn.bdqfork.aop.proxy.DefaultProxyInvocationHandler;
import net.sf.cglib.proxy.*;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class CglibMethodInterceptor extends DefaultProxyInvocationHandler implements MethodInterceptor {

    public CglibMethodInterceptor(AopProxySupport support) {
        super(support);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return invoke(method, args);
    }

}
