package cn.bdqfork.aop.aspect;


import cn.bdqfork.aop.MethodInvocation;
import cn.bdqfork.aop.advice.MethodInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author bdq
 * @since 2019/12/23
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
