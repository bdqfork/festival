package cn.bdqfork.core.proxy;

import cn.bdqfork.core.aop.*;
import cn.bdqfork.core.aop.aspect.AspectAdvice;
import cn.bdqfork.core.aop.aspect.AspectAdvisor;
import cn.bdqfork.core.container.BeanFactory;
import cn.bdqfork.core.exception.BeansException;

import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @since 2019-07-29
 */
public class ProxyFactory {
    private Object target;
    private List<Advisor> advisors;
    private BeanFactory beanFactory;
    private Class<?>[] interfaces;

    public ProxyFactory() {
        this.advisors = new LinkedList<>();
    }

    public Object getProxy() throws BeansException {
        Object proxyInstance;
        if (interfaces.length != 0) {
            JdkInvocationHandler jdkInvocationHandler = new JdkInvocationHandler();
            jdkInvocationHandler.setTarget(target);
            jdkInvocationHandler.setInterfaces(interfaces);
            jdkInvocationHandler.setAdvisors(advisors);
            proxyInstance = jdkInvocationHandler.newProxyInstance();
        } else {
            CglibMethodInterceptor cglibMethodInterceptor = new CglibMethodInterceptor();
            cglibMethodInterceptor.setTarget(target);
            cglibMethodInterceptor.setAdvisors(advisors);
            proxyInstance = cglibMethodInterceptor.newProxyInstance();
        }
        if (beanFactory != null) {
            beanFactory.getBeans(Advice.class).forEach((beanName, advice) -> addAdvice((Advice) advice));
        }
        return proxyInstance;
    }

    public void addAdvice(Advice advice) {
        if (advice instanceof Advisor) {
            advisors.add((Advisor) advice);
            return;
        }
        Advisor advisor;
        if (advice instanceof AspectAdvice) {
            advisor = new AspectAdvisor(".*", (AspectAdvice) advice);
        } else {
            advisor = new RegexpMethodAdvisor(".*", advice);
        }
        advisors.add(advisor);
    }

    public void setInterfaces(Class<?>[] interfaces) {
        this.interfaces = interfaces;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
