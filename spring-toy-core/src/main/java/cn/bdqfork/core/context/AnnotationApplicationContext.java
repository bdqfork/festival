package cn.bdqfork.core.context;


import cn.bdqfork.core.annotation.*;
import cn.bdqfork.core.annotation.ScopeType;
import cn.bdqfork.core.container.*;
import cn.bdqfork.core.exception.*;
import cn.bdqfork.core.container.BeanNameGenerator;
import cn.bdqfork.core.container.SimpleBeanNameGenerator;
import cn.bdqfork.core.utils.ReflectUtil;

import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ApplicationContext的实现类，负责扫描注解，并将bean注册到容器中
 *
 * @author bdq
 * @date 2019-02-12
 */
public class AnnotationApplicationContext implements ApplicationContext {
    private String[] scanPaths;
    private BeanFactory beanFactory;
    private BeanNameGenerator beanNameGenerator;

    public AnnotationApplicationContext(String... scanPaths) throws ApplicationContextException {
        if (scanPaths.length < 1) {
            throw new ApplicationContextException("the length of scanPaths is less than one ");
        }
        this.beanNameGenerator = new SimpleBeanNameGenerator();
        this.beanFactory = new BeanFactoryImpl();
        this.scanPaths = scanPaths;
        this.scan();
    }

    private void scan() throws ResolvedException, BeansException {
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
                String beanScope = "singleton";
                if (candidate.getAnnotation(Singleton.class) == null) {

                    Scope scope = candidate.getAnnotation(Scope.class);

                    if (scope != null) {
                        if (!ScopeType.PROTOTYPE.equals(scope.value())) {
                            throw new ScopeException("the value of scope is error !");
                        } else {
                            beanScope = scope.value();
                        }
                    }

                }
                if ("".equals(name)) {
                    name = this.beanNameGenerator.generateBeanName(candidate);
                }

                Lazy lazy = candidate.getAnnotation(Lazy.class);
                boolean isLazy = false;
                if (lazy != null) {
                    isLazy = lazy.value();
                }
                BeanDefinition beanDefinition = new BeanDefinition(candidate, beanScope, name, isLazy);

                beanFactory.register(beanDefinition.getBeanName(), beanDefinition);
            }
        }

        Map<String, BeanDefinition> beanDefinationMap = beanFactory.getBeanDefinations();

        Resolver resolver = new Resolver(beanNameGenerator,beanDefinationMap.values());
        resolver.resolve();

        instantiate(beanDefinationMap);
        processField(beanDefinationMap);
        processMethod(beanDefinationMap);
    }

    /**
     * 实例化
     */
    private void instantiate(Map<String, BeanDefinition> beanDefinations) throws BeansException {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinations.entrySet()) {
            beanFactory.instantiate(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 字段依赖注入
     */
    private void processField(Map<String, BeanDefinition> beanDefinationMap) throws BeansException {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinationMap.entrySet()) {
            beanFactory.processField(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 方法注入
     */
    private void processMethod(Map<String, BeanDefinition> beanDefinationMap) throws BeansException {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinationMap.entrySet()) {
            beanFactory.processMethod(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        return beanFactory.getBean(beanName);
    }

    @Override
    public <T> T getBean(Class<T> clazz) throws BeansException {
        return (T) beanFactory.getBean(clazz);
    }

    @Override
    public <T> Map<String, T> getBeans(Class<T> clazz) throws BeansException {
        return (Map<String, T>) beanFactory.getBeans(clazz);
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
