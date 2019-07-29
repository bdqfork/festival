package cn.bdqfork.core.aop;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-29
 */
public interface PointcutAdvisor {
    boolean isMatch(Method method);
}
