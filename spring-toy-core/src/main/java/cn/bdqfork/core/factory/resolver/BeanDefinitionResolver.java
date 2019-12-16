package cn.bdqfork.core.factory.resolver;

import cn.bdqfork.core.factory.*;
import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.exception.ScopeException;

import javax.inject.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * 解析BeanDefinition
 *
 * @author bdq
 * @since 2019-02-22
 */
public class BeanDefinitionResolver implements Resolver {
    /**
     * BeanName生成器
     */
    private BeanNameGenerator beanNameGenerator;
    /**
     * 待解析的类
     */
    private Collection<Class<?>> classes;

    public BeanDefinitionResolver(BeanNameGenerator beanNameGenerator, Collection<Class<?>> classes) {
        this.beanNameGenerator = beanNameGenerator;
        this.classes = classes;
    }

    @Override
    public Map<String, BeanDefinition> resolve() throws ResolvedException {
        Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
        for (Class<?> clazz : classes) {
            BeanDefinition beanDefinition = createBeanDefinition(clazz);
            doResolve(beanDefinition);
            beanDefinitions.put(beanDefinition.getBeanName(), beanDefinition);
        }
        return beanDefinitions;
    }

    private BeanDefinition createBeanDefinition(Class<?> clazz) throws ScopeException {
        String name = clazz.getAnnotation(Named.class).value();
        if ("".equals(name)) {
            name = this.beanNameGenerator.generateBeanName(clazz);
        }
        if (clazz.getAnnotation(Singleton.class) == null) {
            return new BeanDefinition(name, clazz, BeanDefinition.SINGLETON);
        } else if (clazz.isAnnotationPresent(Scope.class)) {
            //todo:添加异常信息
            throw new ScopeException("");
        } else {
            return new BeanDefinition(name, clazz);
        }
    }

    private void doResolve(BeanDefinition beanDefinition) throws ResolvedException {
        //如果已经解析过了，则返回
        if (beanDefinition.isResolved()) {
            return;
        }
        //优先解析父类
        Class<?> superClass = beanDefinition.getBeanClass().getSuperclass();
        if (superClass != null && superClass != Object.class) {
            for (Class<?> clazz : classes) {
                if (clazz == superClass) {
                    doResolve(createBeanDefinition(clazz));
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
            //todo:添加异常信息
            throw new ResolvedException("");
        } else if (constructors.length == 1) {
            beanDefinition.setConstructor(constructors[0]);
        }
    }

    private void resolveFieldInfo(BeanDefinition beanDefinition) throws ResolvedException {
        Class<?> candidate = beanDefinition.getBeanClass();
        Set<Field> fields = new HashSet<>();

        for (Field field : candidate.getDeclaredFields()) {
            field.setAccessible(true);

            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {

                if (Modifier.isFinal(field.getModifiers())) {
                    throw new ResolvedException(String.format("the field %s is final !", field.getName()));
                }
                fields.add(field);
            }
        }
        beanDefinition.setFields(fields);
    }

    private void resolveMethodInfo(BeanDefinition beanDefinition) throws ResolvedException {
        Class<?> candidate = beanDefinition.getBeanClass();
        Set<Method> methods = new HashSet<>();
        for (Method method : candidate.getDeclaredMethods()) {

            method.setAccessible(true);
            Inject inject = method.getAnnotation(Inject.class);

            if (inject != null) {
                String methodName = method.getName();
                if (Modifier.isAbstract(method.getModifiers())) {
                    throw new ResolvedException(String.format("the method %s is abstract !", methodName));
                }

                if (!methodName.startsWith("set")) {
                    throw new ResolvedException(String.format("the method %s is not setter !", methodName));
                }

                methods.add(method);
            }
        }
        beanDefinition.setMethods(methods);
    }

}
