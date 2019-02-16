package cn.bdqfork.core.aop;

/**
 * @author bdq
 * @date 2019-02-16
 */
public interface MethodAroundAdvice extends Advice {
    Object invoke(MethodInvocation invocation) throws Throwable;
}
