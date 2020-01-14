package cn.bdqfork.context;

import cn.bdqfork.aop.processor.AopProxyProcessor;
import cn.bdqfork.context.factory.AnnotationBeanDefinitionReader;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.AbstractBeanFactory;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.DefaultBeanFactory;
import cn.bdqfork.core.factory.DefaultJSR250BeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.processor.BeanFactoryPostProcessor;
import cn.bdqfork.core.factory.processor.BeanPostProcessor;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;
import cn.bdqfork.value.reader.GenericResourceReader;
import cn.bdqfork.value.reader.ResourceReader;

import java.util.Map;

/**
 * @author bdq
 * @since 2020/1/8
 */
public class AnnotationApplicationContext extends AbstractApplicationContext {
    /**
     * 是否启用JSR250
     */
    protected static boolean JSR250 = true;
    protected static boolean AOP = true;
    /**
     * 委托工厂
     */
    private AbstractBeanFactory delegateBeanFactory;

    private AnnotationBeanDefinitionReader beanDefinitionReader;

    static {
        ClassLoader classLoader = AnnotationApplicationContext.class.getClassLoader();
        try {
            classLoader.loadClass("javax.annotation.Resource");
        } catch (ClassNotFoundException e) {
            JSR250 = false;
        }
        try {
            classLoader.loadClass("cn.bdqfork.aop.factory.AopProxyBeanFactory");
        } catch (ClassNotFoundException e) {
            JSR250 = false;
        }
    }


    public AnnotationApplicationContext(String... scanPaths) throws BeansException {

        createBeanFactory();

        registerResourceReader();

        registerProcessor();

        this.scan(scanPaths);

        this.refresh();

    }

    private void createBeanFactory() {
        if (JSR250) {
            this.delegateBeanFactory = new DefaultJSR250BeanFactory();
        } else {
            this.delegateBeanFactory = new DefaultBeanFactory();
        }
    }

    private void registerResourceReader() throws BeansException {
        this.delegateBeanFactory.registerBeanDefinition("resourceReader", new BeanDefinition("resourceReader", GenericResourceReader.class, BeanDefinition.SINGLETON));
        this.beanDefinitionReader = getBeanDefinitionReader();
    }

    private AnnotationBeanDefinitionReader getBeanDefinitionReader() throws BeansException {
        AnnotationBeanDefinitionReader annotationBeanDefinitionReader = new AnnotationBeanDefinitionReader(JSR250);
        annotationBeanDefinitionReader.setResourceReader(this.delegateBeanFactory.getBean(ResourceReader.class));
        return annotationBeanDefinitionReader;
    }

    private void registerProcessor() throws BeansException {
        if (AOP) {
            this.delegateBeanFactory.registerBeanDefinition("aopProcessor", new BeanDefinition("aopProcessor", AopProxyProcessor.class, BeanDefinition.SINGLETON));
        }
    }

    @Override
    public void scan(String... scanPaths) throws BeansException {
        for (String scanPath : scanPaths) {
            beanDefinitionReader.scan(scanPath);
        }
    }

    @Override
    public void refresh() throws BeansException {
        registerBeanDefinition();
        registerBeanFactoryPostProcessor();
        registerBeanPostProcessor();
    }

    private void registerBeanDefinition() throws BeansException {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionReader) {
            BeanDefinitionRegistry registry = delegateBeanFactory;
            String beanName = entry.getKey();
            if (registry.containBeanDefinition(beanName)) {
                continue;
            }
            BeanDefinition beanDefinition = entry.getValue();
            beanDefinitionReader.resolveInjectedPoint(beanDefinition, delegateBeanFactory);
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    private void registerBeanPostProcessor() throws BeansException {
        for (BeanPostProcessor beanPostProcessor : delegateBeanFactory.getBeans(BeanPostProcessor.class).values()) {
            delegateBeanFactory.addPostBeanProcessor(beanPostProcessor);
        }
    }

    private void registerBeanFactoryPostProcessor() throws BeansException {
        for (BeanFactoryPostProcessor factoryPostProcessor : delegateBeanFactory.getBeans(BeanFactoryPostProcessor.class).values()) {
            factoryPostProcessor.postProcessBeanFactory(delegateBeanFactory);
        }
    }


    @Override
    public ConfigurableBeanFactory getConfigurableBeanFactory() {
        return delegateBeanFactory;
    }

    @Override
    public void close() {
        AbstractBeanFactory abstractBeanFactory = delegateBeanFactory;
        abstractBeanFactory.destroySingletons();
    }
}
