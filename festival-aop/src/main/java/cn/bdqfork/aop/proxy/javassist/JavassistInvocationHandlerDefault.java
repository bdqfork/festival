package cn.bdqfork.aop.proxy.javassist;

import cn.bdqfork.aop.proxy.AbstractAopInvocationHandler;
import cn.bdqfork.aop.proxy.AbstractProxyInvocationHandler;
import cn.bdqfork.core.exception.BeansException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class JavassistInvocationHandlerDefault extends AbstractAopInvocationHandler implements InvocationHandler {

    /**
     * 创建代理实例
     *
     * @return Object 代理实例
     */
    @Override
    public Object newProxyInstance() throws BeansException {
        Class<?> targetClass = getTargetClass();
        return Proxy.newProxyInstance(targetClass.getClassLoader(), interfaces, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object targetObject = getTargetObject();
        Object result = invokeObjectMethod(targetObject, method, args);
        if (result == null) {
            result = invokeWithAdvice(targetObject, method, args);
        }
        return result;
    }

}
