package cn.bdqfork.core.aop;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-29
 */
public interface ThrowsAdvice extends Advice {
    void afterThrowing(Method method, Object[] args, Object target, Exception ex);
}
