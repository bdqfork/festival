package cn.bdqfork.core.aop.advice;

import java.lang.reflect.Method;

/**
 * 顾问接口，通知接口的增强，可以实现更复杂的通知
 *
 * @author bdq
 * @since 2019-07-29
 */
public interface Advisor extends Advice {
    /**
     * 设置切点
     *
     * @param pointcut 切点表达式
     */
    void setPointcut(String pointcut);

    /**
     * 获取切点表达式
     *
     * @return String
     */
    String getPointcut();

    /**
     * 设置通知
     *
     * @param advice 通知
     */
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
