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
    /**
     * Bean工厂实例
     */
    private BeanFactory beanFactory;

    public CglibMethodInterceptor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 创建代理实例
     *
     * @return Object 代理实例
     */
    public Object newProxyInstance() {
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
