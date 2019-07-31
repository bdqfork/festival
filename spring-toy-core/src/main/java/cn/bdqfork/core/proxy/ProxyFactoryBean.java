package cn.bdqfork.core.proxy;

import cn.bdqfork.core.aop.Advice;
import cn.bdqfork.core.container.BeanFactory;
import cn.bdqfork.core.exception.BeansException;

/**
 * @author bdq
 * @since 2019-07-30
 */
public class ProxyFactoryBean {
    private BeanFactory beanFactory;
    private String[] interceptorNames;
    private Object target;
    private Class<?>[] interfaces;


    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setInterceptorNames(String... interceptorNames) {
        this.interceptorNames = interceptorNames;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setInterfaces(Class<?>... proxyInterfaces) {
        this.interfaces = proxyInterfaces;
    }

    public Object getObject() throws BeansException {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        proxyFactory.setInterfaces(target.getClass().getInterfaces());
        for (String interceptorName : interceptorNames) {
            Advice advice = (Advice) beanFactory.getBean(interceptorName);
            proxyFactory.addAdvice(advice);
        }
        return proxyFactory.getProxy();
    }
}
