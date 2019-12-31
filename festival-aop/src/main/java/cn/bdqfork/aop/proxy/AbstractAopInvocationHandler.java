package cn.bdqfork.aop.proxy;

import cn.bdqfork.aop.MethodInvocation;
import cn.bdqfork.aop.MethodSignature;
import cn.bdqfork.aop.advice.*;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019/12/23
 */
public abstract class AbstractAopInvocationHandler extends AbstractProxyInvocationHandler {
    /**
     * 方法签名，前置通知映射
     */
    private final Map<String, MethodBeforeAdvice[]> methodBeforeAdviceCache = new ConcurrentHashMap<>(16);
    /**
     * 方法签名，后置通知映射
     */
    private final Map<String, AfterReturningAdvice[]> afterReturningAdviceCache = new ConcurrentHashMap<>(16);
    /**
     * 方法签名，环绕通知映射
     */
    private final Map<String, AroundAdvice[]> aroundAdviceCache = new ConcurrentHashMap<>(16);
    /**
     * 方法签名，异常通知映射
     */
    private final Map<String, ThrowsAdvice[]> throwsAdviceCache = new ConcurrentHashMap<>(16);

    @Override
    public Object invokeWithAdvice(Object target, Method method, Object[] args) throws Throwable {
        MethodSignature methodSignature = new MethodSignature(target.getClass(), method);
        String fullyMethodName = methodSignature.toLongString();

        MethodBeforeAdvice[] beforeAdvices = getMethodBeforeAdvices(fullyMethodName);

        MethodInvocation invocation = new MethodInvocation(target, method, args, beforeAdvices);

        AroundAdvice[] aroundAdvices = getAroundAdvices(fullyMethodName);

        if (aroundAdvices.length > 0) {
            //执行环绕通知
            for (AroundAdvice aroundAdvice : aroundAdvices) {
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

    private MethodBeforeAdvice[] getMethodBeforeAdvices(String fullyMethodName) {
        return methodBeforeAdviceCache.computeIfAbsent(fullyMethodName, s -> new MethodBeforeAdvice[0]);
    }

    private AroundAdvice[] getAroundAdvices(String fullyMethodName) {
        return aroundAdviceCache.computeIfAbsent(fullyMethodName, s -> new AroundAdvice[0]);
    }

    private Object doAround(AroundAdvice aroundAdvice, MethodInvocation methodInvocation) throws Throwable {
        return aroundAdvice.invoke(methodInvocation);
    }

    private void doAfterReturning(MethodInvocation methodInvocation, Object returnValue) throws Throwable {
        String fullyMethodName = methodInvocation.toLongString();
        AfterReturningAdvice[] afterReturningAdvice = getAfterReturningAdvice(fullyMethodName);
        for (AfterReturningAdvice advice : afterReturningAdvice) {
            if (advice instanceof AspectAdvice) {
                AspectAdvice aspectAdvice = (AspectAdvice) advice;
                aspectAdvice.setJoinPoint(methodInvocation);
            }
            advice.afterReturning(returnValue, methodInvocation.getMethod(),
                    methodInvocation.getArgs(), methodInvocation.getTarget());
        }
    }

    private AfterReturningAdvice[] getAfterReturningAdvice(String fullyMethodName) {
        return afterReturningAdviceCache.computeIfAbsent(fullyMethodName, s -> new AfterReturningAdvice[0]);

    }

    private void doThrows(MethodInvocation methodInvocation, Exception e) throws Exception {
        ThrowsAdvice[] throwsAdvice = getThrowsAdvices(methodInvocation.toLongString());
        if (throwsAdvice != null && throwsAdvice.length > 0) {
            for (ThrowsAdvice advice : throwsAdvice) {
                advice.afterThrowing(methodInvocation.getMethod(), methodInvocation.getArgs(),
                        methodInvocation.getTarget(), e);
            }
        } else {
            throw e;
        }
    }

    private ThrowsAdvice[] getThrowsAdvices(String fullyMethodName) {
        return throwsAdviceCache.computeIfAbsent(fullyMethodName, s -> new ThrowsAdvice[0]);
    }

    @Override
    public void addAdvice(String fullyMethodName, MethodBeforeAdvice[] advice) {
        if (advice == null || !(advice.length > 0)) {
            return;
        }
        methodBeforeAdviceCache.put(fullyMethodName, advice);
    }

    @Override
    public void addAdvice(String fullyMethodName, AroundAdvice[] advice) {
        if (advice == null || !(advice.length > 0)) {
            return;
        }
        aroundAdviceCache.put(fullyMethodName, advice);
    }

    @Override
    public void addAdvice(String fullyMethodName, AfterReturningAdvice[] advice) {
        if (advice == null || !(advice.length > 0)) {
            return;
        }
        afterReturningAdviceCache.put(fullyMethodName, advice);

    }

    @Override
    public void addAdvice(String fullyMethodName, ThrowsAdvice[] advice) {
        if (advice == null || !(advice.length > 0)) {
            return;
        }
        throwsAdviceCache.put(fullyMethodName, advice);
    }

}
