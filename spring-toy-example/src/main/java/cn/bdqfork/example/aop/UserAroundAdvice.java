package cn.bdqfork.example.aop;

import cn.bdqfork.core.aop.MethodAroundAdvice;
import cn.bdqfork.core.aop.MethodInvocation;

/**
 * @author bdq
 * @date 2019-02-16
 */
public class UserAroundAdvice implements MethodAroundAdvice {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("around");
        return invocation.proceed();
    }
}
