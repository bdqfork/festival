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
    private Object adviceInstance;
    private Method adviceMethod;
    private JoinPoint joinPoint;

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

    @Override
    public void setAdviceInstance(Object adviceInstance) {
        this.adviceInstance = adviceInstance;
    }

    @Override
    public Object getAdviceInstance() {
        return adviceInstance;
    }

    @Override
    public void setAdviceMethod(Method adviceMethod) {
        this.adviceMethod = adviceMethod;
    }
}
