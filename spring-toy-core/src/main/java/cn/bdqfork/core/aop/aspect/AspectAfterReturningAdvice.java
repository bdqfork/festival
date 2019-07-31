package cn.bdqfork.core.aop.aspect;

import cn.bdqfork.core.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-29
 */
public class AspectAfterReturningAdvice extends AbstractAspectAdvice implements AfterReturningAdvice {

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        Object[] adviceArgs = getAdviceArgs(returnValue);
        adviceMethod.invoke(adviceInstance, adviceArgs);
    }

}
