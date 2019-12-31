package cn.bdqfork.aop.constant;

import cn.bdqfork.aop.advice.AfterAdvice;
import cn.bdqfork.aop.advice.BeforeAdvice;
import cn.bdqfork.aop.advice.AroundAdvice;
import cn.bdqfork.aop.advice.ThrowsAdvice;

/**
 * @author bdq
 * @since 2019/12/27
 */
public enum AdviceType {
    BEFORE(BeforeAdvice.class), AROUND(AroundAdvice.class), AFTER(AfterAdvice.class), THROWS(ThrowsAdvice.class);
    private Class<?> value;

    private AdviceType(Class<?> value) {
        this.value = value;
    }
}
