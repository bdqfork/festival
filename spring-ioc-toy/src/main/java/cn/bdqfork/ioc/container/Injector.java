package cn.bdqfork.ioc.container;

import cn.bdqfork.ioc.annotation.AutoWired;
import cn.bdqfork.ioc.annotation.Qualifier;
import cn.bdqfork.ioc.exception.SpringToyException;
import cn.bdqfork.ioc.generator.BeanNameGenerator;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @date 2019-02-13
 */
public class Injector {
    private Constructor<?> constructor;
    private List<ParameterInjectorData> constructorParameterDatas;
    private List<FieldInjectorData> fieldInjectorDatas;
    private List<MethodInjectorAttribute> methodInjectorAttributes;

    public Injector(Class<?> clazz, BeanNameGenerator beanNameGenerator) throws SpringToyException {
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
                this.constructorParameterDatas = getParameterInjectDatas(beanNameGenerator, autoWired.required(), constructor.getParameters());
                this.constructor = constructor;
            }
        }
    }

    private void initFieldInjectorDatas(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws SpringToyException {
        List<FieldInjectorData> fieldInjectorDatas = new ArrayList<>();

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
        this.fieldInjectorDatas = fieldInjectorDatas;
    }

    private void initMethodInjectorAttributes(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws SpringToyException {
        Method[] methods = candidate.getDeclaredMethods();
        List<MethodInjectorAttribute> methodInjectorAttributes = new ArrayList<>();
        for (Method method : methods) {
            method.setAccessible(true);
            AutoWired autoWired = method.getAnnotation(AutoWired.class);
            if (autoWired != null) {
                if (Modifier.isAbstract(method.getModifiers())) {
                    throw new SpringToyException("the method: " + method.getName() + "is abstract , it can't be injected !");
                }
                List<ParameterInjectorData> parameterInjectorDatas = getParameterInjectDatas(beanNameGenerator, autoWired.required(), method.getParameters());
                methodInjectorAttributes.add(new MethodInjectorAttribute(method, parameterInjectorDatas));
            }
        }
        this.methodInjectorAttributes = methodInjectorAttributes;
    }

    private List<ParameterInjectorData> getParameterInjectDatas(BeanNameGenerator beanNameGenerator, boolean required, Parameter[] parameters) {
        List<ParameterInjectorData> parameterInjectorDatas = new ArrayList<>();
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
        Object instance;
        if (constructor != null) {
            List<Object> args = new ArrayList<>(constructorParameterDatas.size());
            for (ParameterInjectorData parameterInjectorData : constructorParameterDatas) {
                injectParameters(beanDefination, args, parameterInjectorData);
            }
            try {
                instance = constructor.newInstance(args.toArray());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new SpringToyException("failed to init bean : " + beanDefination.getName(), e);
            }
        } else {
            try {
                instance = beanDefination.getClazz().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new SpringToyException("failed to init bean : " + beanDefination.getName(), e);
            }
        }
        for (FieldInjectorData fieldInjectorData : fieldInjectorDatas) {
            BeanDefination bean = fieldInjectorData.getBean();
            Field field = fieldInjectorData.getField();
            field.setAccessible(true);
            try {
                field.set(instance, bean.getInstance());
            } catch (IllegalAccessException e) {
                throw new SpringToyException("failed to init bean : " + beanDefination.getName(), e);
            } catch (SpringToyException e) {
                if (!fieldInjectorData.isRequired()) {
                    throw new SpringToyException("failed to init bean : " + beanDefination.getName(), e);
                }
            }
        }
        for (MethodInjectorAttribute methodInjectorAttribute : methodInjectorAttributes) {
            List<Object> args = new LinkedList<>();
            for (ParameterInjectorData parameterInjectorData : methodInjectorAttribute.getParameterInjectorDatas()) {
                injectParameters(beanDefination, args, parameterInjectorData);
            }
            Method method = methodInjectorAttribute.getMethod();
            String methodName = method.getName();
            if (!methodName.startsWith("set")) {
                throw new SpringToyException("failed to init bean : " + beanDefination.getName());
            }
            try {
                method.invoke(instance, args.toArray());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new SpringToyException("failed to init bean : " + beanDefination.getName(), e);
            }
        }
        return instance;
    }

    private void injectParameters(BeanDefination beanDefination, List<Object> args, ParameterInjectorData parameterInjectorData) throws SpringToyException {
        BeanDefination bean = parameterInjectorData.getBean();
        try {
            args.add(bean.getInstance());
        } catch (SpringToyException e) {
            if (!parameterInjectorData.isRequired()) {
                throw new SpringToyException("failed to init bean : " + beanDefination.getName(), e);
            }
        }
    }

    /**
     * 判断当前bean是否依赖beanDefination，如果是，返回true，否则返回false
     *
     * @param beanDefination
     * @return boolean
     */
    public boolean hasDependence(BeanDefination beanDefination) {
        if (constructor != null) {
            for (ParameterInjectorData parameterInjectorData : constructorParameterDatas) {
                if (parameterInjectorData.isMatch(beanDefination)) {
                    return true;
                }
            }
        }
        for (FieldInjectorData fieldInjectorData : fieldInjectorDatas) {
            if (fieldInjectorData.isMatch(beanDefination)) {
                return true;
            }
        }
        return false;
    }

    public List<ParameterInjectorData> getConstructorParameterDatas() {
        return constructorParameterDatas;
    }

    public List<FieldInjectorData> getFieldInjectorDatas() {
        return fieldInjectorDatas;
    }

    public List<MethodInjectorAttribute> getMethodInjectorAttributes() {
        return methodInjectorAttributes;
    }
}
