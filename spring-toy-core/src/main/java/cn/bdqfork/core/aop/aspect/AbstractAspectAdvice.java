package cn.bdqfork.core.aop.aspect;

import cn.bdqfork.core.utils.BeanUtils;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @since 2019-07-31
 */
public abstract class AbstractAspectAdvice implements AspectAdvice {
    protected Object adviceInstance;
    protected Method adviceMethod;
    protected JoinPoint joinPoint;

    protected Object[] getAdviceArgs(Object value) {
        Parameter[] parameters = adviceMethod.getParameters();
        Object[] adviceArgs = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (BeanUtils.isSubType(parameters[i].getType(), JoinPoint.class)) {
                adviceArgs[i] = joinPoint;
            } else {
                adviceArgs[i] = value;
            }
        }
        return adviceArgs;
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
