package cn.bdqfork.aop.processor;

import cn.bdqfork.aop.advice.Advisor;
import cn.bdqfork.aop.factory.AopProxyBeanFactory;
import cn.bdqfork.aop.factory.DefaultAopProxyBeanFactory;
import cn.bdqfork.aop.proxy.AopProxySupport;
import cn.bdqfork.context.AbstractLifeCycleProcessor;
import cn.bdqfork.context.ApplicationContext;
import cn.bdqfork.core.annotation.Optimize;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.definition.ManagedBeanDefinition;
import cn.bdqfork.core.factory.processor.BeanFactoryPostProcessor;
import cn.bdqfork.core.factory.processor.BeanPostProcessor;
import cn.bdqfork.core.factory.processor.OrderAware;
import cn.bdqfork.core.util.AnnotationUtils;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Executable;
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
public class AopProxyProcessor extends AbstractLifeCycleProcessor implements BeanPostProcessor, BeanFactoryPostProcessor, OrderAware {
    private static final Logger log = LoggerFactory.getLogger(AopProxyProcessor.class);
    private Set<Advisor> advisors = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private AspectResolver aspectResolver = new AspectResolver();
    private AopProxyBeanFactory aopProxyBeanFactory = new DefaultAopProxyBeanFactory();
    private ApplicationContext applicationContext;

    @Override
    public void beforeStart(ApplicationContext applicationContext) throws Exception {
        super.beforeStart(applicationContext);
        if (log.isInfoEnabled()) {
            log.info("aop enabled!");
        }
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
        List<BeanDefinition> beanDefinitions = beanFactory.getBeanDefinitions().values()
                .stream()
                .filter(beanDefinition -> checkIfNeedRegister(beanFactory, beanDefinition))
                .collect(Collectors.toList());

        for (BeanDefinition beanDefinition : beanDefinitions) {
            Set<Advisor> advisors = aspectResolver.resolveAdvisors(beanFactory, beanDefinition);
            this.advisors.addAll(advisors);
        }
    }

    private boolean checkIfNeedRegister(ConfigurableBeanFactory beanFactory, BeanDefinition beanDefinition) {
        return beanDefinition.getBeanClass().isAnnotationPresent(Aspect.class) && !beanFactory.containSingleton(beanDefinition.getBeanName());
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

        BeanDefinition beanDefinition = applicationContext.getBeanFactory().getBeanDefinition(beanName);
        Class<?> beanClass = beanDefinition.getBeanClass();
        config.setBeanClass(beanClass);

        List<Class<?>> interfaces = Arrays.stream(beanClass.getInterfaces())
                .collect(Collectors.toList());
        config.setInterfaces(interfaces);

        if (checkIfOptimize(beanDefinition)) {
            config.setOptimze(true);
        }

        return aopProxyBeanFactory.createAopProxyBean(config);
    }

    private boolean checkIfOptimize(BeanDefinition beanDefinition) {
        if (beanDefinition instanceof ManagedBeanDefinition) {
            return true;
        }

        Class<?> beanClass = beanDefinition.getBeanClass();
        if (AnnotationUtils.isAnnotationPresent(beanClass, Optimize.class)) {
            return true;
        }

        Executable executable = beanDefinition.getConstructor();
        if (executable != null) {
            return AnnotationUtils.isAnnotationPresent(executable, Optimize.class);
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
