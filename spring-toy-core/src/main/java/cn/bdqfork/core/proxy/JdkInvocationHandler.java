package cn.bdqfork.core.proxy;

import cn.bdqfork.core.container.ObjectFactory;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.utils.BeanUtils;

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
    private Class[] interfaces;

    /**
     * 创建代理实例
     *
     * @return Object 代理实例
     */
    public Object newProxyInstance() throws BeansException {
        Class targetClass = target.getClass();
        if (BeanUtils.isSubType(target.getClass(), ObjectFactory.class)) {
            ObjectFactory factory = (ObjectFactory) target;
            targetClass = factory.getObject().getClass();
        }
        return Proxy.newProxyInstance(targetClass.getClassLoader(), interfaces, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object targetObject = target;
        if (BeanUtils.isSubType(target.getClass(), ObjectFactory.class)) {
            ObjectFactory factory = (ObjectFactory) target;
            targetObject = factory.getObject();
        }
        return super.invoke(targetObject, method, args);
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setInterfaces(Class<?>... interfaces) {
        this.interfaces = interfaces;
    }
}
