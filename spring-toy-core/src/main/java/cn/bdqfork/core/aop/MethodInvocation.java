package cn.bdqfork.core.aop;

import cn.bdqfork.core.container.BeanFactory;
import cn.bdqfork.core.exception.InjectedException;
import cn.bdqfork.core.exception.InstantiateException;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-29
 */
public class MethodInvocation {
    private BeanFactory beanFactory;
    private Method method;
    private Object[] args;

    public MethodInvocation(BeanFactory beanFactory, Method method, Object[] args) {
        this.beanFactory = beanFactory;
        this.method = method;
        this.args = args;
    }

    public Object proceed() throws Throwable {
        return method.invoke(beanFactory.getInstance(), args);
    }

    public Object getTarget() throws InjectedException, InstantiateException {
        return beanFactory.getInstance();
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }
}
