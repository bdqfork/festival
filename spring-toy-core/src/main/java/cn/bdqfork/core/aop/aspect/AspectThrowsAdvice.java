package cn.bdqfork.core.aop.aspect;

import cn.bdqfork.core.aop.ThrowsAdvice;
import cn.bdqfork.core.container.BeanFactory;
import cn.bdqfork.core.exception.InjectedException;
import cn.bdqfork.core.exception.InstantiateException;
import cn.bdqfork.core.utils.BeanUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @since 2019-07-29
 */
public class AspectThrowsAdvice implements AspectAdvice, ThrowsAdvice {
    /**
     * 执行优先级
     */
    private int order;
    private Object adviceInstance;
    private Method adviceMethod;
    private JoinPoint joinPoint;

    public AspectThrowsAdvice(Object adviceInstance, Method adviceMethod) {
        this.adviceInstance = adviceInstance;
        this.adviceMethod = adviceMethod;
    }

    @Override
    public void afterThrowing(Method method, Object[] args, Object target, Exception ex) {
        Parameter[] parameters = adviceMethod.getParameters();
        Object[] adviceArgs = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (BeanUtils.isSubType(parameters[i].getType(), JoinPoint.class)) {
                adviceArgs[i] = joinPoint;
            } else {
                adviceArgs[i] = ex;
            }
        }
        try {
            adviceMethod.invoke(adviceInstance, adviceArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setJoinPoint(JoinPoint joinPoint) {
        this.joinPoint = joinPoint;
    }
}
