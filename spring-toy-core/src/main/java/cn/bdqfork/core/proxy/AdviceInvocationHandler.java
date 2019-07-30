package cn.bdqfork.core.proxy;

import cn.bdqfork.core.aop.*;
import cn.bdqfork.core.aop.aspect.AspectAdvice;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author bdq
 * @since 2019-07-30
 */
public abstract class AdviceInvocationHandler {
    /**
     * 切面
     */
    private List<PointcutAdvisor> pointcutAdvisors;

    protected Object invoke(Object target, Method method, Object[] args) throws Throwable {
        MethodBeforeAdvice[] beforeAdvices = getMethodBeforeAdvices(method);
        MethodInvocation invocation = new MethodInvocation(target, method, args, beforeAdvices);
        MethodInterceptor[] aroundAdvices = getAroundAdvices(method);

        if (aroundAdvices.length > 0) {
            for (MethodInterceptor aroundAdvice : aroundAdvices) {
                try {
                    Object returnValue = doAround(aroundAdvice, invocation);
                    doAfterReturning(invocation, returnValue);
                    return returnValue;
                } catch (Exception e) {
                    doThrows(invocation, e);
                }
            }
        } else {
            try {
                Object returnValue = invocation.proceed();
                doAfterReturning(invocation, returnValue);
                return returnValue;
            } catch (Exception e) {
                doThrows(invocation, e);
            }
        }
        return null;
    }

    private MethodInterceptor[] getAroundAdvices(Method method) {
        return pointcutAdvisors.stream()
                .filter(pointcutAdvisor -> pointcutAdvisor.isMatch(method, MethodInterceptor.class))
                .map(pointcutAdvisor -> (MethodInterceptor) pointcutAdvisor.getAdvice())
                .toArray(MethodInterceptor[]::new);
    }

    private MethodBeforeAdvice[] getMethodBeforeAdvices(Method method) {
        return pointcutAdvisors.stream()
                .filter(pointcutAdvisor -> pointcutAdvisor.isMatch(method, MethodBeforeAdvice.class))
                .map(pointcutAdvisor -> (MethodBeforeAdvice) pointcutAdvisor.getAdvice())
                .toArray(MethodBeforeAdvice[]::new);
    }


    private Object doAround(MethodInterceptor methodInterceptor, MethodInvocation methodInvocation) throws Throwable {
        return methodInterceptor.invoke(methodInvocation);
    }

    private void doAfterReturning(MethodInvocation methodInvocation, Object returnValue) throws Throwable {
        for (PointcutAdvisor pointcutAdvisor : pointcutAdvisors) {
            if (pointcutAdvisor.isMatch(methodInvocation.getMethod(), AfterReturningAdvice.class)) {
                AfterReturningAdvice advice = (AfterReturningAdvice) pointcutAdvisor.getAdvice();
                if (advice instanceof AspectAdvice) {
                    ((AspectAdvice) advice).setJoinPoint(methodInvocation);
                }
                advice.afterReturning(returnValue, methodInvocation.getMethod(),
                        methodInvocation.getArgs(), methodInvocation.getTarget());
            }
        }
    }

    private void doThrows(MethodInvocation methodInvocation, Exception e) throws Exception {
        long count = pointcutAdvisors.stream()
                .filter(pointcutAdvisor -> pointcutAdvisor.isMatch(methodInvocation.getMethod(), ThrowsAdvice.class))
                .count();
        if (count > 0) {
            pointcutAdvisors.stream()
                    .filter(pointcutAdvisor -> pointcutAdvisor.isMatch(methodInvocation.getMethod(), ThrowsAdvice.class))
                    .map(pointcutAdvisor -> (ThrowsAdvice) pointcutAdvisor.getAdvice())
                    .forEach(advice -> advice.afterThrowing(methodInvocation.getMethod(), methodInvocation.getArgs(),
                            methodInvocation.getTarget(), e));
        } else {
            throw e;
        }
    }

    public void setPointcutAdvisors(List<PointcutAdvisor> pointcutAdvisors) {
        this.pointcutAdvisors = pointcutAdvisors;
    }

}
