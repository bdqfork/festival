package cn.bdqfork.core.aop.advice;

import cn.bdqfork.core.aop.advice.Advice;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-29
 */
public interface AspectAdvice extends Advice {
    /**
     * 注入切点实例
     *
     * @param joinPoint 切点
     */
    void setJoinPoint(JoinPoint joinPoint);

    /**
     * 注入切面类实例
     *
     * @param aspectInstance 切面实例
     */
    void setAspectInstance(Object aspectInstance);

    /**
     * 获取切面类实例
     *
     * @return Object 切面实例
     */
    Object getAspectInstance();

    /**
     * 注入切面通知方法
     *
     * @param aspectAdviceMethod
     */
    void setAspectAdviceMethod(Method aspectAdviceMethod);
}
