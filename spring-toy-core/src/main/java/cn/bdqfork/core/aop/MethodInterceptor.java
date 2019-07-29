package cn.bdqfork.core.aop;

/**
 * @author bdq
 * @since 2019-07-29
 */
public interface MethodInterceptor extends Advice {
    Object invoke(MethodInvocation invocation) throws Throwable;
}
