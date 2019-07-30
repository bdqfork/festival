package cn.bdqfork.core.aop;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-29
 */
public class RegexpMethodPointcutAdvisor implements PointcutAdvisor {
    private String pointcut;
    private Advice advice;

    public RegexpMethodPointcutAdvisor(String pointcut, Advice advice) {
        this.pointcut = pointcut;
        this.advice = advice;
    }

    public Advice getAdvice() {
        return advice;
    }

    @Override
    public boolean isMatch(Method method, Class<?> adviceType) {
        return adviceType.isAssignableFrom(advice.getClass()) && method.getName().matches(pointcut);
    }

}
