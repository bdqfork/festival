package cn.bdqfork.core.proxy;

import cn.bdqfork.core.container.ObjectFactory;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.utils.BeanUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Cglib代理
 *
 * @author bdq
 * @since 2019-02-14
 */
public class CglibMethodInterceptor extends AdviceInvocationHandler implements MethodInterceptor {
    private Object target;

    /**
     * 创建代理实例
     *
     * @return Object 代理实例
     */
    public Object newProxyInstance() throws BeansException {
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(this);
        Class targetClass = target.getClass();
        if (BeanUtils.isSubType(target.getClass(), ObjectFactory.class)) {
            ObjectFactory factory = (ObjectFactory) target;
            targetClass = factory.getObject().getClass();
        }
        enhancer.setSuperclass(targetClass);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
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

}
