package cn.bdqfork.core.aop.proxy;

import cn.bdqfork.core.exception.BeansException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk动态代理
 *
 * @author bdq
 * @since 2019-02-13
 */
public class JdkInvocationHandler extends AbstractProxyInvocationHandler implements InvocationHandler {
    /**
     * 顾问处理
     */
    private AdvisorInvocationHandler advisorInvocationHandler;

    public JdkInvocationHandler(AdvisorInvocationHandler advisorInvocationHandler) {
        this.advisorInvocationHandler = advisorInvocationHandler;
    }

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
            result = advisorInvocationHandler.invokeWithAdvice(targetObject, method, args);
        }
        return result;
    }

}
