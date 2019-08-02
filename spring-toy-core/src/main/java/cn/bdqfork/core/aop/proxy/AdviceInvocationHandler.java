package cn.bdqfork.core.aop.proxy;

import cn.bdqfork.core.aop.Advisor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author bdq
 * @since 2019-07-31
 */
public interface AdviceInvocationHandler {

    void setAdvisors(List<Advisor> advisors);

    Object invokeWithAdvice(Object target, Method method, Object[] args) throws Throwable;
}
