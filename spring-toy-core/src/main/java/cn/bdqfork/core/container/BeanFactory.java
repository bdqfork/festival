package cn.bdqfork.core.container;


import cn.bdqfork.core.annotation.ScopeType;
import cn.bdqfork.core.exception.ConstructorInjectedException;
import cn.bdqfork.core.exception.FieldInjectedException;
import cn.bdqfork.core.exception.InjectedException;
import cn.bdqfork.core.exception.SpringToyException;
import cn.bdqfork.core.proxy.CglibMethodInterceptor;
import cn.bdqfork.core.proxy.JdkInvocationHandler;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author bdq
 * @date 2019-02-16
 */
public class BeanFactory implements ObjectFactory<Object> {
    /**
     * 容器类，负责管理bean
     */
    private BeanContainer beanContainer;
    /**
     * 单实例
     */
    private Object instance;
    /**
     * 代理类
     */
    private Object proxyInstance;
    /**
     * Bean信息描述
     */
    private BeanDefinition beanDefinition;
    /**
     * 是否单例
     */
    private boolean isSingleton;
    /**
     * 是否正在实例化
     */
    private boolean instanting;

    public BeanFactory(BeanContainer beanContainer, BeanDefinition beanDefinition) {
        this.beanContainer = beanContainer;
        this.beanDefinition = beanDefinition;
        this.isSingleton = ScopeType.SINGLETON.equals(beanDefinition.getScope());
    }

    /**
     * 获取对象实例，如果bean是单例的，则每次都返回同一个实例，如果不是，则每次都创建一个新的实例。
     *
     * @return Object
     */
    public Object getInstance() throws InjectedException {
        if (!isSingleton()) {
            newBean();
            doFieldInject();
            doMethodInject();
        }
        return proxyInstance;
    }

    public void newBean() {
        ConstructorAttribute constructorAttribute = beanDefinition.getConstructorAttribute();
        if (constructorAttribute != null) {
            instance = doConstructorInject(constructorAttribute);
        }

        if (instance == null) {
            try {
                instance = beanDefinition.getClazz().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new InjectedException("failed to init entity : " + beanDefinition.getName(), e);
            }
        }
        Class<?>[] classes = beanDefinition.getClazz().getInterfaces();
        if (classes.length != 0) {
            JdkInvocationHandler jdkInvocationHandler = new JdkInvocationHandler();
            proxyInstance = jdkInvocationHandler.newProxyInstance(instance);
        } else {
            CglibMethodInterceptor cglibMethodInterceptor = new CglibMethodInterceptor();
            proxyInstance = cglibMethodInterceptor.newProxyInstance(instance);
        }
    }

    private Object doConstructorInject(ConstructorAttribute constructorAttribute) {
        instanting = true;
        Constructor<?> constructor = constructorAttribute.getConstructor();
        List<Object> args = getArguments(constructorAttribute.getArgs());
        //反射调用构造器，构造对象实例
        try {
            Object instance = constructor.newInstance(args.toArray());
            instanting = false;
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ConstructorInjectedException(String.format("failed to do constructor inject for entity %s !", beanDefinition.getName()), e);
        }
    }

    public void doFieldInject() {
        instanting = true;
        for (FieldAttribute fieldAttribute : beanDefinition.getFieldAttributes()) {
            Field field = fieldAttribute.getField();
            field.setAccessible(true);
            try {
                BeanFactory beanFactory = getReference(fieldAttribute.getBeanName(), fieldAttribute.getType());
                if (beanFactory != null) {
                    if (beanFactory.isInstanting()) {
                        throw new FieldInjectedException(String.format("failed to inject entity: %s by field , there is circular reference !", beanDefinition.getName()), null);
                    }

                    if (fieldAttribute.isProvider()) {
                        field.set(instance, beanFactory);
                    } else {
                        field.set(instance, beanFactory.getInstance());
                    }
                } else if (fieldAttribute.isRequired()) {
                    throw new FieldInjectedException(String.format("failed to inject entity: %s by field , " +
                            "there is no match reference bean type %s !", beanDefinition.getName(), fieldAttribute.getType().getSimpleName()), null);
                }
            } catch (IllegalAccessException | SpringToyException e) {
                throw new FieldInjectedException(String.format("failed to inject entity: %s by field!", beanDefinition.getName()), e);
            }
        }
        instanting = false;
    }

    public void doMethodInject() {
        for (MethodAttribute methodAttribute : beanDefinition.getMethodAttributes()) {
            Method method = methodAttribute.getMethod();
            method.setAccessible(true);
            List<Object> args = getArguments(methodAttribute.getArgs());
            try {
                method.invoke(instance, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new FieldInjectedException(String.format("failed to inject entity: %s by method!", beanDefinition.getName()), e);
            }
        }
    }

    private List<Object> getArguments(List<ParameterAttribute> parameterAttributes) {
        List<Object> args = new ArrayList<>(parameterAttributes.size());
        //遍历构造函数的参数依赖信息
        for (ParameterAttribute parameterAttribute : parameterAttributes) {
            BeanFactory beanFactory = getReference(parameterAttribute.getBeanName(), parameterAttribute.getType());
            if (beanFactory.isInstanting()) {
                throw new FieldInjectedException(String.format("failed to inject entity: %s by field , there has circular reference !", beanDefinition.getName()), null);
            }
            //判断是否是Provider
            if (parameterAttribute.isProvider()) {
                //添加实例到Provider参数
                args.add(beanFactory);
            } else {
                //添加实例作为参数
                args.add(beanFactory.getInstance());
            }
        }
        return args;
    }

    private BeanFactory getReference(String beanName, Class<?> type) {
        BeanFactory beanFactory = beanContainer.getBean(beanName);

        if (beanFactory != null && !beanFactory.getBeanDefinition().isType(type)) {
            throw new FieldInjectedException(String.format("failed to inject entity: %s by field , " +
                    "there is no match reference bean named %s !", beanDefinition.getName(), beanName), null);
        } else if (beanFactory == null) {
            //如果指定依赖名和默认依赖名都没有找到Bean，则按类型进行匹配
            Map<String, BeanFactory> beanFactories = beanContainer.getBeans(type);
            for (BeanFactory factory : beanFactories.values()) {
                BeanDefinition beanDefinition = factory.getBeanDefinition();
                if (beanDefinition.isType(type)) {
                    beanFactory = factory;
                    break;
                } else if (beanDefinition.isSubType(type)) {
                    beanFactory = factory;
                    break;
                }
            }
        }
        return beanFactory;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public boolean isInstanting() {
        return instanting;
    }

    public boolean isLazy() {
        return beanDefinition.isLazy();
    }

    public BeanDefinition getBeanDefinition() {
        return beanDefinition;
    }

    @Override
    public Object get() {
        return getInstance();
    }
}
