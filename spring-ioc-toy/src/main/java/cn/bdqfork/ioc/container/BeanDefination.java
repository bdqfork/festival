package cn.bdqfork.ioc.container;

import cn.bdqfork.ioc.exception.SpringToyException;
import cn.bdqfork.ioc.proxy.JdkInvocationHandler;

/**
 * bean的定义，用来描述bean的信息
 *
 * @author bdq
 * @date 2019-02-12
 */
public class BeanDefination {
    private Class<?> clazz;
    private String name;
    private Object instance;
    private boolean isSingleton;
    private InjectorProvider injectorProvider;

    public BeanDefination(Class<?> clazz, boolean isSingleton, String name) {
        this.clazz = clazz;
        this.isSingleton = isSingleton;
        this.name = name;
    }

    /**
     * 判断当前bean是否是clazz的类型，如果是，返回true，否则返回false
     *
     * @param clazz
     * @return boolean
     */
    public boolean isType(Class<?> clazz) {
        if (this.clazz == clazz) {
            return true;
        }
        return clazz.isAssignableFrom(this.clazz);
    }

    /**
     * 获取对象实例，如果bean是单例的，则每次都返回同一个实例，如果不是，则每次都创建一个新的实例。
     *
     * @return Object
     */
    public Object getInstance() throws SpringToyException {
        if (isSingleton) {
            return getSingleInstance();
        }
        return newBean();
    }

    private Object getSingleInstance() throws SpringToyException {
        if (instance == null) {
            synchronized (Object.class) {
                if (instance == null) {
                    instance = newBean();
                }
            }
        }
        return instance;
    }

    private Object newBean() throws SpringToyException {
        Object instance = injectorProvider.doInject(this);
        Class<?>[] classes = clazz.getInterfaces();
        if (classes.length != 0) {
            JdkInvocationHandler jdkInvocationHandler = new JdkInvocationHandler();
            return jdkInvocationHandler.newProxyInstance(instance);
        }
        return instance;
    }

    public String getName() {
        return name;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setInjectorProvider(InjectorProvider injectorProvider) {
        this.injectorProvider = injectorProvider;
    }

    public InjectorProvider getInjectorProvider() {
        return injectorProvider;
    }

}
