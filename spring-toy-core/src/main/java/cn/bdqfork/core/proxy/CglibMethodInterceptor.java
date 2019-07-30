package cn.bdqfork.core.proxy;

import cn.bdqfork.core.aop.MethodBeforeAdvice;
import cn.bdqfork.core.aop.MethodInvocation;
import cn.bdqfork.core.container.BeanFactory;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Cglib代理
 *
 * @author bdq
 * @since 2019-02-14
 */
public class CglibMethodInterceptor extends AdviceInvocationHandler implements MethodInterceptor {
    private Object target;
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
        Class<?> clazz;
        if (target == null) {
            clazz = beanFactory.getBeanDefinition().getClazz();
        } else {
            clazz = target.getClass();
        }
        enhancer.setSuperclass(clazz);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        //target == null表示非单例
        if (target == null) {
            target = beanFactory.getInstance();
        }
        return super.invoke(target, method, args);
    }

}
