package cn.bdqfork.aop.proxy;

import cn.bdqfork.aop.advice.Advice;
import cn.bdqfork.aop.advice.Advisor;
import cn.bdqfork.aop.advice.AspectAdvice;
import cn.bdqfork.aop.advice.AspectAdvisor;
import cn.bdqfork.aop.advice.RegexpMethodAdvisor;
import cn.bdqfork.aop.proxy.cglib.CglibMethodInterceptor;
import cn.bdqfork.aop.proxy.javassist.JavassistInvocationHandler;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class ProxyFactory {
    /**
     * 目标实例
     */
    private Object target;
    /**
     * 顾问
     */
    private List<Advisor> advisors;
    /**
     * Bean工厂，用于获取Aspect通知
     */
    private BeanFactory beanFactory;
    /**
     * 代理类型
     */
    private Class<?>[] interfaces;

    public ProxyFactory() {
        this.advisors = new LinkedList<>();
    }

    /**
     * 获取代理实例
     *
     * @return Object 代理实例
     * @throws BeansException bean异常
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy() throws BeansException {

        //AdvisorInvocationHandler advisorInvocationHandler = getAdviceInvocationHandler();

        return (T) createProxyInstance();
    }

    private Object createProxyInstance() throws BeansException {
        ProxyInvocationHandler invocationHandler;
        //实例化代理生成类
        if (interfaces != null && interfaces.length > 0) {
            invocationHandler = new JavassistInvocationHandler();
        } else {
            invocationHandler = new CglibMethodInterceptor();
        }

        invocationHandler.setTarget(target);
        invocationHandler.setInterfaces(interfaces);

        return invocationHandler.newProxyInstance();
    }

    private AdvisorInvocationHandler getAdviceInvocationHandler() {
        AdvisorInvocationHandler advisorInvocationHandler = new AdvisorInvocationHandlerImpl();
        if (advisors.size() > 0) {
            advisorInvocationHandler.setAdvisors(advisors);
        }
        if (beanFactory != null) {
            //从BeanFactory获取Aspect通知
//            AdvisorBeanFactoryImpl advisorBeanFactoryImpl = (AdvisorBeanFactoryImpl) beanFactory;
//            advisorInvocationHandler.setAdvisors(advisorBeanFactoryImpl.getAdvisors());
        }
        return advisorInvocationHandler;
    }

    /**
     * 添加通知
     *
     * @param advice 通知
     */
    public void addAdvice(Advice advice) {
        if (advice instanceof Advisor) {
            advisors.add((Advisor) advice);
            return;
        }
        Advisor advisor = new RegexpMethodAdvisor();
        if (advice instanceof AspectAdvice) {
            advisor = new AspectAdvisor();
        }
        advisor.setPointcut(".*");
        advisor.setAdvice(advice);
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
