package cn.bdqfork.core.aop;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @date 2019-02-16
 */
public interface MethodBeforeAdvice extends Advice {
    void before(Method method, Object[] args, Object target) throws Throwable;
}
