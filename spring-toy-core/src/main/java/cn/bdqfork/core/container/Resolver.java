package cn.bdqfork.core.container;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Qualifier;
import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.exception.SpringToyException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.lang.reflect.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @date 2019-02-22
 */
public class Resolver {
    private BeanContainer beanContainer;

    public Resolver(BeanContainer beanContainer) {
        this.beanContainer = beanContainer;
    }

    public void resolve(BeanDefinition beanDefinition) throws SpringToyException {
        //如果已经解析过了，则返回
        if (beanDefinition.isResolved()) {
            return;
        }
        //优先解析父类
        Class<?> superClass = beanDefinition.getClazz().getSuperclass();
        if (superClass != null && superClass != Object.class) {

            for (BeanFactory beanFactory : beanContainer.getBeans(superClass).values()) {
                BeanDefinition bean = beanFactory.getBeanDefinition();
                if (bean != beanDefinition) {
                    //递归解析父类
                    resolve(bean);
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

    private void resolveFieldInfo(BeanDefinition beanDefinition) throws SpringToyException {
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

                //Qualifier优先级比Named高
                String refName = getIfNamed(field.getName(), field.getAnnotation(Named.class));

                Qualifier qualifier = field.getAnnotation(Qualifier.class);
                if (qualifier != null) {
                    refName = qualifier.value();
                }

                //获取依赖类型
                Class<?> type = field.getType();
                boolean isProvider = isProvider(type);
                if (isProvider) {
                    type = getActualType((ParameterizedType) field.getGenericType());
                }

                FieldAttribute fieldAttribute = new FieldAttribute(refName, field, type, isRequire, isProvider(field.getType()));
                fieldAttributes.add(fieldAttribute);
            }
        }
        beanDefinition.setFieldAttributes(fieldAttributes);
    }

    private String getIfNamed(String defaultName, Named named) {
        //获取依赖的BeanName
        String refName = defaultName;
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
            //获取依赖BeanName
            String refName = getIfNamed(parameter.getName(), parameter.getAnnotation(Named.class));
            //获取依赖类型
            Class<?> type = parameter.getType();
            boolean isProvider = isProvider(type);
            if (isProvider) {
                type = getActualType((ParameterizedType) parameter.getParameterizedType());
            }

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
