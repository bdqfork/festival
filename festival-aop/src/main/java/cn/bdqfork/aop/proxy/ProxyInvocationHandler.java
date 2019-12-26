package cn.bdqfork.aop.proxy;

import cn.bdqfork.core.exception.BeansException;

/**
 * @author bdq
 * @since 2019/12/23
 */
public interface ProxyInvocationHandler {
    /**
     * 创建代理实例
     *
     * @return 代理实例
     * @throws BeansException bean异常
     */
    Object newProxyInstance() throws BeansException;

    /**
     * 设置目标实例
     *
     * @param target
     */
    void setTarget(Object target);

    /**
     * 设置代理类型
     *
     * @param interfaces 代理类型
     */
    void setInterfaces(Class<?>... interfaces);
}
