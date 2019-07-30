package cn.bdqfork.core.proxy;

import cn.bdqfork.core.aop.*;
import cn.bdqfork.core.aop.aspect.AspectAdvice;
import cn.bdqfork.core.aop.aspect.AspectAdvisor;
import cn.bdqfork.core.container.BeanFactory;

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
        this(null);
    }

    public ProxyFactory(BeanFactory beanFactory) {
        this.advisors = new LinkedList<>();
        this.beanFactory = beanFactory;
    }

    public Object getProxy() {
        Class<?>[] classes;
        if (target != null) {
            classes = target.getClass().getInterfaces();
        } else {
            classes = beanFactory.getBeanDefinition().getClazz().getInterfaces();
        }
        Object proxyInstance;
        if (classes.length != 0) {
            JdkInvocationHandler jdkInvocationHandler;
            if (target != null) {
                jdkInvocationHandler = new JdkInvocationHandler(target);
            } else {
                jdkInvocationHandler = new JdkInvocationHandler(beanFactory);
            }
            jdkInvocationHandler.setAdvisors(advisors);
            proxyInstance = jdkInvocationHandler.newProxyInstance();
        } else {
            CglibMethodInterceptor cglibMethodInterceptor = new CglibMethodInterceptor(beanFactory);
            proxyInstance = cglibMethodInterceptor.newProxyInstance();
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
}
