package cn.bdqfork.core.container;

import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Qualifier;
import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.exception.SpringToyException;
import cn.bdqfork.core.exception.UnsatisfiedBeanException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author bdq
 * @date 2019-02-22
 */
public class Resolver {
    private BeanContainer beanContainer;
    private BeanNameGenerator beanNameGenerator;

    public Resolver(BeanContainer beanContainer, BeanNameGenerator beanNameGenerator) {
        this.beanContainer = beanContainer;
        this.beanNameGenerator = beanNameGenerator;
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
                    throw new ResolvedException("the entity: " + candidate.getName() + " has more than one constructor to be injected , it can't be injected !");
                }
                List<ParameterAttribute> parameterAttributes = resolveParameterAttributes(constructor.getParameters());
                ConstructorAttribute attribute = new ConstructorAttribute(constructor, parameterAttributes);
                beanDefinition.setConstructorAttribute(attribute);
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
                    throw new ResolvedException("the field: " + field.getName() + "is final , it can't be injected !");
                }

                boolean isRequire = autoWired != null && autoWired.required();

                String refName = field.getName();
                //Qualifier优先级比Named高
                Named named = field.getAnnotation(Named.class);
                if (named != null) {
                    refName = named.value();
                }
                Qualifier qualifier = field.getAnnotation(Qualifier.class);
                if (qualifier != null) {
                    refName = qualifier.value();
                }
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

    private void resolveMethodInfo(BeanDefinition beanDefinition) throws ResolvedException {
        Class<?> candidate = beanDefinition.getClazz();
        Method[] methods = candidate.getDeclaredMethods();
        List<MethodAttribute> methodAttributes = new LinkedList<>();
        for (Method method : methods) {
            method.setAccessible(true);
            AutoWired autoWired = method.getAnnotation(AutoWired.class);
            Inject inject = method.getAnnotation(Inject.class);
            if (autoWired != null || inject != null) {
                if (Modifier.isAbstract(method.getModifiers())) {
                    throw new ResolvedException("the method: " + method.getName() + "is abstract , it can't be injected !");
                }

                String methodName = method.getName();
                if (!methodName.startsWith("set")) {
                    throw new ResolvedException("the method: " + method.getName() + "is not setter , it can't be injected !");
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

    private List<ParameterAttribute> resolveParameterAttributes(Parameter[] parameters) {
        List<ParameterAttribute> parameterAttributes = new ArrayList<>(parameters.length);
        for (Parameter parameter : parameters) {
            String beanName = parameter.getName();
            Named named = parameter.getAnnotation(Named.class);
            if (named != null) {
                beanName = named.value();
            }
            Class<?> type = parameter.getType();
            boolean isProvider = isProvider(type);
            if (isProvider) {
                type = getActualType((ParameterizedType) parameter.getParameterizedType());
            }
            ParameterAttribute parameterAttribute = new ParameterAttribute(beanName, parameter, type, isProvider(parameter.getType()));
            parameterAttributes.add(parameterAttribute);
        }
        return parameterAttributes;
    }

    private Class<?> getActualType(ParameterizedType type) {
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
