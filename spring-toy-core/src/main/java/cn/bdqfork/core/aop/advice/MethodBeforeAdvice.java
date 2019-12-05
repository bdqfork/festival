package cn.bdqfork.core.aop.advice;

import java.lang.reflect.Method;

/**
 * 前置通知
 *
 * @author bdq
 * @since 2019-07-29
 */
public interface MethodBeforeAdvice extends BeforeAdvice {
    /**
     * 执行前置通知
     *
     * @param method 代理方法
     * @param args   代理方法参数
     * @param target 代理目标类
     * @throws Throwable 异常
     */
    void before(Method method, Object[] args, Object target) throws Throwable;
}
