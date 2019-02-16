package cn.bdqfork.example.aop;

import cn.bdqfork.core.aop.ThrowsAdvice;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @date 2019-02-16
 */
public class UserThrowAdvice implements ThrowsAdvice {
    @Override
    public void afterThrowing(Method method, Object[] args, Object target, Exception e) {
        System.out.println("throw");
    }
}
