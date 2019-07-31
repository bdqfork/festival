package cn.bdqfork.core.proxy;

import cn.bdqfork.core.aop.Advisor;

import java.util.List;

/**
 * @author bdq
 * @since 2019-07-31
 */
public interface AdviceInvocationHandler extends AopInvocationHandler {

    void setAdvisors(List<Advisor> advisors);
}
