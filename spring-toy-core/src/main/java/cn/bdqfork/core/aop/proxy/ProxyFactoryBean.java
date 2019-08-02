package cn.bdqfork.core.aop.proxy;

import cn.bdqfork.core.aop.Advice;
import cn.bdqfork.core.container.FactoryBean;
import cn.bdqfork.core.exception.BeansException;

/**
 * FactoryBean，代理实例直接注入到BeanFactory
 *
 * @author bdq
 * @since 2019-07-30
 */
public class ProxyFactoryBean implements FactoryBean<Object> {
    /**
     * 目标实例
     */
    private Object target;
    /**
     * 代理类型
     */
    private Class<?>[] interfaces;
    /**
     * 代理工厂
     */
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

    /**
     * 添加通知
     *
     * @param advice 通知
     */
    public void addAdvice(Advice advice) {
        proxyFactory.addAdvice(advice);
    }

}
