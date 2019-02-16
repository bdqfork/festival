package cn.bdqfork.example.aop;

import cn.bdqfork.core.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @date 2019-02-16
 */
public class UserBeforeAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("before");
    }
}
