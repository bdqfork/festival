package cn.bdqfork.core.aop.proxy;

import cn.bdqfork.core.container.UnSharedInstance;
import cn.bdqfork.core.exception.BeansException;

import java.lang.reflect.Method;

/**
 * ProxyInvocationHandler抽象实现，实现了基本的方法
 *
 * @author bdq
 * @since 2019-08-02
 */
public abstract class AbstractProxyInvocationHandler implements ProxyInvocationHandler {
    /**
     * 目标对象
     */
    protected Object target;
    /**
     * 代理接口
     */
    protected Class[] interfaces;

    @Override
    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public void setInterfaces(Class<?>... interfaces) {
        this.interfaces = interfaces;
    }

    protected Class<?> getTargetClass() {
        Class<?> targetClass = target.getClass();
        if (target.getClass() == UnSharedInstance.class) {
            UnSharedInstance unSharedInstance = (UnSharedInstance) target;
            targetClass = unSharedInstance.getClazz();
        }
        return targetClass;
    }

    protected Object getTargetObject() throws BeansException {
        Object targetObject = target;
        if (target.getClass() == UnSharedInstance.class) {
            UnSharedInstance unSharedInstance = (UnSharedInstance) target;
            targetObject = unSharedInstance.getObjectFactory().getObject();
        }
        return targetObject;
    }

    protected Object invokeObjectMethod(Object targetObject, Method method, Object[] args) {
        if ("toString".equals(method.getName())) {
            return targetObject.toString();
        }
        if ("equals".equals(method.getName())) {
            return targetObject.equals(args[0]);
        }
        if ("hashCode".equals(method.getName())) {
            return targetObject.hashCode();
        }
        return null;
    }

}
