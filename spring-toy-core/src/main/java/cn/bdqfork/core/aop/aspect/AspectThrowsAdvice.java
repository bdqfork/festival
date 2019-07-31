package cn.bdqfork.core.aop.aspect;

import cn.bdqfork.core.aop.ThrowsAdvice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-29
 */
public class AspectThrowsAdvice extends AbstractAspectAdvice implements ThrowsAdvice {

    @Override
    public void afterThrowing(Method method, Object[] args, Object target, Exception ex) {
        Object[] adviceArgs = getAdviceArgs(ex);
        try {
            adviceMethod.invoke(adviceInstance, adviceArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
