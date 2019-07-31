package cn.bdqfork.core.aop.aspect;

import cn.bdqfork.core.aop.Advice;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-29
 */
public interface AspectAdvice extends Advice {
    void setJoinPoint(JoinPoint joinPoint);

    void setAdviceInstance(Object adviceInstance);

    Object getAdviceInstance();

    void setAdviceMethod(Method adviceMethod);
}
