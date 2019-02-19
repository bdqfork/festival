package cn.bdqfork.core.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @date 2019-02-14
 */
public class CglibMethodInterceptor implements MethodInterceptor {
    private Object target;

    /**
     * 创建代理实例
     *
     * @param target
     * @return
     */
    public Object newProxyInstance(Object target) {
        this.target = target;
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(this);
        enhancer.setSuperclass(target.getClass());
        return enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return proxy.invoke(target, args);
    }
}
