package cn.bdqfork.core.proxy;

import cn.bdqfork.core.aop.MethodBeforeAdvice;
import cn.bdqfork.core.aop.MethodInterceptor;
import cn.bdqfork.core.aop.MethodInvocation;
import cn.bdqfork.core.container.BeanFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk动态代理
 *
 * @author bdq
 * @date 2019-02-13
 */
public class JdkInvocationHandler extends AdviceInvocationHandler implements InvocationHandler {
    private Object target;
    /**
     * Bean工厂实例
     */
    private BeanFactory beanFactory;

    public JdkInvocationHandler(Object target) {
        this.target = target;
    }

    public JdkInvocationHandler(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 创建代理实例
     *
     * @return Object 代理实例
     */
    public Object newProxyInstance() {
        Class<?> clazz;
        if (target == null) {
            clazz = beanFactory.getBeanDefinition().getClazz();
        } else {
            clazz = target.getClass();
        }
        return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //target == null表示非单例
        if (target == null) {
            target = beanFactory.getInstance();
        }
        return super.invoke(target, method, args);
    }

}
