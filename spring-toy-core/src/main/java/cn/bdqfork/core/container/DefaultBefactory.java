package cn.bdqfork.core.container;

import cn.bdqfork.core.exception.BeansException;

import javax.inject.Provider;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bdq
 * @since 2019/12/15
 */
public class DefaultBefactory extends AbstractBeanFactory {
    @Override
    protected boolean isDependent(String dependOn, String beanName) {
        if (dependOn.equals(beanName)) {
            return true;
        }
        Map<String, Boolean> trace = new HashMap<>();
        trace.put(beanName, true);
        trace.put(dependOn, true);
        return isDependent(dependOn, trace);
    }

    private boolean isDependent(String dependOn, Map<String, Boolean> trace) {
        BeanDefinition beanDefinition = getBeanDefination(dependOn);
        for (String depend : beanDefinition.getDependOns()) {
            if (trace.containsKey(depend)) {
                return true;
            }
            if (isDependent(depend, trace)) {
                return true;
            }
            trace.put(depend, true);
        }
        return false;
    }

    @Override
    protected Object getSingleton(String beanName) {
        if (instances.containsKey(beanName)) {
            return instances.get(beanName);
        }
        return null;
    }

    @Override
    protected Object doCreateBean(String beanName, Constructor<?> constructor, Object[] args) throws BeansException {
        Object bean;
        try {
            bean = constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeansException(e);
        }
        inject(beanName, bean);
        return bean;
    }

    protected void inject(String beanName, Object bean) throws BeansException {
        BeanDefinition beanDefinition = getBeanDefination(beanName);
        if (beanDefinition.getFieldAttributes() != null) {
            for (FieldAttribute fieldAttribute : beanDefinition.getFieldAttributes()) {
                Object fieldValue = resovleDependency(fieldAttribute.getType(), fieldAttribute.getBeanName());
                if (fieldAttribute.isProvider()) {
                    Field field = fieldAttribute.getField();
                    field.setAccessible(true);
                    try {
                        field.set(bean, fieldValue);
                    } catch (IllegalAccessException e) {
                        throw new BeansException(e);
                    }
                }
            }
        }
        if (beanDefinition.getMethodAttributes() != null) {
            for (MethodAttribute methodAttribute : beanDefinition.getMethodAttributes()) {
                List<Object> args = new ArrayList<>(methodAttribute.getArgs().size());
                for (ParameterAttribute attribute : methodAttribute.getArgs()) {
                    Object argValue = resovleDependency(attribute.getType(), attribute.getBeanName());
                    args.add(argValue);
                }
                Method method = methodAttribute.getMethod();
                method.setAccessible(true);
                try {
                    method.invoke(bean, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new BeansException(e);
                }
            }
        }
    }
}
