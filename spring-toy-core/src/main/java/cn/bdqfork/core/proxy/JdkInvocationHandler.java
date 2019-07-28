package cn.bdqfork.core.proxy;

import cn.bdqfork.core.container.BeanFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author bdq
 * @date 2019-02-13
 */
public class JdkInvocationHandler implements InvocationHandler {

    private BeanFactory beanFactory;

    /**
     * 创建代理实例
     *
     * @param beanFactory
     * @return
     */
    public Object newProxyInstance(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        Object target = beanFactory.getInstance();
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(beanFactory.getInstance(), args);
    }

}
