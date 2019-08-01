package cn.bdqfork.core.aop;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-29
 */
public class RegexpMethodAdvisor implements Advisor {
    private String pointcut;
    private Advice advice;

    public RegexpMethodAdvisor(String pointcut, Advice advice) {
        this.pointcut = pointcut;
        this.advice = advice;
    }

    public Advice getAdvice() {
        return advice;
    }

    @Override
    public boolean isMatch(Method method, Class<?> adviceType) {
        MethodSignature methodSignature = new MethodSignature(adviceType, method);
        return adviceType.isAssignableFrom(advice.getClass()) && methodSignature.toLongString().matches(pointcut);
    }

}
