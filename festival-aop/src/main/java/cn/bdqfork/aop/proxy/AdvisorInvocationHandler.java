package cn.bdqfork.aop.proxy;

import cn.bdqfork.aop.advice.Advisor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author bdq
 * @since 2019/12/23
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
