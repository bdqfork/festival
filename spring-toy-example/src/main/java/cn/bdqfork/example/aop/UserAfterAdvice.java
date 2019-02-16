package cn.bdqfork.example.aop;

import cn.bdqfork.core.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @date 2019-02-16
 */
public class UserAfterAdvice implements AfterReturningAdvice {
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println("after");
    }
}
