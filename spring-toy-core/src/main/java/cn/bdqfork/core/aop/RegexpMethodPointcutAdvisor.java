package cn.bdqfork.core.aop;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-29
 */
public class RegexpMethodPointcutAdvisor implements PointcutAdvisor {
    private Advice advice;
    private String pointcut;

    public RegexpMethodPointcutAdvisor(Advice advice, String pointcut) {
        this.advice = advice;
        this.pointcut = pointcut;
    }

    public Advice getAdvice() {
        return advice;
    }

    public String getPointcut() {
        return pointcut;
    }

    @Override
    public boolean isMatch(Method method) {
        return method.getName().matches(pointcut);
    }
}
