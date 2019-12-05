package cn.bdqfork.core.aop.advice;

import cn.bdqfork.core.aop.advice.Advice;
import cn.bdqfork.core.aop.advice.Advisor;

/**
 * 抽象Advisor
 *
 * @author bdq
 * @since 2019-08-02
 */
public abstract class AbstractAdvisor implements Advisor {
    /**
     * 切点表达式，正则表达式
     */
    private String pointcut;
    /**
     * 通知
     */
    private Advice advice;

    @Override
    public void setPointcut(String pointcut) {
        this.pointcut = pointcut;
    }

    @Override
    public String getPointcut() {
        return pointcut;
    }

    @Override
    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

}
