package cn.bdqfork.aop.factory;

import cn.bdqfork.aop.advice.*;
import cn.bdqfork.core.exception.BeansException;

import java.util.Map;
import java.util.Set;

/**
 * @author bdq
 * @since 2019/12/27
 */
public interface AopProxyBeanFactory {

    void registerAdvisor(Advisor advisor) throws BeansException;

    Object getAopProxyInstance(String beanName, Object bean, Class<?>[] interfaces) throws BeansException;

    /**
     * 解析advice
     *
     * @param beanName
     * @param beanClass
     * @return Map<Advisor> method name and advisor instance entry
     */
    Map<String, Set<MethodBeforeAdvice>> resolveBeforeAdvice(String beanName, Class<?> beanClass);

    Map<String, Set<AroundAdvice>> resolveAroundAdvice(String beanName, Class<?> beanClass);

    Map<String, Set<AfterReturningAdvice>> resolveAfterAdvice(String beanName, Class<?> beanClass);

    Map<String, Set<ThrowsAdvice>> resolveThrowsAdvice(String beanName, Class<?> beanClass);

}
