package cn.bdqfork.core.factory.support;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.exception.ScopeException;
import cn.bdqfork.core.factory.*;
import cn.bdqfork.core.util.BeanUtils;
import cn.bdqfork.core.util.ReflectUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Scope;
import javax.inject.Singleton;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author bdq
 * @since 2019/12/16
 */
public class AnnotationBeanFactory extends AbstractDelegateBeanFactory implements ConfigurableBeanFactory {
    private AbstractAutoInjectedBeanFactory delegateBeanFactory;

    /**
     * BeanName生成器
     */
    private BeanNameGenerator beanNameGenerator;

    public AnnotationBeanFactory(String... scanPaths) throws BeansException {
        this(new DefaultBeanFactory(), scanPaths);
    }

    public AnnotationBeanFactory(AbstractAutoInjectedBeanFactory delegateBeanFactory, String... scanPaths) throws BeansException {
        if (scanPaths.length < 1) {
            throw new BeansException("the length of scanPaths is less than one ");
        }
        this.delegateBeanFactory = delegateBeanFactory;
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
        Map<String, BeanDefinition> beanDefinitions;
        try {
            beanDefinitions = resolve(beanClasses);
        } catch (ResolvedException e) {
            throw new BeansException(e);
        }

        //注册BeanDefinition
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            getDelegateBeanFactory().registerBeanDefinition(beanName, beanDefinition);
        }

    }

    protected Map<String, BeanDefinition> resolve(Set<Class<?>> beanClasses) throws ResolvedException {
        Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
        for (Class<?> clazz : beanClasses) {
            BeanDefinition beanDefinition = createBeanDefinition(clazz);
            doResolve(beanDefinition, beanClasses);
            beanDefinitions.put(beanDefinition.getBeanName(), beanDefinition);
        }
        return beanDefinitions;
    }

    protected BeanDefinition createBeanDefinition(Class<?> clazz) throws ScopeException {
        String name = clazz.getAnnotation(Named.class).value();
        if ("".equals(name)) {
            name = this.beanNameGenerator.generateBeanName(clazz);
        }
        if (clazz.isAnnotationPresent(Singleton.class)) {
            return new ManagedBeanDefinition(name, clazz, BeanDefinition.SINGLETON);
        } else if (clazz.isAnnotationPresent(Scope.class)) {
            throw new ScopeException(String.format("used the scope annotation on a class " +
                    "but forgot to configure the scope in the class %s !", clazz.getCanonicalName()));
        } else {
            return new ManagedBeanDefinition(name, clazz);
        }
    }

    private void doResolve(BeanDefinition beanDefinition, Set<Class<?>> beanClasses) throws ResolvedException {
        //如果已经解析过了，则返回
        if (beanDefinition.isResolved()) {
            return;
        }
        //优先解析父类
        Class<?> superClass = beanDefinition.getBeanClass().getSuperclass();
        if (superClass != null && superClass != Object.class) {
            for (Class<?> clazz : beanClasses) {
                if (clazz == superClass) {
                    doResolve(createBeanDefinition(clazz), beanClasses);
                }
            }
        }

        resolveConstructorInfo(beanDefinition);

        resolveFieldInfo(beanDefinition);

        resolveMethodInfo(beanDefinition);

        beanDefinition.setResolved(true);

    }

    private void resolveConstructorInfo(BeanDefinition beanDefinition) throws ResolvedException {
        Class<?> candidate = beanDefinition.getBeanClass();
        Constructor<?>[] constructors = Arrays.stream(candidate.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
                .toArray(Constructor<?>[]::new);
        if (constructors.length > 1) {
            throw new ResolvedException("injected constructors are more than one !");
        } else if (constructors.length == 1) {
            Constructor<?> constructor = constructors[0];
            MultInjectedPoint multInjectedPoint = new MultInjectedPoint();
            for (Parameter parameter : constructor.getParameters()) {
                InjectedPoint injectedPoint = new InjectedPoint(parameter.getParameterizedType());
                multInjectedPoint.addInjectedPoint(injectedPoint);
                Class<?> type;
                if (BeanUtils.isProvider(parameter.getType())) {
                    type = ReflectUtils.getActualType(parameter.getParameterizedType());
                } else {
                    type = parameter.getType();
                }
                String beanName = beanNameGenerator.generateBeanName(type);
                beanDefinition.addDependOn(beanName);
                getDelegateBeanFactory().registerDependentForBean(beanDefinition.getBeanName(), beanName);
            }

            beanDefinition.setInjectedConstructor(multInjectedPoint);
        }
    }

    private void resolveFieldInfo(BeanDefinition beanDefinition) throws ResolvedException {
        Class<?> candidate = beanDefinition.getBeanClass();
        Map<String, InjectedPoint> fields = new HashMap<>();

        for (Field field : candidate.getDeclaredFields()) {

            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {

                if (Modifier.isFinal(field.getModifiers())) {
                    throw new ResolvedException(String.format("the field %s is final !", field.getName()));
                }

                Type type = field.getGenericType();
                if (field.isAnnotationPresent(Named.class)) {
                    Named named = field.getAnnotation(Named.class);
                    fields.put(field.getName(), new InjectedPoint(named.value(), type, true));
                } else {
                    fields.put(field.getName(), new InjectedPoint(type));
                }

                if (BeanUtils.isProvider(type)) {
                    type = ReflectUtils.getActualType(type);
                } else {
                    type = field.getType();
                }
                String beanName = beanNameGenerator.generateBeanName((Class<?>) type);
                if (beanDefinition.isPrototype()) {
                    beanDefinition.addDependOn(beanName);
                    getDelegateBeanFactory().registerDependentForBean(beanDefinition.getBeanName(), beanName);
                }
            }
        }
        beanDefinition.setInjectedFields(fields);
    }

    private void resolveMethodInfo(BeanDefinition beanDefinition) throws ResolvedException {
        Class<?> candidate = beanDefinition.getBeanClass();
        Map<String, InjectedPoint> methods = new HashMap<>();
        for (Method method : candidate.getDeclaredMethods()) {

            Inject inject = method.getAnnotation(Inject.class);

            if (inject != null) {
                String methodName = method.getName();
                if (Modifier.isAbstract(method.getModifiers())) {
                    throw new ResolvedException(String.format("the method %s is abstract !", methodName));
                }

                if (!methodName.startsWith("set") && method.getParameterCount() != 1) {
                    throw new ResolvedException(String.format("the method %s is not setter !", methodName));
                }

                Type type = method.getGenericParameterTypes()[0];

                InjectedPoint injectedPoint = new InjectedPoint(type);

                if (BeanUtils.isProvider(type)) {
                    type = ReflectUtils.getActualType(type);
                }
                String beanName = beanNameGenerator.generateBeanName((Class<?>) type);
                if (beanDefinition.isPrototype()) {
                    beanDefinition.addDependOn(beanName);
                    getDelegateBeanFactory().registerDependentForBean(beanDefinition.getBeanName(), beanName);
                }

                methods.put(methodName, injectedPoint);
            }

            if (method.isAnnotationPresent(PostConstruct.class)) {
                ManagedBeanDefinition managedBeanDefinition = (ManagedBeanDefinition) beanDefinition;
                managedBeanDefinition.setInitializingMethod(method.getName());
            }

            if (method.isAnnotationPresent(PreDestroy.class)) {
                ManagedBeanDefinition managedBeanDefinition = (ManagedBeanDefinition) beanDefinition;
                managedBeanDefinition.setDestroyMethod(method.getName());
            }

        }
        beanDefinition.setInjectedSetters(methods);
    }

    protected boolean checkIfComponent(Class<?> candidate) {
        return candidate.isAnnotationPresent(Named.class);
    }

    @Override
    public void setParentBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof AbstractAutoInjectedBeanFactory) {
            delegateBeanFactory = (AbstractAutoInjectedBeanFactory) beanFactory;
        }
        //todo:unsupport
        throw new IllegalStateException("");
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return delegateBeanFactory;
    }

    protected AbstractAutoInjectedBeanFactory getDelegateBeanFactory() {
        return delegateBeanFactory;
    }

    public void destroy(){
        getDelegateBeanFactory().destorySingletons();
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

}
