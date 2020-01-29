package cn.bdqfork.context;

import cn.bdqfork.core.exception.BeansException;

import java.util.Map;

/**
 * @author bdq
 * @since 2020/1/8
 */
public abstract class AbstractApplicationContext implements ApplicationContext {
    protected boolean closed = false;
    protected String[] scanPaths;

    public AbstractApplicationContext(String... scanPaths) throws BeansException {
        this.scanPaths = scanPaths;
    }

    @Override
    public void start() throws Exception {
        createBeanFactory();

        registerResourceReader();

        registerProcessor();

        scan(scanPaths);

        registerShutdownHook();
    }

    protected abstract void createBeanFactory();

    protected abstract void registerResourceReader() throws BeansException;

    protected abstract void registerProcessor() throws BeansException;

    protected abstract void registerShutdownHook();

    @Override
    public <T> T getBean(String beanName) throws BeansException {
        return getBeanFactory().getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, Object[] args) throws BeansException {
        return getBeanFactory().getBean(beanName, args);
    }

    @Override
    public <T> T getBean(Class<T> clazz) throws BeansException {
        return getBeanFactory().getBean(clazz);
    }

    @Override
    public <T> T getSpecificBean(String beanName, Class<T> clazz) throws BeansException {
        return getBeanFactory().getSpecificBean(beanName, clazz);
    }

    @Override
    public <T> Map<String, T> getBeans(Class<T> clazz) throws BeansException {
        return getBeanFactory().getBeans(clazz);
    }

    @Override
    public boolean containBean(String beanName) {
        return getBeanFactory().containBean(beanName);
    }

    @Override
    public boolean isSingleton(String beanName) throws BeansException {
        return getBeanFactory().isSingleton(beanName);
    }

    @Override
    public boolean isPrototype(String beanName) throws BeansException {
        return getBeanFactory().isPrototype(beanName);
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

}
