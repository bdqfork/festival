package cn.bdqfork.core.proxy;

import cn.bdqfork.core.aop.*;
import cn.bdqfork.core.container.BeanFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * jdk动态代理
 *
 * @author bdq
 * @date 2019-02-13
 */
public class JdkInvocationHandler implements InvocationHandler {
    /**
     * Bean工厂实例
     */
    private BeanFactory beanFactory;
    /**
     * 前置增强
     */
    private List<MethodBeforeAdvice> beforeAdvices;
    /**
     * 后置增强
     */
    private List<AfterReturningAdvice> afterReturningAdvices;
    /**
     * 环绕增强
     */
    private List<MethodInterceptor> aroundAdvices;

    public JdkInvocationHandler(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 创建代理实例
     *
     * @return Object 代理实例
     */
    public Object newProxyInstance() {
        Class<?> clazz = beanFactory.getBeanDefinition().getClazz();
        return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodInvocation invocation = new MethodInvocation(beanFactory, method, args);
        return invocation.proceed();
    }


}
