package cn.bdqfork.core.proxy;

import cn.bdqfork.core.container.UnSharedInstance;
import cn.bdqfork.core.exception.BeansException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk动态代理
 *
 * @author bdq
 * @since 2019-02-13
 */
public class JdkInvocationHandler extends AbstractAopInvocationHandler implements InvocationHandler {
    /**
     * 创建代理实例
     *
     * @return Object 代理实例
     */
    @Override
    public Object newProxyInstance() throws BeansException {
        Class<?> targetClass = target.getClass();
        if (target.getClass() == UnSharedInstance.class) {
            UnSharedInstance unSharedInstance = (UnSharedInstance) target;
            targetClass = unSharedInstance.getClazz();
        }
        return Proxy.newProxyInstance(targetClass.getClassLoader(), interfaces, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invoke(method, args);
    }

}
