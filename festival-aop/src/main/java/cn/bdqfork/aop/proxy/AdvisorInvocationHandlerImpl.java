package cn.bdqfork.aop.proxy;

import cn.bdqfork.aop.MethodInvocation;
import cn.bdqfork.aop.MethodSignature;
import cn.bdqfork.aop.advice.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class AdvisorInvocationHandlerImpl implements AdvisorInvocationHandler {
    /**
     * 切面
     */
    private List<Advisor> advisors;
    /**
     * 方法签名，前置通知映射
     */
    private Map<String, MethodBeforeAdvice[]> methodBeforeAdviceCache;
    /**
     * 方法签名，后置通知映射
     */
    private Map<String, AfterReturningAdvice[]> afterReturningAdviceCache;
    /**
     * 方法签名，环绕通知映射
     */
    private Map<String, AroundAdvice[]> aroundAdviceCache;
    /**
     * 方法签名，异常通知映射
     */
    private Map<String, ThrowsAdvice[]> throwsAdviceCache;

    public AdvisorInvocationHandlerImpl() {
        methodBeforeAdviceCache = new HashMap<>();
        afterReturningAdviceCache = new HashMap<>();
        aroundAdviceCache = new HashMap<>();
        throwsAdviceCache = new HashMap<>();
    }

    @Override
    public Object invokeWithAdvice(Object target, Method method, Object[] args) throws Throwable {
        MethodSignature methodSignature = new MethodSignature(target.getClass(), method);

        MethodBeforeAdvice[] beforeAdvices = getMethodBeforeAdvices(method, methodSignature);

        MethodInvocation invocation = new MethodInvocation(target, method, args, beforeAdvices);

        AroundAdvice[] aroundAdvices = getAroundAdvices(method, methodSignature);

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

    private MethodBeforeAdvice[] getMethodBeforeAdvices(Method method, MethodSignature methodSignature) {
        String fullyMethodName = methodSignature.toLongString();
        //判断是否有缓存key
        if (methodBeforeAdviceCache.containsKey(fullyMethodName)) {
            return methodBeforeAdviceCache.get(fullyMethodName);
        }
        MethodBeforeAdvice[] methodBeforeAdvice = advisors.stream()
                .filter(advisor -> advisor.isMatch(method, MethodBeforeAdvice.class))
                .map(advisor -> (MethodBeforeAdvice) advisor.getAdvice())
                .toArray(MethodBeforeAdvice[]::new);
        //methodBeforeAdvices可能为空数组
        methodBeforeAdviceCache.put(fullyMethodName, methodBeforeAdvice);
        return methodBeforeAdvice;
    }

    private AroundAdvice[] getAroundAdvices(Method method, MethodSignature methodSignature) {
        String fullyMethodName = methodSignature.toLongString();
        //判断是否有缓存key
        if (aroundAdviceCache.containsKey(fullyMethodName)) {
            return aroundAdviceCache.get(fullyMethodName);
        }
        AroundAdvice[] aroundAdvice = advisors.stream()
                .filter(advisor -> advisor.isMatch(method, AroundAdvice.class))
                .map(advisor -> (AroundAdvice) advisor.getAdvice())
                .toArray(AroundAdvice[]::new);
        aroundAdviceCache.put(fullyMethodName, aroundAdvice);
        return aroundAdvice;
    }

    private Object doAround(AroundAdvice aroundAdvice, MethodInvocation methodInvocation) throws Throwable {
        return aroundAdvice.invoke(methodInvocation);
    }

    private void doAfterReturning(MethodInvocation methodInvocation, Object returnValue) throws Throwable {
        String fullyMethodName = methodInvocation.toLongString();
        AfterReturningAdvice[] afterReturningAdvice = getAfterReturningAdvice(methodInvocation, fullyMethodName);
        for (AfterReturningAdvice advice : afterReturningAdvice) {
            if (advice instanceof AspectAdvice) {
                AspectAdvice aspectAdvice = (AspectAdvice) advice;
                aspectAdvice.setJoinPoint(methodInvocation);
            }
            advice.afterReturning(returnValue, methodInvocation.getMethod(),
                    methodInvocation.getArgs(), methodInvocation.getTarget());
        }
    }

    private AfterReturningAdvice[] getAfterReturningAdvice(MethodInvocation methodInvocation, String fullyMethodName) {
        if (afterReturningAdviceCache.containsKey(fullyMethodName)) {
            return afterReturningAdviceCache.get(fullyMethodName);
        }
        AfterReturningAdvice[] afterReturningAdvice = advisors.stream()
                .filter(advisor -> advisor.isMatch(methodInvocation.getMethod(), AfterReturningAdvice.class))
                .map(advisor -> (AfterReturningAdvice) advisor.getAdvice())
                .toArray(AfterReturningAdvice[]::new);
        afterReturningAdviceCache.put(fullyMethodName, afterReturningAdvice);
        return afterReturningAdvice;

    }

    private void doThrows(MethodInvocation methodInvocation, Exception e) throws Exception {
        ThrowsAdvice[] throwsAdvice;
        throwsAdvice = getThrowsAdvices(methodInvocation);
        if (throwsAdvice != null && throwsAdvice.length > 0) {
            for (ThrowsAdvice advice : throwsAdvice) {
                advice.afterThrowing(methodInvocation.getMethod(), methodInvocation.getArgs(),
                        methodInvocation.getTarget(), e);
            }
        } else {
            throw e;
        }
    }

    private ThrowsAdvice[] getThrowsAdvices(MethodInvocation methodInvocation) {
        String fullyMethodName = methodInvocation.toLongString();
        if (throwsAdviceCache.containsKey(fullyMethodName)) {
            return throwsAdviceCache.get(fullyMethodName);
        }
        ThrowsAdvice[] throwsAdvice = advisors.stream()
                .filter(advisor -> advisor.isMatch(methodInvocation.getMethod(), ThrowsAdvice.class))
                .map(advisor -> (ThrowsAdvice) advisor.getAdvice())
                .toArray(ThrowsAdvice[]::new);
        throwsAdviceCache.put(fullyMethodName, throwsAdvice);
        return throwsAdvice;
    }

    @Override
    public void setAdvisors(List<Advisor> advisors) {
        this.advisors = advisors;
    }

}
