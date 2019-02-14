package cn.bdqfork.ioc.container;

import cn.bdqfork.ioc.annotation.AutoWired;
import cn.bdqfork.ioc.annotation.Qualifier;
import cn.bdqfork.ioc.exception.SpringToyException;
import cn.bdqfork.ioc.generator.BeanNameGenerator;

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
        init(clazz, beanNameGenerator);
    }

    private void init(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws SpringToyException {
        initConstructorInfo(candidate, beanNameGenerator);
        initFieldInjectorDatas(candidate, beanNameGenerator);
        initMethodInjectorAttributes(candidate, beanNameGenerator);
    }

    private void initConstructorInfo(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws SpringToyException {
        int count = 0;
        for (Constructor<?> constructor : candidate.getDeclaredConstructors()) {
            AutoWired autoWired = constructor.getAnnotation(AutoWired.class);
            if (autoWired != null) {
                count++;
                if (count > 1) {
                    throw new SpringToyException("");
                }
                List<InjectorData> parameterInjectorDatas = getParameterInjectDatas(beanNameGenerator, autoWired.required(), constructor.getParameters());
                this.constructorInjector = new ConstructorInjector(constructor, parameterInjectorDatas, autoWired.required());
            }
        }
    }

    private void initFieldInjectorDatas(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws SpringToyException {
        List<InjectorData> fieldInjectorDatas = new ArrayList<>();

        for (Field field : candidate.getDeclaredFields()) {
            field.setAccessible(true);

            AutoWired autoWired = field.getAnnotation(AutoWired.class);
            if (autoWired != null) {
                if (Modifier.isFinal(field.getModifiers())) {
                    throw new SpringToyException("the field: " + field.getName() + "is final , it can't be injected !");
                }
                String refName = null;

                Qualifier qualifier = field.getAnnotation(Qualifier.class);
                if (qualifier != null) {
                    refName = qualifier.value();
                }

                String defaultName = beanNameGenerator.generateBeanName(field.getType());
                fieldInjectorDatas.add(new FieldInjectorData(defaultName, refName, autoWired.required(), field));
            }
        }
        this.fieldInjector = new FieldInjector(fieldInjectorDatas);
    }

    private void initMethodInjectorAttributes(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws SpringToyException {
        Method[] methods = candidate.getDeclaredMethods();
        List<MethodInjectorAttribute> methodInjectorAttributes = new ArrayList<>();
        List<InjectorData> injectorDatas = new ArrayList<>();
        for (Method method : methods) {
            method.setAccessible(true);
            AutoWired autoWired = method.getAnnotation(AutoWired.class);
            if (autoWired != null) {
                if (Modifier.isAbstract(method.getModifiers())) {
                    throw new SpringToyException("the method: " + method.getName() + "is abstract , it can't be injected !");
                }
                List<InjectorData> parameterInjectorDatas = getParameterInjectDatas(beanNameGenerator, autoWired.required(), method.getParameters());
                methodInjectorAttributes.add(new MethodInjectorAttribute(method, parameterInjectorDatas, autoWired.required()));
                injectorDatas.addAll(parameterInjectorDatas);
            }
        }
        this.methodInjector = new MethodInjector(methodInjectorAttributes, injectorDatas);
    }

    private List<InjectorData> getParameterInjectDatas(BeanNameGenerator beanNameGenerator, boolean required, Parameter[] parameters) {
        List<InjectorData> parameterInjectorDatas = new ArrayList<>();
        if (parameters.length == 0) {
            return parameterInjectorDatas;
        }
        for (Parameter parameter : parameters) {
            String defaultNmae = beanNameGenerator.generateBeanName(parameter.getType());
            String refName = parameter.getName();
            parameterInjectorDatas.add(new ParameterInjectorData(defaultNmae, refName, required, parameter));
        }
        return parameterInjectorDatas;
    }

    public Object doInject(BeanDefination beanDefination) throws SpringToyException {
        Object instance = null;
        if (constructorInjector != null) {
            instance = this.constructorInjector.inject(beanDefination);
        }
        if (instance == null) {
            try {
                instance = beanDefination.getClazz().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new SpringToyException("failed to init bean : " + beanDefination.getName(), e);
            }
        }
        if (fieldInjector!=null){
            instance = this.fieldInjector.inject(instance, beanDefination);
        }
        if (methodInjector!=null){
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
