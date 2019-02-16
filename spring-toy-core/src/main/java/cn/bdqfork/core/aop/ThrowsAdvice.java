package cn.bdqfork.core.aop;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @date 2019-02-16
 */
public interface ThrowsAdvice extends Advice {
    void afterThrowing(Method method, Object[] args, Object target, Exception e);
}
