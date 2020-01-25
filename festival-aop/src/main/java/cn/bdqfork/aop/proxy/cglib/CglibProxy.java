package cn.bdqfork.aop.proxy.cglib;

import cn.bdqfork.aop.proxy.AopProxy;
import cn.bdqfork.aop.proxy.AopProxySupport;
import cn.bdqfork.aop.proxy.FestivalProxy;
import cn.bdqfork.aop.proxy.TargetClassAware;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * @author bdq
 * @since 2020/1/14
 */
public class CglibProxy implements AopProxy {
    private AopProxySupport support;

    public CglibProxy(AopProxySupport support) {
        this.support = support;
    }

    @Override
    public Object getProxy() {
        return getProxy(null);
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(classLoader);
        enhancer.setCallbackType(MethodInterceptor.class);

        Class<?> targetClass = support.getBeanClass();
        enhancer.setSuperclass(targetClass);
        enhancer.setInterfaces(new Class[]{FestivalProxy.class, TargetClassAware.class});
        Class<?> proxyClass = enhancer.createClass();

        Objenesis objenesis = new ObjenesisStd();
        ObjectInstantiator<?> instantiator = objenesis.getInstantiatorOf(proxyClass);
        Object proxyInstance = instantiator.newInstance();

        ((Factory) proxyInstance).setCallbacks(new Callback[]{new CglibMethodInterceptor(support)});

        return proxyInstance;
    }
}
