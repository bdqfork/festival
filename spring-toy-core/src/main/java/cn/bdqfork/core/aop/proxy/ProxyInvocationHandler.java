package cn.bdqfork.core.aop.proxy;

import cn.bdqfork.core.exception.BeansException;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-31
 */
public interface ProxyInvocationHandler {
    /**
     * 创建代理实例
     *
     * @return 代理实例
     * @throws BeansException bean异常
     */
    Object newProxyInstance() throws BeansException;

    /**
     * 执行代理方法
     *
     * @return 执行结果
     * @throws BeansException bean异常
     */
    Object invoke(Method method, Object[] args) throws Throwable;

    void setTarget(Object target);

    void setInterfaces(Class<?>... interfaces);
}
