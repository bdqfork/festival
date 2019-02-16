package cn.bdqfork.core.aop;

import net.sf.cglib.proxy.MethodInterceptor;

/**
 * @author bdq
 * @date 2019-02-16
 */
public class ProxyFactory {
    private Object target;

    private MethodBeforeAdvice methodBeforeAdvice;
    private MethodAroundAdvice methodAroundAdvice;
    private AfterReturningAdvice afterReturningAdvice;
    private ThrowsAdvice throwsAdvice;

    public void setTarget(Object target) {
        this.target = target;
    }

    public void addAdvice(Advice advice) {
        if (advice instanceof MethodBeforeAdvice) {
            methodBeforeAdvice = (MethodBeforeAdvice) advice;
        } else if (advice instanceof MethodInterceptor) {
            methodAroundAdvice = (MethodAroundAdvice) advice;
        } else if (advice instanceof AfterReturningAdvice) {
            afterReturningAdvice = (AfterReturningAdvice) advice;
        } else if (advice instanceof ThrowsAdvice) {
            throwsAdvice = (ThrowsAdvice) advice;
        }
    }

    public Object getProxy() {
        if (target.getClass().getInterfaces().length > 0) {
            JdkInvocationHandler jdkInvocationHandler = new JdkInvocationHandler();
            jdkInvocationHandler.setMethodBeforeAdvice(methodBeforeAdvice);
            jdkInvocationHandler.setMethodAroundAdvice(methodAroundAdvice);
            jdkInvocationHandler.setAfterReturningAdvice(afterReturningAdvice);
            jdkInvocationHandler.setThrowsAdvice(throwsAdvice);
            return jdkInvocationHandler.newProxyInstance(target);
        } else {
            CglibMethodInterceptor cglibMethodInterceptor = new CglibMethodInterceptor();
            cglibMethodInterceptor.setMethodBeforeAdvice(methodBeforeAdvice);
            cglibMethodInterceptor.setMethodAroundAdvice(methodAroundAdvice);
            cglibMethodInterceptor.setAfterReturningAdvice(afterReturningAdvice);
            cglibMethodInterceptor.setThrowsAdvice(throwsAdvice);
            return cglibMethodInterceptor.newProxyInstance(target);
        }
    }

}
