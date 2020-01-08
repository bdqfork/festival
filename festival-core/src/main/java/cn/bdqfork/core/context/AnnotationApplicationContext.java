package cn.bdqfork.core.context;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.AbstractBeanFactory;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.DefaultBeanFactory;
import cn.bdqfork.core.factory.DefaultJSR250BeanFactory;
import cn.bdqfork.core.factory.definition.AnnotationBeanDefinitionReader;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.processor.BeanFactoryPostProcessor;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;

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
    }


    public AnnotationApplicationContext(String... scanPaths) throws BeansException {

        this.delegateBeanFactory = createBeanFactory();

        this.beanDefinitionReader = getBeanDefinitionReader();

        this.scan(scanPaths);

        this.refresh();

    }

    protected AnnotationBeanDefinitionReader getBeanDefinitionReader() {
        return new AnnotationBeanDefinitionReader(JSR250);
    }

    protected AbstractBeanFactory createBeanFactory() {
        if (JSR250) {
            return new DefaultJSR250BeanFactory();
        } else {
            return new DefaultBeanFactory();
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
        doRefresh();
        processBeanFactory();
    }

    private void processBeanFactory() throws BeansException {
        BeanFactoryPostProcessor[] beanFactoryPostProcessors = delegateBeanFactory.getBeans(BeanFactoryPostProcessor.class).values().toArray(new BeanFactoryPostProcessor[0]);
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessors) {
            beanFactoryPostProcessor.postProcessBeanFactory(delegateBeanFactory);
        }
    }

    private void doRefresh() throws BeansException {
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
