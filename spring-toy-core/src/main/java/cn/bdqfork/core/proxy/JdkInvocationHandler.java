package cn.bdqfork.core.proxy;

import cn.bdqfork.core.container.BeanFactory;
import cn.bdqfork.core.exception.InjectedException;
import cn.bdqfork.core.exception.InstantiateException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk动态代理
 *
 * @author bdq
 * @date 2019-02-13
 */
public class JdkInvocationHandler implements InvocationHandler {

    private BeanFactory beanFactory;

    /**
     * 创建代理实例
     *
     * @param beanFactory Bean工厂实例
     * @return Object 代理实例
     */
    public Object newProxyInstance(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        Class<?> clazz = beanFactory.getBeanDefinition().getClazz();
        return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(beanFactory.getInstance(), args);
    }

}
