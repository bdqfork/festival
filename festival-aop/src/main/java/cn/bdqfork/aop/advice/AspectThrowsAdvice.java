package cn.bdqfork.aop.advice;

import org.aspectj.lang.JoinPoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class AspectThrowsAdvice extends AbstractAspectAdvice implements ThrowsAdvice {

    @Override
    public void afterThrowing(Method method, Object[] args, Object target, Exception ex) {
        Object[] adviceArgs = getAdviceArgs(ex, JoinPoint.class);
        try {
            //执行切面通知方法
            aspectAdviceMethod.invoke(aspectInstance, adviceArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            //如果切面通知方法有异常，则抛出
            throw new RuntimeException(e);
        }
    }

}
