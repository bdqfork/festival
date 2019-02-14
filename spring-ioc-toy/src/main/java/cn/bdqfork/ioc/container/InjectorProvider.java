package cn.bdqfork.ioc.container;

import cn.bdqfork.ioc.annotation.AutoWired;
import cn.bdqfork.ioc.annotation.Qualifier;
import cn.bdqfork.ioc.exception.InjectedException;
import cn.bdqfork.ioc.exception.ResolvedException;
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
        doResolve(clazz, beanNameGenerator);
    }

    private void doResolve(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws SpringToyException {
        resolveConstructorInfo(candidate, beanNameGenerator);
        resolveFieldInfo(candidate, beanNameGenerator);
        resolveMethodInfo(candidate, beanNameGenerator);
    }

    private void resolveConstructorInfo(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws ResolvedException {
        int count = 0;
        for (Constructor<?> constructor : candidate.getDeclaredConstructors()) {
            AutoWired autoWired = constructor.getAnnotation(AutoWired.class);
            if (autoWired != null) {
                count++;
                if (count > 1) {
                    throw new ResolvedException("the bean: " + candidate.getName() + " has more than one constructor to be injected , it can't be injected !");
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
            if (autoWired != null) {
                if (Modifier.isFinal(field.getModifiers())) {
                    throw new ResolvedException("the field: " + field.getName() + "is final , it can't be injected !");
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

    private void resolveMethodInfo(Class<?> candidate, BeanNameGenerator beanNameGenerator) throws ResolvedException {
        Method[] methods = candidate.getDeclaredMethods();
        List<MethodInjectorAttribute> methodInjectorAttributes = new ArrayList<>();
        List<InjectorData> injectorDatas = new ArrayList<>();
        for (Method method : methods) {
            method.setAccessible(true);
            AutoWired autoWired = method.getAnnotation(AutoWired.class);
            if (autoWired != null) {
                if (Modifier.isAbstract(method.getModifiers())) {
                    throw new ResolvedException("the method: " + method.getName() + "is abstract , it can't be injected !");
                }

                String methodName = method.getName();
                if (!methodName.startsWith("set")) {
                    throw new ResolvedException("the method: " + method.getName() + "is not setter , it can't be injected !");
                }

                List<InjectorData> injectorDataInfo = getParameterInjectDatas(beanNameGenerator, autoWired.required(), method.getParameters());
                methodInjectorAttributes.add(new MethodInjectorAttribute(method, injectorDataInfo, autoWired.required()));
                injectorDatas.addAll(injectorDataInfo);
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
                throw new InjectedException("failed to init bean : " + beanDefination.getName(), e);
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