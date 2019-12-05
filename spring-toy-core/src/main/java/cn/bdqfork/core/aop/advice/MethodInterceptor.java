package cn.bdqfork.core.aop.advice;

import cn.bdqfork.core.aop.MethodInvocation;

/**
 * 环绕通知
 *
 * @author bdq
 * @since 2019-07-29
 */
public interface MethodInterceptor extends Advice {
    /**
     * 执行环绕通知
     *
     * @param invocation 切点
     * @return Object 方法执行结果
     * @throws Throwable 异常
     */
    Object invoke(MethodInvocation invocation) throws Throwable;
}
