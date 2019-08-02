package cn.bdqfork.core.aop;

import java.lang.reflect.Method;

/**
 * 顾问接口，通知接口的增强，可以实现更复杂的通知
 *
 * @author bdq
 * @since 2019-07-29
 */
public interface Advisor extends Advice {
    void setPointcut(String pointcut);

    String getPointcut();

    void setAdvice(Advice advice);

    /**
     * 获取通知
     *
     * @return Advice
     */
    Advice getAdvice();

    /**
     * 代理方法是否匹配通知
     *
     * @param method     代理方法
     * @param adviceType 通知类型
     * @return boolean
     */
    boolean isMatch(Method method, Class<?> adviceType);
}
