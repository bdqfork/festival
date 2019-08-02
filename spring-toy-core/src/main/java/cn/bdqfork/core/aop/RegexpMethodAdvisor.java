package cn.bdqfork.core.aop;

import java.lang.reflect.Method;

/**
 * 正则顾问，用于正则表达式匹配切点
 *
 * @author bdq
 * @since 2019-07-29
 */
public class RegexpMethodAdvisor extends AbstractAdvisor {

    @Override
    public boolean isMatch(Method method, Class<?> adviceType) {
        MethodSignature methodSignature = new MethodSignature(adviceType, method);
        String fullyMethodName = methodSignature.toLongString();
        return adviceType.isAssignableFrom(getAdvice().getClass()) && fullyMethodName.matches(getPointcut());
    }

}
