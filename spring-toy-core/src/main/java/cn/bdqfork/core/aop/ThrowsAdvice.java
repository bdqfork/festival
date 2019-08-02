package cn.bdqfork.core.aop;

import java.lang.reflect.Method;

/**
 * 异常通知
 *
 * @author bdq
 * @since 2019-07-29
 */
public interface ThrowsAdvice extends Advice {
    /**
     * 执行异常通知，如果执行过程中出异常，会抛出runtime异常
     *
     * @param method 代理方法
     * @param args   代理方法参数
     * @param target 代理目标类
     * @param ex     异常实例
     */
    void afterThrowing(Method method, Object[] args, Object target, Exception ex);
}
