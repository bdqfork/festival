package cn.bdqfork.core.aop.aspect;

import cn.bdqfork.core.aop.AfterReturningAdvice;
import cn.bdqfork.core.utils.BeanUtils;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @since 2019-07-29
 */
public class AspectAfterReturningAdvice implements AspectAdvice, AfterReturningAdvice {
    /**
     * 执行优先级
     */
    private int order;
    private Object adviceInstance;
    private Method adviceMethod;
    private JoinPoint joinPoint;

    public AspectAfterReturningAdvice(Object adviceInstance, Method adviceMethod) {
        this.adviceInstance = adviceInstance;
        this.adviceMethod = adviceMethod;
    }

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        Parameter[] parameters = adviceMethod.getParameters();
        Object[] adviceArgs = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (BeanUtils.isSubType(parameters[i].getType(), JoinPoint.class)) {
                adviceArgs[i] = joinPoint;
            } else {
                adviceArgs[i] = returnValue;
            }
        }
        adviceMethod.invoke(adviceInstance, adviceArgs);
    }

    @Override
    public void setJoinPoint(JoinPoint joinPoint) {
        this.joinPoint = joinPoint;
    }
}
