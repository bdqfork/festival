package cn.bdqfork.aop.aspect;

import cn.bdqfork.aop.advice.AfterReturningAdvice;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class AspectAfterReturningAdvice extends AbstractAspectAdvice implements AfterReturningAdvice {

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        Object[] adviceArgs = getAdviceArgs(returnValue, JoinPoint.class);
        //执行切面通知方法
        aspectAdviceMethod.invoke(aspectInstance, adviceArgs);
    }

}
