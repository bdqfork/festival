package cn.bdqfork.context;

import cn.bdqfork.context.configuration.reader.GenericResourceReader;
import cn.bdqfork.context.configuration.reader.ResourceReader;
import cn.bdqfork.context.aware.BeanFactoryAware;
import cn.bdqfork.context.aware.ClassLoaderAware;
import cn.bdqfork.context.aware.ResourceReaderAware;
import cn.bdqfork.context.factory.AnnotationBeanDefinitionReader;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.AbstractBeanFactory;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.DefaultBeanFactory;
import cn.bdqfork.core.factory.DefaultJSR250BeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.processor.BeanFactoryPostProcessor;
import cn.bdqfork.core.factory.processor.BeanPostProcessor;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;
import cn.bdqfork.core.util.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * @author bdq
 * @since 2020/1/8
 */
public class AnnotationApplicationContext extends AbstractApplicationContext {
    private static final Logger log = LoggerFactory.getLogger(AnnotationApplicationContext.class);

    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    /**
     * 是否启用JSR250
     */
    protected static boolean JSR250 = true;
    /**
     * 是否启用AOP
     */
    protected static boolean AOP = true;
    /**
     * 委托工厂
     */
    private AbstractBeanFactory delegateBeanFactory;
    /**
     * bean描述信息读取器
     */
    private AnnotationBeanDefinitionReader beanDefinitionReader;
    private ResourceReader resourceReader;

    static {
        try {
            classLoader.loadClass("javax.annotation.Resource");
            log.info("enable jsr250 !");
        } catch (ClassNotFoundException e) {
            JSR250 = false;
        }
        try {
            classLoader.loadClass("cn.bdqfork.aop.factory.AopProxyBeanFactory");
            log.info("enable aop !");
        } catch (ClassNotFoundException e) {
            AOP = false;
        }
    }

    public AnnotationApplicationContext(String... scanPaths) throws BeansException {
        super(scanPaths);
    }

    @Override
    protected void createBeanFactory() {
        if (JSR250) {
            this.delegateBeanFactory = new DefaultJSR250BeanFactory();
        } else {
            this.delegateBeanFactory = new DefaultBeanFactory();
        }
        if (log.isTraceEnabled()) {
            log.trace("create BeanFactory of type {} !", this.delegateBeanFactory.getClass().getName());
        }
    }

    @Override
    protected void registerResourceReader() throws BeansException {
        if (log.isTraceEnabled()) {
            log.trace("register ResourceReader of type {} !", GenericResourceReader.class.getName());
        }

        BeanDefinition beanDefinition = BeanDefinition.builder()
                .beanName("resourceReader")
                .beanClass(GenericResourceReader.class)
                .scope(BeanDefinition.SINGLETON)
                .build();

        delegateBeanFactory.registerBeanDefinition(beanDefinition.getBeanName(), beanDefinition);

        resourceReader = delegateBeanFactory.getBean(beanDefinition.getBeanName());

        AnnotationBeanDefinitionReader annotationBeanDefinitionReader = new AnnotationBeanDefinitionReader(JSR250);

        annotationBeanDefinitionReader.setResourceReader(resourceReader);

        beanDefinitionReader = annotationBeanDefinitionReader;
    }

    @Override
    protected void registerProcessor() throws BeansException {
        if (AOP) {
            if (log.isTraceEnabled()) {
                log.trace("register aop processor !");
            }

            registerProxyProcessorBean();
        }
    }

    protected void registerProxyProcessorBean() throws BeansException {
        Class<?> aopProcessorClass;
        try {
            aopProcessorClass = classLoader.loadClass("cn.bdqfork.aop.processor.AopProxyProcessor");
        } catch (ClassNotFoundException e) {
            throw new BeansException(e);
        }

        BeanDefinition beanDefinition = BeanDefinition.builder()
                .beanName("aopProcessor")
                .beanClass(aopProcessorClass)
                .scope(BeanDefinition.SINGLETON)
                .build();
        this.delegateBeanFactory.registerBeanDefinition(beanDefinition.getBeanName(), beanDefinition);
    }

    @Override
    protected void registerShutdownHook() {
        if (log.isTraceEnabled()) {
            log.trace("register shutdown hook !");
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                close();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }));
    }

    @Override
    public void scan(String... scanPaths) throws BeansException {
        for (String scanPath : scanPaths) {
            if (log.isTraceEnabled()) {
                log.trace("scan bean by path {} !", scanPath);
            }
            beanDefinitionReader.scan(scanPath);
        }
        refresh();
    }

    protected void refresh() throws BeansException {

        registerBean();

        processEnvironment();

        processBeanFactory();

        registerBeanPostProcessor();
    }

    protected void registerBean() throws BeansException {
        if (log.isTraceEnabled()) {
            log.trace("register BeanDefinition !");
        }

        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionReader.getBeanDefinitions().entrySet()) {
            BeanDefinitionRegistry registry = delegateBeanFactory;
            String beanName = entry.getKey();
            if (log.isDebugEnabled()) {
                log.debug("register bean {}!", beanName);
            }
            BeanDefinition beanDefinition = entry.getValue();
            if (registry.containBeanDefinition(beanName)) {
                if (log.isTraceEnabled()) {
                    log.trace("duplicate bean of type {} will not be registered !", beanDefinition.getBeanClass().getName());
                }
                continue;
            }
            beanDefinitionReader.resolveInjectedPoint(beanDefinition, delegateBeanFactory);
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    protected void processEnvironment() throws BeansException {
        if (log.isTraceEnabled()) {
            log.trace("process environment !");
        }
        for (ClassLoaderAware classLoaderAware : delegateBeanFactory.getBeans(ClassLoaderAware.class).values()) {
            classLoaderAware.setClassLoader(classLoader);
        }
        for (ResourceReaderAware resourceReaderAware : delegateBeanFactory.getBeans(ResourceReaderAware.class).values()) {
            resourceReaderAware.setResourceReader(resourceReader);
        }
    }

    protected void registerBeanPostProcessor() throws BeansException {
        if (log.isTraceEnabled()) {
            log.trace("register bean processor !");
        }
        Collection<BeanPostProcessor> processors = null;
        try {
            processors = delegateBeanFactory.getBeans(BeanPostProcessor.class).values();
        } catch (NoSuchBeanException e) {
            if (log.isTraceEnabled()) {
                log.trace("no register BeanPost processor !");
            }
        }
        if (processors != null) {
            List<BeanPostProcessor> sortedProcessorList = BeanUtils.sortByOrder(processors);
            for (BeanPostProcessor beanPostProcessor : sortedProcessorList) {
                delegateBeanFactory.addPostBeanProcessor(beanPostProcessor);
            }
        }
    }

    protected void processBeanFactory() throws BeansException {
        if (log.isTraceEnabled()) {
            log.trace("register BeanFactory processor !");
        }

        Collection<BeanFactoryPostProcessor> processors = null;
        try {
            processors = delegateBeanFactory.getBeans(BeanFactoryPostProcessor.class).values();

        } catch (NoSuchBeanException e) {
            if (log.isTraceEnabled()) {
                log.trace("no register BeanFactory found !");
            }
        }
        if (processors != null) {
            List<BeanFactoryPostProcessor> sortedProcessorList = BeanUtils.sortByOrder(processors);
            for (BeanFactoryPostProcessor factoryPostProcessor : sortedProcessorList) {
                factoryPostProcessor.postProcessBeanFactory(delegateBeanFactory);
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("register BeanFactoryAware processor !");
        }

        Collection<BeanFactoryAware> beanFactoryAwares = null;
        try {
            beanFactoryAwares = delegateBeanFactory.getBeans(BeanFactoryAware.class).values();
        } catch (NoSuchBeanException e) {
            if (log.isTraceEnabled()) {
                log.trace("no register BeanFactoryAware found !");
            }
        }
        if (beanFactoryAwares != null) {
            for (BeanFactoryAware beanFactoryAware : beanFactoryAwares) {
                beanFactoryAware.setBeanFactory(delegateBeanFactory);
            }
        }

    }


    @Override
    public ConfigurableBeanFactory getBeanFactory() {
        return delegateBeanFactory;
    }

    @Override
    public void close() throws Exception {
        log.info("closing context !");
        synchronized (Object.class) {
            if (!isClosed()) {
                doClose();
            }
            closed = true;
        }
        log.info("closed context !");
    }

    protected void doClose() throws InterruptedException {
        delegateBeanFactory.destroySingletons();
    }

}
