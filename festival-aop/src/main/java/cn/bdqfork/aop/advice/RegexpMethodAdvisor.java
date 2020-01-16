package cn.bdqfork.aop.advice;

import cn.bdqfork.aop.MethodSignature;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class RegexpMethodAdvisor extends AbstractAdvisor {

    @Override
    public boolean isMatch(Method method, Class<?> adviceType) {
        MethodSignature methodSignature = new MethodSignature(adviceType, method);
        String fullyMethodName = methodSignature.toLongString();
        return adviceType.isAssignableFrom(getAdvice().getClass()) && fullyMethodName.matches(getPointcut());
    }

}
