package cn.bdqfork.core.aop.aspect;

import cn.bdqfork.core.aop.Advice;
import cn.bdqfork.core.aop.PointcutAdvisor;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-29
 */
public class AspectAdvisor implements PointcutAdvisor {
    private String pointcut;
    private AspectAdvice aspectAdvice;

    public AspectAdvisor(String pointcut, AspectAdvice aspectAdvice) {
        this.pointcut = pointcut;
        this.aspectAdvice = aspectAdvice;
    }


    @Override
    public Advice getAdvice() {
        return aspectAdvice;
    }

    @Override
    public boolean isMatch(Method method, Class<?> adviceType) {
        return adviceType.isAssignableFrom(aspectAdvice.getClass()) && method.getName().matches(pointcut);
    }

}
