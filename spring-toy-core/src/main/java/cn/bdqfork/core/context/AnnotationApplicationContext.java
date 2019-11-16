package cn.bdqfork.core.context;


import cn.bdqfork.core.annotation.ScopeType;
import cn.bdqfork.core.aop.aspect.AspectAdvisor;
import cn.bdqfork.core.container.*;
import cn.bdqfork.core.container.resolver.AspectResolver;
import cn.bdqfork.core.container.resolver.BeanDefinitionResolver;
import cn.bdqfork.core.exception.ApplicationContextException;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.util.ComponentUtils;
import cn.bdqfork.core.util.ReflectUtils;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ApplicationContext的实现类，负责扫描注解，并将bean注册到容器中
 *
 * @author bdq
 * @since 2019-02-12
 */
public class AnnotationApplicationContext implements ApplicationContext {
    /**
     * 待扫描的路径
     */
    private String[] scanPaths;
    /**
     * Bean工厂
     */
    private BeanFactory beanFactory;
    /**
     * BeanName生成器
     */
    private BeanNameGenerator beanNameGenerator;

    public AnnotationApplicationContext(String... scanPaths) throws ApplicationContextException {
        if (scanPaths.length < 1) {
            throw new ApplicationContextException("the length of scanPaths is less than one ");
        }
        this.beanNameGenerator = new SimpleBeanNameGenerator();
        this.beanFactory = new AspectBeanFactoryImpl();
        this.scanPaths = scanPaths;
        this.scan();
    }

    private void scan() throws ResolvedException, BeansException {
        Set<Class<?>> candidates = new HashSet<>();
        for (String scanPath : scanPaths) {
            candidates.addAll(ReflectUtils.getClasses(scanPath));
        }
        Set<Class<?>> beanClasses = new HashSet<>();
        //获取组件类
        for (Class<?> candidate : candidates) {
            if (candidate.isAnnotation() || candidate.isInterface() || Modifier.isAbstract(candidate.getModifiers())) {
                continue;
            }
            if (ComponentUtils.isComponent(candidate)) {
                beanClasses.add(candidate);
            }
        }
        //解析BeanDefinition
        BeanDefinitionResolver beanDefinitionResolver = new BeanDefinitionResolver(beanNameGenerator, beanClasses);
        Map<String, BeanDefinition> beanDefinitions = beanDefinitionResolver.resolve();

        //注册BeanDefinition
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            beanFactory.registerBeanDefinition(beanName, beanDefinition);
        }

        //容器初始化
        instantiate(beanDefinitions);
        processField(beanDefinitions);
        processMethod(beanDefinitions);

        //解析aspect
        AspectResolver aspectResolver = new AspectResolver(beanDefinitions.values());
        Map<String, List<AspectAdvisor>> beanAdvisorMapper = aspectResolver.resolve();
        //注册advisor
        for (Map.Entry<String, List<AspectAdvisor>> entry : beanAdvisorMapper.entrySet()) {
            registerAdvisors(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 实例化
     */
    private void instantiate(Map<String, BeanDefinition> beanDefinations) throws BeansException {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinations.entrySet()) {
            beanFactory.instantiateIfNeed(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 字段依赖注入
     */
    private void processField(Map<String, BeanDefinition> beanDefinationMap) throws BeansException {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinationMap.entrySet()) {
            BeanDefinition beanDefinition = entry.getValue();
            if (!beanDefinition.isLazy()) {
                beanFactory.processField(entry.getKey(), beanDefinition);
            }
        }
    }

    /**
     * 方法注入
     */
    private void processMethod(Map<String, BeanDefinition> beanDefinationMap) throws BeansException {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinationMap.entrySet()) {
            BeanDefinition beanDefinition = entry.getValue();
            if (!beanDefinition.isLazy()) {
                beanFactory.processMethod(entry.getKey(), beanDefinition);
            }
        }
    }

    /**
     * 注册Advisor
     */
    private void registerAdvisors(String beanName, List<AspectAdvisor> advisors) throws BeansException {
        AspectBeanFactoryImpl aspectBeanFactory = (AspectBeanFactoryImpl) beanFactory;
        for (AspectAdvisor advisor : advisors) {
            aspectBeanFactory.registerAspectAdvisor(beanName, advisor);
        }
    }

    @Override
    public void registerSingleBean(FactoryBean factoryBean) throws BeansException {
        String beanName = this.beanNameGenerator.generateBeanName(factoryBean.getObjectType());
        Class<?> objectType = factoryBean.getObjectType();
        BeanDefinition beanDefinition = new BeanDefinition(objectType, ScopeType.SINGLETON, beanName, false);
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
        beanFactory.registerSingleBean(beanName, factoryBean);
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        BeanDefinition beanDefinition = beanFactory.getBeanDefinations().get(beanName);
        if (beanDefinition == null) {
            return null;
        }
        doProcessLazy(beanName, beanDefinition);
        return beanFactory.getBean(beanName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> clazz) throws BeansException {
        processLazyIfNeed(clazz);
        Object bean = beanFactory.getBean(clazz);
        return (T) bean;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> getBeans(Class<T> clazz) throws BeansException {
        processLazyIfNeed(clazz);
        return (Map<String, T>) beanFactory.getBeans(clazz);
    }

    private <T> void processLazyIfNeed(Class<T> clazz) throws BeansException {
        for (Map.Entry<String, BeanDefinition> entry : beanFactory.getBeanDefinations().entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (beanDefinition.getClazz() == clazz) {
                doProcessLazy(beanName, beanDefinition);
            }
        }
    }

    private void doProcessLazy(String beanName, BeanDefinition beanDefinition) throws BeansException {
        if (ScopeType.SINGLETON.equals(beanDefinition.getScope()) && beanDefinition.isLazy()) {
            beanFactory.processField(beanName, beanDefinition);
            beanFactory.processMethod(beanName, beanDefinition);
        }
    }

    @Override
    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

}
