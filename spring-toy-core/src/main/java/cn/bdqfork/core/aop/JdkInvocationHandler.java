package cn.bdqfork.core.aop;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author bdq
 * @date 2019-02-13
 */
public class JdkInvocationHandler implements InvocationHandler {
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
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        if (methodAroundAdvice != null) {
            MethodInvocation methodInvocation = new MethodInvocation(target, method, args);
            return methodAroundAdvice.invoke(methodInvocation);
        }
        if (methodBeforeAdvice != null) {
            methodBeforeAdvice.before(method, args, target);
        }
        try {
            result = method.invoke(target, args);
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
