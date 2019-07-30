package cn.bdqfork.core.proxy;

import cn.bdqfork.core.aop.Advice;
import cn.bdqfork.core.aop.Advisor;
import cn.bdqfork.core.container.BeanContainer;

import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @since 2019-07-30
 */
public class ProxyFactoryBean {
    private BeanContainer beanContainer;
    private String[] interceptorNames;
    private Object target;
    private Class<?>[] interfaces;


    public void setBeanContainer(BeanContainer beanContainer) {
        this.beanContainer = beanContainer;
    }

    public void setInterceptorNames(String... interceptorNames) {
        this.interceptorNames = interceptorNames;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setProxyInterfaces(Class<?>... proxyInterfaces) {
        this.interfaces = proxyInterfaces;
    }

    public Object getObject() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        for (String interceptorName : interceptorNames) {
            Advice advice = (Advice) beanContainer.getBean(interceptorName);
            proxyFactory.addAdvice(advice);
        }
        return proxyFactory.getProxy();
    }
}
