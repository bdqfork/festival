package cn.bdqfork.core.container.resolver;

import cn.bdqfork.core.annotation.*;
import cn.bdqfork.core.container.*;
import cn.bdqfork.core.container.resolver.Resolver;
import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.exception.ScopeException;
import cn.bdqfork.core.utils.ComponentUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author bdq
 * @since 2019-02-22
 */
public class BeanDefinitionResolver implements Resolver<Map<String, BeanDefinition>> {
    private BeanNameGenerator beanNameGenerator;
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
        String name = ComponentUtils.getComponentName(clazz);
        String beanScope = "singleton";
        if (clazz.getAnnotation(Singleton.class) == null) {

            Scope scope = clazz.getAnnotation(Scope.class);

            if (scope != null) {
                if (!ScopeType.PROTOTYPE.equals(scope.value())) {
                    throw new ScopeException("the value of scope is error !");
                } else {
                    beanScope = scope.value();
                }
            }

        }
        if ("".equals(name)) {
            name = this.beanNameGenerator.generateBeanName(clazz);
        }

        Lazy lazy = clazz.getAnnotation(Lazy.class);
        boolean isLazy = false;
        if (lazy != null) {
            isLazy = lazy.value();
        }
        return new BeanDefinition(clazz, beanScope, name, isLazy);
    }

    private void doResolve(BeanDefinition beanDefinition) throws ResolvedException {
        //如果已经解析过了，则返回
        if (beanDefinition.isResolved()) {
            return;
        }
        //优先解析父类
        Class<?> superClass = beanDefinition.getClazz().getSuperclass();
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
        Class<?> candidate = beanDefinition.getClazz();
        int count = 0;
        for (Constructor<?> constructor : candidate.getDeclaredConstructors()) {
            AutoWired autoWired = constructor.getAnnotation(AutoWired.class);
            Inject inject = constructor.getAnnotation(Inject.class);
            if (autoWired != null || inject != null) {
                count++;
                if (count > 1) {
                    throw new ResolvedException(String.format("the entity named %s has more than one constructor to be injected !",
                            candidate.getName()));
                }

                List<ParameterAttribute> parameterAttributes = resolveParameterAttributes(constructor.getParameters());
                ConstructorAttribute constructorAttribute = new ConstructorAttribute(constructor, parameterAttributes);
                beanDefinition.setConstructorAttribute(constructorAttribute);
            }
        }
    }

    private void resolveFieldInfo(BeanDefinition beanDefinition) throws ResolvedException {
        Class<?> candidate = beanDefinition.getClazz();
        List<FieldAttribute> fieldAttributes = new LinkedList<>();

        for (Field field : candidate.getDeclaredFields()) {
            field.setAccessible(true);

            AutoWired autoWired = field.getAnnotation(AutoWired.class);
            Inject inject = field.getAnnotation(Inject.class);
            if (autoWired != null || inject != null) {

                if (Modifier.isFinal(field.getModifiers())) {
                    throw new ResolvedException(String.format("the field %s is final !", field.getName()));
                }

                boolean isRequire = autoWired != null && autoWired.required();

                //获取依赖类型
                Class<?> type = field.getType();
                boolean isProvider = isProvider(type);
                if (isProvider) {
                    type = getActualType((ParameterizedType) field.getGenericType());
                }

                //Qualifier优先级比Named高
                String refName = getIfNamed(type, field.getAnnotation(Named.class));

                Qualifier qualifier = field.getAnnotation(Qualifier.class);
                if (qualifier != null) {
                    refName = qualifier.value();
                }

                FieldAttribute fieldAttribute = new FieldAttribute(refName, field, type, isRequire, isProvider(field.getType()));
                fieldAttributes.add(fieldAttribute);
            }
        }
        beanDefinition.setFieldAttributes(fieldAttributes);
    }

    private String getIfNamed(Class<?> clazz, Named named) {
        //获取依赖的BeanName
        String refName = beanNameGenerator.generateBeanName(clazz);
        if (named != null) {
            refName = named.value();
        }
        return refName;
    }

    private void resolveMethodInfo(BeanDefinition beanDefinition) throws ResolvedException {
        Class<?> candidate = beanDefinition.getClazz();
        Method[] methods = candidate.getDeclaredMethods();
        List<MethodAttribute> methodAttributes = new LinkedList<>();
        for (Method method : methods) {

            method.setAccessible(true);
            AutoWired autoWired = method.getAnnotation(AutoWired.class);
            Inject inject = method.getAnnotation(Inject.class);

            if (autoWired != null || inject != null) {
                String methodName = method.getName();
                if (Modifier.isAbstract(method.getModifiers())) {
                    throw new ResolvedException(String.format("the method %s is abstract !", methodName));
                }

                if (!methodName.startsWith("set")) {
                    throw new ResolvedException(String.format("the method %s is not setter !", methodName));
                }

                List<ParameterAttribute> parameterAttributes = resolveParameterAttributes(method.getParameters());

                boolean isRequire = false;
                if (autoWired != null) {
                    isRequire = true;
                }

                MethodAttribute methodAttribute = new MethodAttribute(method, parameterAttributes, isRequire);
                methodAttributes.add(methodAttribute);
            }
        }
        beanDefinition.setMethodAttributes(methodAttributes);
    }

    private List<ParameterAttribute> resolveParameterAttributes(Parameter[] parameters) throws ResolvedException {
        List<ParameterAttribute> parameterAttributes = new LinkedList<>();

        for (Parameter parameter : parameters) {
            //获取依赖类型
            Class<?> type = parameter.getType();
            boolean isProvider = isProvider(type);
            if (isProvider) {
                type = getActualType((ParameterizedType) parameter.getParameterizedType());
            }
            //获取依赖BeanName
            String refName = getIfNamed(type, parameter.getAnnotation(Named.class));

            ParameterAttribute parameterAttribute = new ParameterAttribute(refName, type, isProvider(parameter.getType()));
            parameterAttributes.add(parameterAttribute);
        }
        return parameterAttributes;
    }

    private Class<?> getActualType(ParameterizedType type) throws ResolvedException {
        Class<?> providedType;
        try {
            providedType = Class.forName(type.getActualTypeArguments()[0].getTypeName());
        } catch (ClassNotFoundException e) {
            throw new ResolvedException(String.format("class %s is not found !", type.getTypeName()), e);
        }
        return providedType;
    }

    private boolean isProvider(Class<?> clazz) {
        return clazz == ObjectFactory.class || clazz == Provider.class;
    }

}
