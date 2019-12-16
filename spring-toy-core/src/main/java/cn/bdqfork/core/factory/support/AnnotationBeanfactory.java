package cn.bdqfork.core.factory.support;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.factory.BeanDefinition;
import cn.bdqfork.core.factory.BeanNameGenerator;
import cn.bdqfork.core.factory.DefaultBefactory;
import cn.bdqfork.core.factory.SimpleBeanNameGenerator;
import cn.bdqfork.core.factory.resolver.BeanDefinitionResolver;
import cn.bdqfork.core.util.ReflectUtils;

import javax.inject.Named;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author bdq
 * @since 2019/12/16
 */
public class AnnotationBeanfactory extends DefaultBefactory {
    /**
     * BeanName生成器
     */
    private BeanNameGenerator beanNameGenerator;

    public AnnotationBeanfactory(String... scanPaths) throws BeansException {
        if (scanPaths.length < 1) {
            throw new BeansException("the length of scanPaths is less than one ");
        }
        this.beanNameGenerator = new SimpleBeanNameGenerator();
        this.scan(scanPaths);
    }

    private void scan(String[] scanPaths) throws BeansException {
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
            if (checkIfComponent(candidate)) {
                beanClasses.add(candidate);
            }
        }
        //解析BeanDefinition
        BeanDefinitionResolver beanDefinitionResolver = new BeanDefinitionResolver(beanNameGenerator, beanClasses);

        Map<String, BeanDefinition> beanDefinitions;
        try {
            beanDefinitions = beanDefinitionResolver.resolve();
        } catch (ResolvedException e) {
            throw new BeansException(e);
        }

        //注册BeanDefinition
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            registerBeanDefinition(beanName, beanDefinition);
        }

    }

    protected boolean checkIfComponent(Class<?> candidate) {
        return candidate.isAnnotationPresent(Named.class);
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }
}
