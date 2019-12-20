package cn.bdqfork.core.factory.support;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.exception.ScopeException;
import cn.bdqfork.core.factory.BeanDefinition;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;
import cn.bdqfork.core.util.ReflectUtils;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author bdq
 * @since 2019/12/20
 */
public abstract class AbstractAutoResolveBeanFactory extends AbstractDelegateBeanFactory {

    public void scan(String... scanPaths) throws BeansException {

        if (scanPaths.length < 1) {
            throw new BeansException("the length of scanPaths is less than one ");
        }

        Set<Class<?>> candidates = new HashSet<>();
        for (String scanPath : scanPaths) {
            candidates.addAll(ReflectUtils.getClasses(scanPath));
        }

        //获取组件类
        Set<Class<?>> beanClasses = filterBean(candidates);

        //解析BeanDefinition
        Map<String, BeanDefinition> beanDefinitions;
        try {
            beanDefinitions = resolve(beanClasses);
        } catch (ResolvedException e) {
            throw new BeansException(e);
        }

        //注册BeanDefinition
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) getParentBeanFactory();
            registry.registerBeanDefinition(entry.getKey(), entry.getValue());
        }

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

    protected Map<String, BeanDefinition> resolve(Set<Class<?>> beanClasses) throws ResolvedException {
        Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
        for (Class<?> clazz : beanClasses) {

            String beanName = resolveBeanName(clazz);

            BeanDefinition beanDefinition = createBeanDefinition(beanName, clazz);

            resolveInjectedPoint(beanDefinition, beanClasses);

            beanDefinitions.put(beanDefinition.getBeanName(), beanDefinition);

        }
        return beanDefinitions;
    }

    protected abstract BeanDefinition createBeanDefinition(String beanName, Class<?> clazz) throws ScopeException;

    protected abstract String resolveBeanName(Class<?> clazz);

    protected void resolveInjectedPoint(BeanDefinition beanDefinition, Set<Class<?>> beanClasses) throws ResolvedException {

        //如果已经解析过了，则返回
        if (beanDefinition.isResolved()) {
            return;
        }

        //优先解析父类
        Class<?> superClass = beanDefinition.getBeanClass().getSuperclass();
        if (superClass != null && superClass != Object.class) {

            for (Class<?> clazz : beanClasses) {

                if (clazz == superClass) {
                    String beanName = resolveBeanName(clazz);
                    resolveInjectedPoint(createBeanDefinition(beanName, clazz), beanClasses);
                }

            }
        }

        resolveConstructor(beanDefinition);

        resolveField(beanDefinition);

        resolveMethod(beanDefinition);

        beanDefinition.setResolved(true);
    }

    protected abstract void resolveConstructor(BeanDefinition beanDefinition) throws ResolvedException;

    protected abstract void resolveField(BeanDefinition beanDefinition) throws ResolvedException;

    protected abstract void resolveMethod(BeanDefinition beanDefinition) throws ResolvedException;

    protected abstract boolean checkIfComponent(Class<?> candidate);

}
