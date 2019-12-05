package cn.bdqfork.core.aop.aspect;

import cn.bdqfork.core.aop.advice.AbstractAdvisor;
import cn.bdqfork.core.aop.MethodSignature;
import cn.bdqfork.core.aop.advice.AspectAdvice;

import java.lang.reflect.Method;

/**
 * Aspect顾问，用于支持Aspect注解AOP
 *
 * @author bdq
 * @since 2019-07-29
 */
public class AspectAdvisor extends AbstractAdvisor {

    @Override
    public boolean isMatch(Method method, Class<?> adviceType) {
        AspectAdvice aspectAdvice = (AspectAdvice) getAdvice();

        Object adviceInstance = aspectAdvice.getAspectInstance();

        MethodSignature methodSignature = new MethodSignature(adviceInstance.getClass(), method);
        String fullyMethodName = methodSignature.toLongString();

        return adviceType.isAssignableFrom(aspectAdvice.getClass()) && fullyMethodName.matches(getPointcut());
    }

}
