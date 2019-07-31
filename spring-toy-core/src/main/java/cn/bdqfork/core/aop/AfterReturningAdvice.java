package cn.bdqfork.core.aop;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-29
 */
public interface AfterReturningAdvice extends AfterAdvice {
    void afterReturning(Object returnValue, Method method, Object[] args,
                        Object target) throws Throwable;
}
