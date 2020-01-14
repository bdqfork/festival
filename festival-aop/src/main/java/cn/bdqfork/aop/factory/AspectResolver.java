package cn.bdqfork.aop.factory;

import cn.bdqfork.aop.advice.*;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import org.aspectj.lang.annotation.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author bdq
 * @since 2020/1/14
 */
public class AspectResolver {
    private BeanFactory beanFactory;

    public List<Advisor> resolveAdvisors(BeanDefinition beanDefinition) throws BeansException {
        List<Advisor> advisors = new LinkedList<>();

        Class<?> clazz = beanDefinition.getBeanClass();
        //存储解析完成的pointcut，通知绑定
        Map<String, String> pointcuts = new HashMap<>();

        Method[] methods = clazz.getDeclaredMethods();
        //先解析pointcut，防止npe问题
        for (Method method : methods) {

            resolvePointcut(pointcuts, method);

        }
        for (Method method : methods) {

            method.setAccessible(true);

            if (method.isAnnotationPresent(Before.class)) {
                AspectAdvisor aspectAdvisor = resolveBeforeAdvice(pointcuts, method, beanDefinition.getBeanName());
                advisors.add(aspectAdvisor);
                continue;
            }

            if (method.isAnnotationPresent(Around.class)) {
                AspectAdvisor aspectAdvisor = resolveAroundAdvice(pointcuts, method, beanDefinition.getBeanName());
                advisors.add(aspectAdvisor);
                continue;
            }

            if (method.isAnnotationPresent(AfterReturning.class)) {
                AspectAdvisor aspectAdvisor = resolveAfterReturningAdvice(pointcuts, method, beanDefinition.getBeanName());
                advisors.add(aspectAdvisor);
                continue;
            }

            if (method.isAnnotationPresent(AfterThrowing.class)) {
                AspectAdvisor aspectAdvisor = resolveAfterThrowingAdvice(pointcuts, method, beanDefinition.getBeanName());
                advisors.add(aspectAdvisor);
            }

        }
        return advisors;
    }

    private void resolvePointcut(Map<String, String> pointcuts, Method method) {
        Pointcut pointcut = method.getAnnotation(Pointcut.class);
        if (pointcut != null) {
            pointcuts.put(method.getName() + "()", pointcut.value());
        }
    }

    private AspectAdvisor resolveBeforeAdvice(Map<String, String> pointcuts, Method method, String beanName) throws BeansException {
        Before before = method.getAnnotation(Before.class);

        AspectMethodBeforeAdvice beforeAdvice = new AspectMethodBeforeAdvice();

        beforeAdvice.setAspectAdviceMethod(method);

        beforeAdvice.setAspectInstance(beanFactory.getBean(beanName));

        return getAspectAdvisor(pointcuts, before.value(), beforeAdvice);
    }

    private AspectAdvisor resolveAfterReturningAdvice(Map<String, String> pointcuts, Method method, String beanName) throws BeansException {
        AfterReturning afterReturning = method.getAnnotation(AfterReturning.class);

        AspectAfterReturningAdvice afterReturningAdvice = new AspectAfterReturningAdvice();

        afterReturningAdvice.setAspectAdviceMethod(method);

        afterReturningAdvice.setAspectInstance(beanFactory.getBean(beanName));

        return getAspectAdvisor(pointcuts, afterReturning.value(), afterReturningAdvice);
    }

    private AspectAdvisor resolveAroundAdvice(Map<String, String> pointcuts, Method method, String beanName) throws BeansException {
        Around around = method.getAnnotation(Around.class);

        AspectAroundAdvice aroundAdvice = new AspectAroundAdvice();

        aroundAdvice.setAspectAdviceMethod(method);

        aroundAdvice.setAspectInstance(beanFactory.getBean(beanName));

        return getAspectAdvisor(pointcuts, around.value(), aroundAdvice);
    }

    private AspectAdvisor resolveAfterThrowingAdvice(Map<String, String> pointcuts, Method method, String beanName) throws BeansException {
        AfterThrowing afterThrowing = method.getAnnotation(AfterThrowing.class);

        AspectThrowsAdvice aspectThrowsAdvice = new AspectThrowsAdvice();

        aspectThrowsAdvice.setAspectAdviceMethod(method);

        aspectThrowsAdvice.setAspectInstance(beanFactory.getBean(beanName));

        return getAspectAdvisor(pointcuts, afterThrowing.value(), aspectThrowsAdvice);
    }

    private AspectAdvisor getAspectAdvisor(Map<String, String> pointcuts, String pointcut, AspectAdvice aspectAdvice) {
        AspectAdvisor aspectAdvisor = new AspectAdvisor();

        aspectAdvisor.setAdvice(aspectAdvice);

        String expression = pointcut;

        if (!pointcut.startsWith("execution")) {
            expression = pointcuts.get(pointcut);
        }

        aspectAdvisor.setPointcut(expression.substring(10, expression.length() - 1));

        return aspectAdvisor;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
