package cn.bdqfork.aop.factory;

import cn.bdqfork.aop.proxy.AopProxySupport;
import cn.bdqfork.core.proxy.TargetClassAware;
import cn.bdqfork.core.util.AopUtils;
import org.junit.Test;

import javax.inject.Named;

import java.util.ArrayList;
import java.util.List;

public class DefaultAopProxyBeanFactoryTest {

    @Test
    public void createAopProxyBean() {
        AopProxyBeanFactory aopProxyBeanFactory = new DefaultAopProxyBeanFactory();
        AopProxySupport support = new AopProxySupport();
        ProxyInterface proxyBean = new ProxyBean();
        support.setBean(proxyBean);
        support.setBeanClass(ProxyBean.class);

        List<Class<?>> interfaces = new ArrayList<>();
        interfaces.add(ProxyInterface.class);
        support.setInterfaces(interfaces);

        proxyBean = (ProxyInterface) aopProxyBeanFactory.createAopProxyBean(support);
        assert proxyBean instanceof TargetClassAware;

        TargetClassAware targetClassAware = (TargetClassAware) proxyBean;
        assert targetClassAware.getTargetClass().isAnnotationPresent(Named.class);

        assert AopUtils.isProxy(proxyBean);
    }

    public interface ProxyInterface {

    }

    @Named
    static class ProxyBean implements ProxyInterface {

    }

}