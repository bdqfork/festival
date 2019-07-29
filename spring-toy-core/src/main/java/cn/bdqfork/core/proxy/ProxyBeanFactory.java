package cn.bdqfork.core.proxy;

import cn.bdqfork.core.aop.PointcutAdvisor;
import cn.bdqfork.core.container.BeanFactory;

import java.util.List;

/**
 * @author bdq
 * @since 2019-07-29
 */
public class ProxyBeanFactory {
    private Object target;
    private List<PointcutAdvisor> pointcutAdvisors;
    private BeanFactory beanFactory;

    public ProxyBeanFactory(Object target, List<PointcutAdvisor> pointcutAdvisors, BeanFactory beanFactory) {
        this.target = target;
        this.pointcutAdvisors = pointcutAdvisors;
        this.beanFactory = beanFactory;
    }

    public Object getProxyBean() {
        Class<?>[] classes = beanFactory.getBeanDefinition().getClazz().getInterfaces();
        Object proxyInstance;
        if (classes.length != 0) {
            JdkInvocationHandler jdkInvocationHandler = new JdkInvocationHandler(beanFactory);
            proxyInstance = jdkInvocationHandler.newProxyInstance();
        } else {
            CglibMethodInterceptor cglibMethodInterceptor = new CglibMethodInterceptor(beanFactory);
            proxyInstance = cglibMethodInterceptor.newProxyInstance();
        }
        return proxyInstance;
    }
}
