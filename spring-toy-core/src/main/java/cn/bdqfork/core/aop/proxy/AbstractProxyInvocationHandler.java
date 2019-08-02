package cn.bdqfork.core.aop.proxy;

import cn.bdqfork.core.container.UnSharedInstance;
import cn.bdqfork.core.exception.BeansException;

import java.lang.reflect.Method;

/**
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

    @Override
    public Object invoke(Method method, Object[] args) throws Throwable {
        Object targetObject = getTargetObject();
        Object result = invokeObjectMethod(targetObject, method, args);
        if (result == null) {
            result = method.invoke(targetObject, args);
        }
        return result;
    }

}
