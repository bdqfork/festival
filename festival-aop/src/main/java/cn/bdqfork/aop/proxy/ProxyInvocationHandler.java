package cn.bdqfork.aop.proxy;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/23
 */
public interface ProxyInvocationHandler {

    Object invoke(Method method, Object[] args) throws Throwable;

}
