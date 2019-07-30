package cn.bdqfork.core.aop.aspect;

import cn.bdqfork.core.aop.MethodBeforeAdvice;
import cn.bdqfork.core.utils.BeanUtils;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-29
 */
public class AspectMethodBeforeAdvice implements AspectAdvice, MethodBeforeAdvice {
    private Object adviceInstance;
    private Method adviceMethod;
    private JoinPoint joinPoint;

    public AspectMethodBeforeAdvice(Object adviceInstance, Method adviceMethod) {
        this.adviceInstance = adviceInstance;
        this.adviceMethod = adviceMethod;
    }

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        Object[] adviceArgs = new Object[adviceMethod.getParameterCount()];
        for (int i = 0; i < adviceMethod.getParameterTypes().length; i++) {
            if (BeanUtils.isSubType(adviceMethod.getParameterTypes()[i], JoinPoint.class)) {
                adviceArgs[i] = joinPoint;
            }
        }
        adviceMethod.invoke(adviceInstance, adviceArgs);
    }

    @Override
    public void setJoinPoint(JoinPoint joinPoint) {
        this.joinPoint = joinPoint;
    }

}
