package cn.bdqfork.aop.advice;

import cn.bdqfork.aop.MethodInvocation;

/**
 * @author bdq
 * @since 2019/12/23
 */
public interface AroundAdvice extends Advice {
    /**
     * 执行环绕通知
     *
     * @param invocation 切点
     * @return Object 方法执行结果
     * @throws Throwable 异常
     */
    Object invoke(MethodInvocation invocation) throws Throwable;
}
