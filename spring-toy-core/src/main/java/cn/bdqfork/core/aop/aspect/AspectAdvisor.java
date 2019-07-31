package cn.bdqfork.core.aop.aspect;

import cn.bdqfork.core.aop.Advice;
import cn.bdqfork.core.aop.Advisor;
import cn.bdqfork.core.aop.MethodSignature;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-29
 */
public class AspectAdvisor implements Advisor {
    private String pointcut;
    private AspectAdvice aspectAdvice;

    public AspectAdvisor() {
    }

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
        Object adviceInstance = aspectAdvice.getAdviceInstance();
        MethodSignature methodSignature = new MethodSignature(adviceInstance.getClass(), method);
        String fullyMethodName = methodSignature.toLongString();
        return adviceType.isAssignableFrom(aspectAdvice.getClass()) && fullyMethodName.matches(pointcut);
    }

    public AspectAdvice getAspectAdvice() {
        return aspectAdvice;
    }

    public void setPointcut(String pointcut) {
        this.pointcut = pointcut;
    }

    public void setAspectAdvice(AspectAdvice aspectAdvice) {
        this.aspectAdvice = aspectAdvice;
    }
}
