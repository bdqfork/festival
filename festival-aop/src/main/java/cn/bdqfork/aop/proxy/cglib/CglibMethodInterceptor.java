package cn.bdqfork.aop.proxy.cglib;

import cn.bdqfork.aop.proxy.AbstractAopInvocationHandler;
import cn.bdqfork.core.exception.BeansException;
import net.sf.cglib.proxy.*;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class CglibMethodInterceptor extends AbstractAopInvocationHandler implements MethodInterceptor {

    /**
     * 创建代理实例
     *
     * @return Object 代理实例
     */
    @Override
    public Object newProxyInstance() throws BeansException {
        Enhancer enhancer = new Enhancer();
        enhancer.setCallbackType(MethodInterceptor.class);
        Class<?> targetClass = target.getClass();
        enhancer.setSuperclass(targetClass);
        Class<?> proxyClass = enhancer.createClass();

        Objenesis objenesis = new ObjenesisStd();
        ObjectInstantiator<?> instantiator = objenesis.getInstantiatorOf(proxyClass);
        Object proxyInstance = instantiator.newInstance();

        ((Factory) proxyInstance).setCallbacks(new Callback[]{this});

        return proxyInstance;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        //获取真实目标实例
        Object targetObject = getTargetObject();
        Object result = invokeObjectMethod(targetObject, method, args);
        if (result == null) {
            result = invokeWithAdvice(targetObject, method, args);
        }
        return result;
    }

}
