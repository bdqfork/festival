package cn.bdqfork.aop.proxy;

import cn.bdqfork.aop.advice.*;
import cn.bdqfork.core.exception.BeansException;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/23
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
     * 设置目标实例
     *
     * @param target
     */
    void setTarget(Object target);

    /**
     * 设置代理类型
     *
     * @param interfaces 代理类型
     */
    void setInterfaces(Class<?>... interfaces);

    void addAdvice(String fullyMethodName, MethodBeforeAdvice[] advice);

    void addAdvice(String fullyMethodName, AroundAdvice[] advice);

    void addAdvice(String fullyMethodName, AfterReturningAdvice[] advice);

    void addAdvice(String fullyMethodName, ThrowsAdvice[] advice);

    /**
     * 执行代理方法以及通知方法
     *
     * @param target 代理实例
     * @param method 代理方法
     * @param args   代理方法参数
     * @return Object 执行结果
     * @throws Throwable 异常
     */
    Object invokeWithAdvice(Object target, Method method, Object[] args) throws Throwable;
}
