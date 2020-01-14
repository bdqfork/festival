package cn.bdqfork.aop.processor;

import cn.bdqfork.aop.advice.*;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.util.ReflectUtils;
import org.aspectj.lang.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author bdq
 * @since 2020/1/14
 */
public class AspectResolver {

    public Set<Advisor> resolveAdvisors(BeanFactory beanFactory, BeanDefinition beanDefinition) throws BeansException {
        Set<Advisor> advisors = new HashSet<>();

        Class<?> clazz = beanDefinition.getBeanClass();
        //存储解析完成的pointcut，通知绑定
        Map<String, String> pointcuts = new HashMap<>();

        Method[] methods = clazz.getDeclaredMethods();
        //先解析pointcut，防止npe问题
        for (Method method : methods) {

            resolvePointcut(pointcuts, method);

        }
        for (Method method : methods) {

            ReflectUtils.makeAccessible(method);

            AspectAdvisorConfig config = new AspectAdvisorConfig(pointcuts, method, beanDefinition.getBeanName());

            if (method.isAnnotationPresent(Before.class)) {
                AspectAdvisor aspectAdvisor = resolveBeforeAdvice(config, beanFactory);
                advisors.add(aspectAdvisor);
                continue;
            }

            if (method.isAnnotationPresent(Around.class)) {
                AspectAdvisor aspectAdvisor = resolveAroundAdvice(config, beanFactory);
                advisors.add(aspectAdvisor);
                continue;
            }

            if (method.isAnnotationPresent(AfterReturning.class)) {
                AspectAdvisor aspectAdvisor = resolveAfterReturningAdvice(config, beanFactory);
                advisors.add(aspectAdvisor);
                continue;
            }

            if (method.isAnnotationPresent(AfterThrowing.class)) {
                AspectAdvisor aspectAdvisor = resolveAfterThrowingAdvice(config, beanFactory);
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

    private AspectAdvisor resolveBeforeAdvice(AspectAdvisorConfig config, BeanFactory beanFactory) throws BeansException {

        Method method = config.method;

        Before before = method.getAnnotation(Before.class);

        AspectMethodBeforeAdvice beforeAdvice = new AspectMethodBeforeAdvice();

        beforeAdvice.setAspectAdviceMethod(method);

        beforeAdvice.setAspectInstance(beanFactory.getBean(config.beanName));

        return getAspectAdvisor(config.pointcuts, before.value(), beforeAdvice);
    }

    private AspectAdvisor resolveAfterReturningAdvice(AspectAdvisorConfig config, BeanFactory beanFactory) throws BeansException {
        Method method = config.method;

        AfterReturning afterReturning = method.getAnnotation(AfterReturning.class);

        AspectAfterReturningAdvice afterReturningAdvice = new AspectAfterReturningAdvice();

        afterReturningAdvice.setAspectAdviceMethod(method);

        afterReturningAdvice.setAspectInstance(beanFactory.getBean(config.beanName));

        return getAspectAdvisor(config.pointcuts, afterReturning.value(), afterReturningAdvice);
    }

    private AspectAdvisor resolveAroundAdvice(AspectAdvisorConfig config, BeanFactory beanFactory) throws BeansException {
        Method method = config.method;

        Around around = method.getAnnotation(Around.class);

        AspectAroundAdvice aroundAdvice = new AspectAroundAdvice();

        aroundAdvice.setAspectAdviceMethod(method);

        aroundAdvice.setAspectInstance(beanFactory.getBean(config.beanName));

        return getAspectAdvisor(config.pointcuts, around.value(), aroundAdvice);
    }

    private AspectAdvisor resolveAfterThrowingAdvice(AspectAdvisorConfig config, BeanFactory beanFactory) throws BeansException {
        Method method = config.method;

        AfterThrowing afterThrowing = method.getAnnotation(AfterThrowing.class);

        AspectThrowsAdvice aspectThrowsAdvice = new AspectThrowsAdvice();

        aspectThrowsAdvice.setAspectAdviceMethod(method);

        aspectThrowsAdvice.setAspectInstance(beanFactory.getBean(config.beanName));

        return getAspectAdvisor(config.pointcuts, afterThrowing.value(), aspectThrowsAdvice);
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

    private static class AspectAdvisorConfig {
        Map<String, String> pointcuts;
        Method method;
        String beanName;

        public AspectAdvisorConfig(Map<String, String> pointcuts, Method method, String beanName) {
            this.pointcuts = pointcuts;
            this.method = method;
            this.beanName = beanName;
        }
    }

}
