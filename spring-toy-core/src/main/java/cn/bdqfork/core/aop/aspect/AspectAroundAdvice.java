package cn.bdqfork.core.aop.aspect;

import cn.bdqfork.core.aop.advice.MethodInterceptor;
import cn.bdqfork.core.aop.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author bdq
 * @since 2019-07-29
 */
public class AspectAroundAdvice extends AbstractAspectAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (invocation != null) {
            joinPoint = invocation;
        }
        Object[] adviceArgs = getAdviceArgs(null, ProceedingJoinPoint.class);
        //执行切面通知方法
        return aspectAdviceMethod.invoke(aspectInstance, adviceArgs);
    }

}
