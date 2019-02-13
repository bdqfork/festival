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
public class Injector {
    private Constructor<?> constructor;
    private List<ParameterInjectorData> constructorParameterDatas;
    private List<FieldInjectorData> fieldInjectorDatas;

    public Injector(Class<?> clazz, BeanNameGenerator beanNameGenerator) throws SpringToyException {
        init(clazz, beanNameGenerator);
    }

    private void init(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws SpringToyException {
        initConstructorInfo(candidate, beanNameGenerator);
        fieldInjectorDatas(candidate, beanNameGenerator);
    }

    private void initConstructorInfo(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws SpringToyException {
        int count = 0;
        List<ParameterInjectorData> parameterInjectorDatas = new ArrayList<>();
        for (Constructor<?> constructor : candidate.getDeclaredConstructors()) {
            Parameter[] parameters = constructor.getParameters();
            if (parameters.length == 0) {
                continue;
            }
            AutoWired autoWired = constructor.getAnnotation(AutoWired.class);
            if (autoWired != null) {
                count++;
                if (count > 1) {
                    throw new SpringToyException("");
                }
                for (Parameter parameter : parameters) {
                    String defaultNmae = beanNameGenerator.generateBeanName(parameter.getType());
                    String refName = parameter.getName();
                    parameterInjectorDatas.add(new ParameterInjectorData(defaultNmae, refName, parameter));
                }
                this.constructor = constructor;
            }
        }
        this.constructorParameterDatas = parameterInjectorDatas;
    }

    private void fieldInjectorDatas(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws SpringToyException {
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
                fieldInjectorDatas.add(new FieldInjectorData(defaultName, refName, field));
            }
        }
        this.fieldInjectorDatas = fieldInjectorDatas;
    }

    public List<ParameterInjectorData> getConstructorParameterDatas() {
        return constructorParameterDatas;
    }

    public List<FieldInjectorData> getFieldInjectorDatas() {
        return fieldInjectorDatas;
    }

    public Object doInject(BeanDefination beanDefination) throws SpringToyException {
        Object instance;
        if (constructor != null) {
            List<Object> args = new ArrayList<>(constructorParameterDatas.size());
            for (ParameterInjectorData parameterInjectorData : constructorParameterDatas) {
                BeanDefination bean = parameterInjectorData.getBean();
                args.add(bean.getInstance());
            }
            try {
                instance = constructor.newInstance(args.toArray());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
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
            }
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
}
