package cn.bdqfork.core.context;


import cn.bdqfork.core.annotation.*;
import cn.bdqfork.core.annotation.ScopeType;
import cn.bdqfork.core.container.*;
import cn.bdqfork.core.exception.SpringToyException;
import cn.bdqfork.core.container.BeanNameGenerator;
import cn.bdqfork.core.container.SimpleBeanNameGenerator;
import cn.bdqfork.core.utils.ReflectUtil;

import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.reflect.*;
import java.util.*;

/**
 * ApplicationContext的实现类，负责扫描注解，并将bean注册到容器中
 *
 * @author bdq
 * @date 2019-02-12
 */
public class AnnotationApplicationContext implements ApplicationContext {
    private String[] scanPaths;
    private BeanContainer beanContainer;
    private BeanNameGenerator beanNameGenerator;

    public AnnotationApplicationContext(String... scanPaths) throws SpringToyException {
        if (scanPaths.length < 1) {
            throw new SpringToyException("the length of scanPaths is less than 1 ");
        }
        this.beanNameGenerator = new SimpleBeanNameGenerator();
        this.beanContainer = new BeanContainer();
        this.scanPaths = scanPaths;
        this.scan();
    }

    private void scan() throws SpringToyException {
        Set<Class<?>> candidates = new HashSet<>();
        for (String scanPath : scanPaths) {
            candidates.addAll(ReflectUtil.getClasses(scanPath));
        }
        for (Class<?> candidate : candidates) {
            if (candidate.isAnnotation() || candidate.isInterface() || Modifier.isAbstract(candidate.getModifiers())) {
                continue;
            }
            String name = getComponentName(candidate);
            if (name != null) {
                boolean isSingleton = false;

                Scope scope = candidate.getAnnotation(Scope.class);
                if (scope != null) {
                    if (ScopeType.SINGLETON.equals(scope.value())) {
                        isSingleton = true;
                    } else if (!ScopeType.PROTOTYPE.equals(scope.value())) {
                        throw new SpringToyException("the value of scope is error !");
                    }
                } else if (candidate.getAnnotation(Singleton.class) != null) {
                    isSingleton = true;
                }

                if ("".equals(name)) {
                    name = this.beanNameGenerator.generateBeanName(candidate);
                }

                BeanDefinition beanDefinition = new BeanDefinition(candidate, isSingleton, name);
                beanDefinition.setInjectorProvider(new InjectorProvider(candidate, this.beanNameGenerator));

                beanContainer.register(beanDefinition.getName(), beanDefinition);
            }
        }

        Map<String, BeanDefinition> beanDefinationMap = beanContainer.getBeanDefinations();
        Resolver resolver = new Resolver(beanContainer);
        for (Map.Entry<String, BeanDefinition> entry : beanDefinationMap.entrySet()) {
            resolver.resolve(entry.getValue());
        }

    }

    @Override
    public Object getBean(String beanName) throws SpringToyException {
        return beanContainer.getBean(beanName);
    }

    @Override
    public <T> T getBean(Class<T> clazz) throws SpringToyException {
        BeanDefinition beanDefinition = beanContainer.getBean(clazz);
        if (beanDefinition != null) {
            return (T) beanDefinition.getInstance();
        }
        return null;
    }

    @Override
    public <T> Map<String, T> getBeans(Class<T> clazz) throws SpringToyException {
        Map<String, T> beanMap = new HashMap<>(8);
        for (Map.Entry<String, BeanDefinition> entry : beanContainer.getBeans(clazz).entrySet()) {
            beanMap.put(entry.getKey(), (T) entry.getValue().getInstance());
        }
        return beanMap;
    }

    @Override
    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

    private String getComponentName(Class<?> candidate) {
        Component component = candidate.getAnnotation(Component.class);
        if (component != null) {
            return component.value();
        }
        Service service = candidate.getAnnotation(Service.class);
        if (service != null) {
            return service.value();
        }
        Repositorty repositorty = candidate.getAnnotation(Repositorty.class);
        if (repositorty != null) {
            return repositorty.value();
        }
        Controller controller = candidate.getAnnotation(Controller.class);
        if (controller != null) {
            return controller.value();
        }
        Named named = candidate.getAnnotation(Named.class);
        if (named != null) {
            return named.value();
        }
        return null;
    }

}
