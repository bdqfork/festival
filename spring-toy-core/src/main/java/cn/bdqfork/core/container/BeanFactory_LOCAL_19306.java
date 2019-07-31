package cn.bdqfork.core.container;


import cn.bdqfork.core.annotation.ScopeType;
import cn.bdqfork.core.exception.*;
import cn.bdqfork.core.proxy.CglibMethodInterceptor;
import cn.bdqfork.core.proxy.JdkInvocationHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public BeanFactory(BeanContainer beanContainer, BeanDefinition beanDefinition) {
        this.beanContainer = beanContainer;
        this.beanDefinition = beanDefinition;
        this.isSingleton = ScopeType.SINGLETON.equals(beanDefinition.getScope());
    }

    /**
     * 获取对象实例，如果bean是单例的，则每次都返回同一个实例，如果不是，则每次都创建一个新的实例。
     * synchronized 待优化
     *
     * @return Object
     */
    public synchronized Object getInstance() throws InjectedException, InstantiateException {
        if (checkIfNeedInstantiate()) {
            newBean();
        }
        if (checkIfNeedInit()) {
            doFieldInject();
            doMethodInject();
        }
        return instance;
    }

    public void newBean() throws InstantiateException {
        ConstructorAttribute constructorAttribute = beanDefinition.getConstructorAttribute();
        if (constructorAttribute != null) {
            //执行构造器注入
            try {
                instance = doConstructorInject(constructorAttribute);
            } catch (ConstructorInjectedException e) {
                throw new InstantiateException(String.format("failed to instantiate entity %s !",
                        beanDefinition.getBeanName()), e);
            }
        } else {
            try {
                instance = beanDefinition.getClazz().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new InstantiateException(String.format("failed to instantiate entity %s !",
                        beanDefinition.getBeanName()), e);
            }
        }

    }

    private Object doConstructorInject(ConstructorAttribute constructorAttribute) throws ConstructorInjectedException {
        Constructor<?> constructor = constructorAttribute.getConstructor();
        //反射调用构造器，构造对象实例
        try {
            List<Object> args = getArguments(constructorAttribute.getArgs(), true);
            return constructor.newInstance(args.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | UnsatisfiedBeanException e) {
            throw new ConstructorInjectedException(String.format("failed to do constructor inject for entity %s !",
                    beanDefinition.getBeanName()), e);
        }
    }

    public void doFieldInject() throws FieldInjectedException {
        for (FieldAttribute fieldAttribute : beanDefinition.getFieldAttributes()) {
            Field field = fieldAttribute.getField();
            field.setAccessible(true);
            try {
                //获取依赖Bean的工厂实例
                BeanFactory beanFactory = getReference(fieldAttribute.getBeanName(), fieldAttribute.getType());
                if (beanFactory != null) {
                    if (fieldAttribute.isProvider()) {
                        field.set(instance, beanFactory);
                    } else {
                        field.set(instance, beanFactory.get());
                    }
                } else if (fieldAttribute.isRequired()) {
                    //如果依赖Bean不存在则抛出异常
                    throw new UnsatisfiedBeanException(String.format("there is no match reference bean named %s !", fieldAttribute.getBeanName()));
                }
            } catch (IllegalAccessException | UnsatisfiedBeanException e) {
                throw new FieldInjectedException(String.format("failed to inject entity: %s by field!",
                        beanDefinition.getBeanName()), e);
            }
        }
    }

    public void doMethodInject() throws MethodInjectedException {
        for (MethodAttribute methodAttribute : beanDefinition.getMethodAttributes()) {
            Method method = methodAttribute.getMethod();
            method.setAccessible(true);
            try {
                List<Object> args = getArguments(methodAttribute.getArgs(), false);
                method.invoke(instance, args);
            } catch (IllegalAccessException | InvocationTargetException | UnsatisfiedBeanException e) {
                if (methodAttribute.isRequired()) {
                    throw new MethodInjectedException(String.format("failed to inject entity %s by method !",
                            beanDefinition.getBeanName()), e);
                }
            }
        }
    }

    private List<Object> getArguments(List<ParameterAttribute> parameterAttributes, boolean isConstructor) throws UnsatisfiedBeanException {
        List<Object> args = new ArrayList<>(parameterAttributes.size());
        //遍历方法的参数依赖信息
        for (ParameterAttribute parameterAttribute : parameterAttributes) {
            BeanFactory beanFactory = getReference(parameterAttribute.getBeanName(), parameterAttribute.getType());
            //依赖不存在，则抛出异常
            if (beanFactory == null) {
                throw new UnsatisfiedBeanException(String.format("there is no match reference bean named %s !",
                        parameterAttribute.getBeanName()));
            }
            //判断是否是Provider
            if (parameterAttribute.isProvider()) {
                //添加实例到Provider参数
                args.add(beanFactory);
                continue;
            }
            //添加代理实例作为参数
            args.add(beanFactory.get());
        }
        return args;
    }

    private BeanFactory getReference(String beanName, Class<?> type) throws UnsatisfiedBeanException {
        BeanFactory beanFactory = beanContainer.getBean(beanName);

        if (beanFactory != null && !beanFactory.getBeanDefinition().isType(type)) {
            throw new UnsatisfiedBeanException(String.format("there is no match reference bean named %s !", beanName));
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

    private boolean checkIfNeedInstantiate() {
        return !isSingleton;
    }

    private boolean checkIfNeedInit() {
        return isLazy() || !isSingleton;
    }

    public boolean isLazy() {
        return beanDefinition.isLazy();
    }

    public BeanDefinition getBeanDefinition() {
        return beanDefinition;
    }

    public Object getObject() {
        if (proxyInstance == null) {
            Class<?>[] classes = beanDefinition.getClazz().getInterfaces();
            if (classes.length != 0) {
                JdkInvocationHandler jdkInvocationHandler = new JdkInvocationHandler();
                proxyInstance = jdkInvocationHandler.newProxyInstance(this);
            } else {
                CglibMethodInterceptor cglibMethodInterceptor = new CglibMethodInterceptor();
                proxyInstance = cglibMethodInterceptor.newProxyInstance(this);
            }
        }
        return proxyInstance;
    }

    @Override
    public Object get() {
        return getObject();
    }

}
