package cn.bdqfork.core.aop.aspect;

import cn.bdqfork.core.aop.MethodInterceptor;
import cn.bdqfork.core.aop.MethodInvocation;
import cn.bdqfork.core.utils.BeanUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-29
 */
public class AspectAroundAdvice implements AspectAdvice, MethodInterceptor {
    /**
     * 执行优先级
     */
    private int order;
    private Object adviceInstance;
    private Method adviceMethod;
    private JoinPoint joinPoint;

    public AspectAroundAdvice(Object adviceInstance, Method adviceMethod) {
        this.adviceInstance = adviceInstance;
        this.adviceMethod = adviceMethod;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (invocation != null) {
            joinPoint = invocation;
        }
        Object[] adviceArgs = new Object[adviceMethod.getParameterCount()];
        for (int i = 0; i < adviceMethod.getParameterTypes().length; i++) {
            if (BeanUtils.isSubType(adviceMethod.getParameterTypes()[i], ProceedingJoinPoint.class)) {
                adviceArgs[i] = joinPoint;
            }
        }
        return adviceMethod.invoke(adviceInstance, adviceArgs);
    }

    @Override
    public void setJoinPoint(JoinPoint joinPoint) {
        this.joinPoint = joinPoint;
    }
}
