package cn.bdqfork.aop.proxy;

import cn.bdqfork.core.exception.BeansException;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/23
 */
public abstract class AbstractProxyInvocationHandler implements ProxyInvocationHandler {
    /**
     * 目标对象
     */
    protected Object target;
    /**
     * 代理接口
     */
    protected Class<?>[] interfaces;

    @Override
    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public void setInterfaces(Class<?>... interfaces) {
        this.interfaces = interfaces;
    }

    protected Class<?> getTargetClass() {
        return target.getClass();
    }

    protected Object getTargetObject() throws BeansException {
        return target;
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
