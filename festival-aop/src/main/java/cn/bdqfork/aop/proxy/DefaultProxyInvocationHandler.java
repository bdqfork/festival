package cn.bdqfork.aop.proxy;

import cn.bdqfork.aop.MethodInvocation;
import cn.bdqfork.aop.advice.*;
import cn.bdqfork.core.util.ReflectUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class DefaultProxyInvocationHandler implements ProxyInvocationHandler {
    private Map<String, MethodBeforeAdvice[]> beforeAdviceCache = new ConcurrentHashMap<>();
    private Map<String, AroundAdvice[]> aroundAdviceCache = new ConcurrentHashMap<>();
    private Map<String, AfterReturningAdvice[]> afterReturningAdviceCache = new ConcurrentHashMap<>();
    private Map<String, ThrowsAdvice[]> throwsAdviceCache = new ConcurrentHashMap<>();
    protected AopProxySupport support;

    public DefaultProxyInvocationHandler(AopProxySupport support) {
        this.support = support;
    }

    @Override
    public Object invoke(Method method, Object[] args) throws Throwable {
        if ("getTargetClass".equals(method.getName())) {
            return support.getBeanClass();
        }

        Object target = support.getBean();
        Object result = invokeObjectMethod(target, method, args);
        if (result == null) {
            try {
                result = invokeWithAdvice(target, method, args);
            } catch (Exception e) {
                throw e.getCause();
            }

        }
        return result;
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

    protected Object invokeWithAdvice(Object target, Method method, Object[] args) throws Throwable {
        MethodBeforeAdvice[] beforeAdvices = getMethodBeforeAdvices(method);

        MethodInvocation invocation = new MethodInvocation(target, method, args, beforeAdvices);

        AroundAdvice[] aroundAdvices = getAroundAdvices(method);

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

    private MethodBeforeAdvice[] getMethodBeforeAdvices(Method method) {
        String methodSignature = ReflectUtils.getSignature(method);
        return beforeAdviceCache.computeIfAbsent(methodSignature,
                v -> support.getBeforeAdvice(method).toArray(new MethodBeforeAdvice[0]));
    }

    private AroundAdvice[] getAroundAdvices(Method method) {
        String methodSignature = ReflectUtils.getSignature(method);
        return aroundAdviceCache.computeIfAbsent(methodSignature,
                v -> support.getAroundAdvice(method).toArray(new AroundAdvice[0]));
    }

    private Object doAround(AroundAdvice aroundAdvice, MethodInvocation methodInvocation) throws Throwable {
        return aroundAdvice.invoke(methodInvocation);
    }

    private void doAfterReturning(MethodInvocation methodInvocation, Object returnValue) throws Throwable {
        AfterReturningAdvice[] afterReturningAdvice = getAfterReturningAdvice(methodInvocation.getMethod());
        for (AfterReturningAdvice advice : afterReturningAdvice) {
            if (advice instanceof AspectAdvice) {
                AspectAdvice aspectAdvice = (AspectAdvice) advice;
                aspectAdvice.setJoinPoint(methodInvocation);
            }
            advice.afterReturning(returnValue, methodInvocation.getMethod(),
                    methodInvocation.getArgs(), methodInvocation.getTarget());
        }
    }

    private AfterReturningAdvice[] getAfterReturningAdvice(Method method) {
        String methodSignature = ReflectUtils.getSignature(method);
        return afterReturningAdviceCache.computeIfAbsent(methodSignature,
                v -> support.getAfterAdvice(method).toArray(new AfterReturningAdvice[0]));

    }

    private void doThrows(MethodInvocation methodInvocation, Exception e) throws Exception {
        ThrowsAdvice[] throwsAdvice = getThrowsAdvices(methodInvocation.getMethod());
        if (throwsAdvice.length > 0) {
            for (ThrowsAdvice advice : throwsAdvice) {
                advice.afterThrowing(methodInvocation.getMethod(), methodInvocation.getArgs(),
                        methodInvocation.getTarget(), e);
            }
        } else {
            throw e;
        }
    }

    private ThrowsAdvice[] getThrowsAdvices(Method method) {
        String methodSignature = ReflectUtils.getSignature(method);
        return throwsAdviceCache.computeIfAbsent(methodSignature,
                v -> support.getThrowsAdvice(method).toArray(new ThrowsAdvice[0]));
    }

}
