package cn.bdqfork.core.aop;

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

    private MethodBeforeAdvice methodBeforeAdvice;
    private MethodAroundAdvice methodAroundAdvice;
    private AfterReturningAdvice afterReturningAdvice;
    private ThrowsAdvice throwsAdvice;

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
        Object result = null;
        if (methodAroundAdvice != null) {
            MethodInvocation methodInvocation = new MethodInvocation(target, method, args,proxy);
            return methodAroundAdvice.invoke(methodInvocation);
        }
        if (methodBeforeAdvice != null) {
            methodBeforeAdvice.before(method, args, target);
        }
        try {
            result = proxy.invoke(target, args);
        } catch (Exception e) {
            if (throwsAdvice != null) {
                throwsAdvice.afterThrowing(method, args, target, e);
            } else {
                throw e;
            }
        }
        if (afterReturningAdvice != null) {
            afterReturningAdvice.afterReturning(result, method, args, target);
        }
        return result;
    }

    public void setMethodBeforeAdvice(MethodBeforeAdvice methodBeforeAdvice) {
        this.methodBeforeAdvice = methodBeforeAdvice;
    }

    public void setMethodAroundAdvice(MethodAroundAdvice methodAroundAdvice) {
        this.methodAroundAdvice = methodAroundAdvice;
    }

    public void setAfterReturningAdvice(AfterReturningAdvice afterReturningAdvice) {
        this.afterReturningAdvice = afterReturningAdvice;
    }

    public void setThrowsAdvice(ThrowsAdvice throwsAdvice) {
        this.throwsAdvice = throwsAdvice;
    }
}
