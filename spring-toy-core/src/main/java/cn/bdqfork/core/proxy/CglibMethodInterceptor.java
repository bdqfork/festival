package cn.bdqfork.core.proxy;

import cn.bdqfork.core.container.BeanFactory;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Cglib代理
 *
 * @author bdq
 * @date 2019-02-14
 */
public class CglibMethodInterceptor implements MethodInterceptor {
    private BeanFactory beanFactory;

    /**
     * 创建代理实例
     *
     * @param beanFactory Bean工厂实例
     * @return Object 代理实例
     */
    public Object newProxyInstance(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(this);
        Class<?> clazz = beanFactory.getBeanDefinition().getClazz();
        enhancer.setSuperclass(clazz);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return proxy.invoke(beanFactory.getInstance(), args);
    }

}
