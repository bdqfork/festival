package cn.bdqfork.aop.factory;

import cn.bdqfork.aop.proxy.AopProxySupport;

/**
 * @author bdq
 * @since 2019/12/27
 */
public interface AopProxyBeanFactory {

    Object createAopProxyBean(AopProxySupport config);

}
