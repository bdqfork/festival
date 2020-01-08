package cn.bdqfork.core.factory.support;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.factory.*;
import cn.bdqfork.core.factory.definition.AnnotationBeanDefinitionReader;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;

import java.util.Map;

/**
 * @author bdq
 * @since 2019/12/16
 */
public class AnnotationBeanFactory extends AbstractDelegateBeanFactory {
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
        ClassLoader classLoader = AnnotationBeanFactory.class.getClassLoader();
        try {
            classLoader.loadClass("javax.annotation.Resource");
        } catch (ClassNotFoundException e) {
            JSR250 = false;
        }
    }

    public AnnotationBeanFactory() {
        this(new AnnotationBeanDefinitionReader(JSR250));
    }

    public AnnotationBeanFactory(AnnotationBeanDefinitionReader beanDefinitionReader) {
        if (JSR250) {
            this.delegateBeanFactory = new DefaultJSR250BeanFactory();
        } else {
            this.delegateBeanFactory = new DefaultBeanFactory();
        }
        this.beanDefinitionReader = beanDefinitionReader;
    }

    public void scan(String scanPath) throws BeansException {
        beanDefinitionReader.scan(scanPath);
    }

    public void refresh() throws BeansException {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionReader) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) getParentBeanFactory();
            String beanName = entry.getKey();
            if (registry.containBeanDefinition(beanName)) {
                continue;
            }
            BeanDefinition beanDefinition = entry.getValue();
            beanDefinitionReader.resolveInjectedPoint(beanDefinition, (AbstractBeanFactory) getParentBeanFactory());
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }


    @Override
    public void setParentBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof AbstractBeanFactory) {
            delegateBeanFactory = (AbstractBeanFactory) beanFactory;
        } else {
            throw new IllegalStateException(String.format("unsupport BeanFactory %s ! delegate BeanFactory " +
                    "can only be instance of AbstractBeanFactory.class !", beanFactory.getClass()));
        }
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return delegateBeanFactory;
    }

    public void destroy() {
        AbstractBeanFactory abstractBeanFactory = (AbstractBeanFactory) getParentBeanFactory();
        abstractBeanFactory.destroySingletons();
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        beanDefinitionReader.setBeanNameGenerator(beanNameGenerator);
    }

}
