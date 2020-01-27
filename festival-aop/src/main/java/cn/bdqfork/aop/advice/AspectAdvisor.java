package cn.bdqfork.aop.advice;

import cn.bdqfork.aop.MethodSignature;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class AspectAdvisor extends AbstractAdvisor {

    @Override
    public boolean isMatch(Method method, Class<?> adviceType) {
        AspectAdvice aspectAdvice = (AspectAdvice) getAdvice();

        MethodSignature methodSignature = new MethodSignature(method);
        String fullyMethodName = methodSignature.toLongString();

        return adviceType.isAssignableFrom(aspectAdvice.getClass()) && fullyMethodName.matches(getPointcut());
    }

}
