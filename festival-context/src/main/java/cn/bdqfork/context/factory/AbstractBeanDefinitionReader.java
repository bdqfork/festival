package cn.bdqfork.context.factory;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.ConflictedBeanException;
import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.exception.ScopeException;
import cn.bdqfork.core.factory.AbstractBeanFactory;
import cn.bdqfork.core.factory.BeanNameGenerator;
import cn.bdqfork.core.factory.SimpleBeanNameGenerator;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.value.reader.ResourceReader;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author bdq
 * @since 2020/1/8
 */
public abstract class AbstractBeanDefinitionReader {
    private Map<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>(256);
    private ResourceReader resourceReader;
    /**
     * BeanName生成器
     */
    private BeanNameGenerator beanNameGenerator = new SimpleBeanNameGenerator();

    public void scan(String scanPath) throws BeansException {

        Set<Class<?>> candidates = new HashSet<>(ReflectUtils.getClasses(scanPath));

        //获取组件类
        Set<Class<?>> beanClasses = filterBean(candidates);

        beanDefinitions.putAll(resolve(beanClasses));

    }

    protected Set<Class<?>> filterBean(Set<Class<?>> candidates) {
        Set<Class<?>> beanClasses = new HashSet<>();
        for (Class<?> candidate : candidates) {

            if (candidate.isAnnotation() || candidate.isInterface() || Modifier.isAbstract(candidate.getModifiers())) {
                continue;
            }

            if (checkIfComponent(candidate)) {
                beanClasses.add(candidate);
            }
        }
        return beanClasses;
    }

    protected Map<String, BeanDefinition> resolve(Set<Class<?>> beanClasses) throws ResolvedException, ConflictedBeanException {
        Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
        for (Class<?> clazz : beanClasses) {

            String beanName = resolveBeanName(clazz);

            BeanDefinition beanDefinition = createBeanDefinition(beanName, clazz);

            if (beanDefinitions.containsKey(beanDefinition.getBeanName())) {

                throw new ConflictedBeanException(String.format("the entity named %s has conflicted ! ", beanName));

            }
            beanDefinitions.put(beanDefinition.getBeanName(), beanDefinition);

        }
        return beanDefinitions;
    }

    protected abstract BeanDefinition createBeanDefinition(String beanName, Class<?> clazz) throws ScopeException;

    protected abstract String resolveBeanName(Class<?> clazz);

    public void resolveInjectedPoint(BeanDefinition beanDefinition, AbstractBeanFactory beanFactory) throws ResolvedException {

        //如果已经解析过了，则返回
        if (beanDefinition.isResolved()) {
            return;
        }

        resolveConstructor(beanDefinition, beanFactory);

        resolveField(beanDefinition, beanFactory);

        resolveMethod(beanDefinition, beanFactory);

        beanDefinition.setResolved(true);
    }

    protected abstract void resolveConstructor(BeanDefinition beanDefinition, AbstractBeanFactory beanFactory) throws ResolvedException;

    protected abstract void resolveField(BeanDefinition beanDefinition, AbstractBeanFactory beanFactory) throws ResolvedException;

    protected abstract void resolveMethod(BeanDefinition beanDefinition, AbstractBeanFactory beanFactory) throws ResolvedException;

    protected abstract boolean checkIfComponent(Class<?> candidate);

    public Map<String, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }

    public void setBeanDefinitions(Map<String, BeanDefinition> beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }

    public BeanNameGenerator getBeanNameGenerator() {
        return beanNameGenerator;
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

    public ResourceReader getResourceReader() {
        return resourceReader;
    }

    public void setResourceReader(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
    }
}
