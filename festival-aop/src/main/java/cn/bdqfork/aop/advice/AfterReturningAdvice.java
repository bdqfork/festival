package cn.bdqfork.aop.advice;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/23
 */
public interface AfterReturningAdvice extends AfterAdvice {
    /**
     * 执行后置通知
     *
     * @param returnValue 代理方法返回值
     * @param method      代理方法
     * @param args        代理方法参数
     * @param target      代理目标类
     * @throws Throwable 异常
     */
    void afterReturning(Object returnValue, Method method, Object[] args,
                        Object target) throws Throwable;
}
