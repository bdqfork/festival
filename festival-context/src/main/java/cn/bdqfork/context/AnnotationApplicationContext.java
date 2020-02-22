package cn.bdqfork.context;

import cn.bdqfork.context.aware.BeanFactoryAware;
import cn.bdqfork.context.aware.ClassLoaderAware;
import cn.bdqfork.context.aware.ResourceReaderAware;
import cn.bdqfork.context.configuration.reader.GenericResourceReader;
import cn.bdqfork.context.configuration.reader.ResourceReader;
import cn.bdqfork.context.factory.AnnotationBeanDefinitionReader;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.extension.ExtensionLoader;
import cn.bdqfork.core.factory.*;
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
            log.info("jsr250 enabled !");
        } catch (ClassNotFoundException e) {
            JSR250 = false;
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
    protected void registerLifeCycleProcessor() throws BeansException {
        ExtensionLoader<LifeCycleProcessor> extensionLoader = ExtensionLoader.getExtensionLoader(LifeCycleProcessor.class);
        Collection<LifeCycleProcessor> lifeCycleProcessors = extensionLoader.getExtensions().values();
        BeanNameGenerator beanNameGenerator = new SimpleBeanNameGenerator();
        for (LifeCycleProcessor lifeCycleProcessor : lifeCycleProcessors) {
            String beanName = beanNameGenerator.generateBeanName(lifeCycleProcessor.getClass());
            getBeanFactory().registerSingleton(beanName, lifeCycleProcessor);
        }
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

        processBeanPostProcessor();
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

    protected void processBeanPostProcessor() throws BeansException {
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
    protected void doClose() throws Exception {
        log.info("closing context !");
        synchronized (this) {
            if (!isClosed()) {
                delegateBeanFactory.destroySingletons();
                log.info("closed context !");
            }
            closed = true;
        }
    }

}
