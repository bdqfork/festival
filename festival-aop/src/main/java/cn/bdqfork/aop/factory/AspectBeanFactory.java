package cn.bdqfork.aop.factory;

import cn.bdqfork.aop.proxy.ProxyFactory;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanDefinition;
import cn.bdqfork.core.factory.DefaultJSR250BeanFactory;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class AspectBeanFactory extends DefaultJSR250BeanFactory {
    public static final String PREFIX = "$";

    public AspectBeanFactory() {
        super();
    }

    @Override
    protected Object doCreateBean(String beanName, BeanDefinition beanDefinition, Object[] explicitArgs) throws BeansException {
        Object instance = super.doCreateBean(beanName, beanDefinition, explicitArgs);
        if (instance == null) {
            return null;
        }
        if (beanName.startsWith(PREFIX)) {
            return instance;
        }
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(instance);
        proxyFactory.setInterfaces(beanDefinition.getBeanClass().getInterfaces());
        return proxyFactory.getProxy();
    }

}
