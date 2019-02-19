package cn.bdqfork.core.container;


import cn.bdqfork.core.annotation.AutoWired;
import cn.bdqfork.core.annotation.Qualifier;
import cn.bdqfork.core.exception.InjectedException;
import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.exception.SpringToyException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bdq
 * @date 2019-02-13
 */
public class InjectorProvider {
    private ConstructorInjector constructorInjector;
    private FieldInjector fieldInjector;
    private MethodInjector methodInjector;

    public InjectorProvider(Class<?> clazz, BeanNameGenerator beanNameGenerator) throws SpringToyException {
        resolve(clazz, beanNameGenerator);
    }

    private void resolve(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws SpringToyException {
        resolveConstructorInfo(candidate, beanNameGenerator);
        resolveFieldInfo(candidate, beanNameGenerator);
        resolveMethodInfo(candidate, beanNameGenerator);
    }

    private void resolveConstructorInfo(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws ResolvedException {
        int count = 0;
        for (Constructor<?> constructor : candidate.getDeclaredConstructors()) {
            AutoWired autoWired = constructor.getAnnotation(AutoWired.class);
            Inject inject = constructor.getAnnotation(Inject.class);
            if (autoWired != null || inject != null) {
                count++;
                if (count > 1) {
                    throw new ResolvedException("the entity: " + candidate.getName() + " has more than one constructor to be injected , it can't be injected !");
                }
                List<InjectorData> injectorDataInfo = getParameterInjectDatas(beanNameGenerator, autoWired.required(), constructor.getParameters());
                this.constructorInjector = new ConstructorInjector(constructor, injectorDataInfo);
            }
        }
    }

    private void resolveFieldInfo(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws SpringToyException {
        List<InjectorData> fieldInjectorDatas = new ArrayList<>();

        for (Field field : candidate.getDeclaredFields()) {
            field.setAccessible(true);

            AutoWired autoWired = field.getAnnotation(AutoWired.class);
            Inject inject = field.getAnnotation(Inject.class);
            if (autoWired != null || inject != null) {

                if (Modifier.isFinal(field.getModifiers())) {
                    throw new ResolvedException("the field: " + field.getName() + "is final , it can't be injected !");
                }

                String refName = null;

                Qualifier qualifier = field.getAnnotation(Qualifier.class);
                Named named = field.getAnnotation(Named.class);
                if (qualifier != null) {
                    refName = qualifier.value();
                } else if (named != null) {
                    refName = named.value();
                }

                boolean isRequire = false;
                if (autoWired != null) {
                    isRequire = autoWired.required();
                }

                String defaultName = beanNameGenerator.generateBeanName(field.getType());

                InjectorData injectorData = new FieldInjectorData(defaultName, refName, isRequire, field);

                if (field.getType() == BeanFactory.class || field.getType() == Provider.class) {
                    Type type = field.getGenericType();
                    setProviderInfo(beanNameGenerator, injectorData, (ParameterizedType) type);
                }

                fieldInjectorDatas.add(injectorData);
            }
        }
        this.fieldInjector = new FieldInjector(fieldInjectorDatas);
    }

    private void setProviderInfo(BeanNameGenerator beanNameGenerator, InjectorData injectorData, ParameterizedType type) throws ResolvedException {
        String defaultName;
        Class<?> providedType;
        try {
            providedType = Class.forName(type.getActualTypeArguments()[0].getTypeName());
        } catch (ClassNotFoundException e) {
            throw new ResolvedException(String.format("class %s is not found !", type.getTypeName()), e);
        }
        injectorData.setProvider(true);
        defaultName = beanNameGenerator.generateBeanName(providedType);
        injectorData.setDefaultName(defaultName);
        injectorData.setProvidedType(providedType);
    }

    private void resolveMethodInfo(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws ResolvedException {
        Method[] methods = candidate.getDeclaredMethods();
        List<MethodInjectorAttribute> methodInjectorAttributes = new ArrayList<>();
        List<InjectorData> injectorDatas = new ArrayList<>();
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
                boolean isRequire = false;
                if (autoWired != null) {
                    isRequire = true;
                }
                List<InjectorData> injectorDataInfo = getParameterInjectDatas(beanNameGenerator, isRequire, method.getParameters());
                methodInjectorAttributes.add(new MethodInjectorAttribute(method, injectorDataInfo, isRequire));
                injectorDatas.addAll(injectorDataInfo);
            }
        }
        this.methodInjector = new MethodInjector(methodInjectorAttributes, injectorDatas);
    }

    private List<InjectorData> getParameterInjectDatas(BeanNameGenerator beanNameGenerator, boolean required, Parameter[] parameters) throws ResolvedException {

        List<InjectorData> parameterInjectorDatas = new ArrayList<>();
        if (parameters.length == 0) {
            return parameterInjectorDatas;
        }

        for (Parameter parameter : parameters) {
            String defaultName = beanNameGenerator.generateBeanName(parameter.getType());
            String refName = parameter.getName();
            InjectorData injectorData = new ParameterInjectorData(defaultName, refName, required, parameter);
            if (parameter.getType() == BeanFactory.class || parameter.getType() == Provider.class) {
                Type type = parameter.getParameterizedType();
                setProviderInfo(beanNameGenerator, injectorData, (ParameterizedType) type);
            }
            parameterInjectorDatas.add(injectorData);
        }
        return parameterInjectorDatas;
    }


    /**
     * 注入依赖
     *
     * @param beanDefination
     * @return
     * @throws InjectedException
     */
    public Object doInject(BeanDefination beanDefination) throws InjectedException {
        Object instance = null;

        if (constructorInjector != null) {
            instance = this.constructorInjector.inject(beanDefination);
        }

        if (instance == null) {
            try {
                instance = beanDefination.getClazz().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new InjectedException("failed to init entity : " + beanDefination.getName(), e);
            }
        }

        if (fieldInjector != null) {
            instance = this.fieldInjector.inject(instance, beanDefination);
        }

        if (methodInjector != null) {
            instance = this.methodInjector.inject(instance, beanDefination);
        }

        return instance;
    }

    /**
     * 判断当前bean是否依赖beanDefination，如果是，返回true，否则返回false
     *
     * @param beanDefination
     * @return boolean
     */
    public boolean hasDependence(BeanDefination beanDefination) {

        if (constructorInjector != null && constructorInjector.hasDependence(beanDefination)) {
            return true;
        }

        if (fieldInjector != null && fieldInjector.hasDependence(beanDefination)) {
            return true;
        }

        if (methodInjector != null && methodInjector.hasDependence(beanDefination)) {
            return true;
        }
        return false;
    }

    public List<InjectorData> getConstructorParameterDatas() {
        if (constructorInjector != null) {
            List<InjectorData> injectorDatas = constructorInjector.getConstructorParameterDatas();
            if (injectorDatas != null) {
                return injectorDatas;
            }
        }
        return null;
    }

    public List<InjectorData> getFieldInjectorDatas() {
        if (fieldInjector != null) {
            List<InjectorData> injectorDatas = fieldInjector.getFieldInjectorDatas();
            if (injectorDatas != null) {
                return injectorDatas;
            }
        }
        return null;
    }

    public List<MethodInjectorAttribute> getMethodInjectorAttributes() {
        if (methodInjector != null) {
            List<MethodInjectorAttribute> methodInjectorAttributes = methodInjector.getMethodInjectorAttributes();
            if (methodInjectorAttributes != null) {
                return methodInjectorAttributes;
            }
        }
        return null;
    }
}
