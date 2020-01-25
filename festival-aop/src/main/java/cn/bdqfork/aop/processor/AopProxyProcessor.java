package cn.bdqfork.aop.processor;

import cn.bdqfork.aop.advice.Advisor;
import cn.bdqfork.aop.factory.AopProxyBeanFactory;
import cn.bdqfork.aop.factory.DefaultAopProxyBeanFactory;
import cn.bdqfork.aop.proxy.AopProxySupport;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.definition.ManagedBeanDefinition;
import cn.bdqfork.core.factory.processor.BeanFactoryPostProcessor;
import cn.bdqfork.core.factory.processor.BeanPostProcessor;
import org.aspectj.lang.annotation.Aspect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2020/1/14
 */
public class AopProxyProcessor implements BeanPostProcessor, BeanFactoryPostProcessor {
    private Set<Advisor> advisors = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private AspectResolver aspectResolver = new AspectResolver();
    private AopProxyBeanFactory aopProxyBeanFactory = new DefaultAopProxyBeanFactory();
    private ConfigurableBeanFactory configurableBeanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
        configurableBeanFactory = beanFactory;

        List<BeanDefinition> beanDefinitions = configurableBeanFactory.getBeanDefinitions().values()
                .stream()
                .filter(this::checkIfNeedRegister)
                .collect(Collectors.toList());

        for (BeanDefinition beanDefinition : beanDefinitions) {
            Set<Advisor> advisors = aspectResolver.resolveAdvisors(beanFactory, beanDefinition);
            this.advisors.addAll(advisors);
        }
    }

    private boolean checkIfNeedRegister(BeanDefinition beanDefinition) {
        return beanDefinition.getBeanClass().isAnnotationPresent(Aspect.class) && !configurableBeanFactory.containSingleton(beanDefinition.getBeanName());
    }

    @Override
    public Object postProcessBeforeInitializtion(String beanName, Object bean) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitializtion(String beanName, Object bean) throws BeansException {
        AopProxySupport config = new AopProxySupport();
        config.addAdvisors(advisors);
        config.setBean(bean);

        BeanDefinition beanDefinition = configurableBeanFactory.getBeanDefinition(beanName);
        Class<?> beanClass = beanDefinition.getBeanClass();
        config.setBeanClass(beanClass);

        List<Class<?>> interfaces = Arrays.stream(beanClass.getInterfaces())
                .collect(Collectors.toList());
        config.setInterfaces(interfaces);

        if (beanDefinition instanceof ManagedBeanDefinition) {
            config.setOptimze(true);
        }

        return aopProxyBeanFactory.createAopProxyBean(config);
    }

}
