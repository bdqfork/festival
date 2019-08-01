package cn.bdqfork.core.proxy;

import cn.bdqfork.core.aop.Advice;
import cn.bdqfork.core.container.FactoryBean;
import cn.bdqfork.core.exception.BeansException;

import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @since 2019-07-30
 */
public class ProxyFactoryBean implements FactoryBean<Object> {
    private Object target;
    private Class<?>[] interfaces;
    private ProxyFactory proxyFactory;


    public ProxyFactoryBean() {
        proxyFactory = new ProxyFactory();
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setInterfaces(Class<?>... proxyInterfaces) {
        this.interfaces = proxyInterfaces;
    }

    @Override
    public Object getObject() throws BeansException {
        proxyFactory.setTarget(target);
        proxyFactory.setInterfaces(target.getClass().getInterfaces());
        if (interfaces != null) {
            if (!(interfaces.length == 1 && interfaces[0] == target.getClass())) {
                proxyFactory.setInterfaces(interfaces);
            }
        }
        return proxyFactory.getProxy();
    }

    @Override
    public Class<?> getObjectType() {
        return target.getClass();
    }

    public void addAdvice(Advice advice) {
        proxyFactory.addAdvice(advice);
    }

}
