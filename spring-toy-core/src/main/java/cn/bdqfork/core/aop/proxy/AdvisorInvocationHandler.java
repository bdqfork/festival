package cn.bdqfork.core.aop.proxy;

import cn.bdqfork.core.aop.advice.Advisor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 用于处理通知的执行
 *
 * @author bdq
 * @since 2019-07-31
 */
public interface AdvisorInvocationHandler {
    /**
     * 设置advisors
     *
     * @param advisors 所有顾问
     */
    void setAdvisors(List<Advisor> advisors);

    /**
     * 执行代理方法以及通知方法
     *
     * @param target 代理实例
     * @param method 代理方法
     * @param args   代理方法参数
     * @return Object 执行结果
     * @throws Throwable 异常
     */
    Object invokeWithAdvice(Object target, Method method, Object[] args) throws Throwable;
}
