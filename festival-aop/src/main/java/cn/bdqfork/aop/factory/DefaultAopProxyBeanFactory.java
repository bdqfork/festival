package cn.bdqfork.aop.factory;

import cn.bdqfork.aop.proxy.AopProxySupport;
import cn.bdqfork.aop.proxy.TargetClassAware;
import cn.bdqfork.aop.proxy.cglib.CglibProxy;
import cn.bdqfork.aop.proxy.javassist.JavassistProxy;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class DefaultAopProxyBeanFactory implements AopProxyBeanFactory {

    @Override
    public Object createAopProxyBean(AopProxySupport config) {
        if (!config.isOptimze() && config.getInterfaces() != null && config.getInterfaces().size() > 0) {
            return new JavassistProxy(config).getProxy();
        }
        return new CglibProxy(config).getProxy();
    }

}
