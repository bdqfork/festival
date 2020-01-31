package cn.bdqfork.model.proxy;

import cn.bdqfork.core.annotation.Order;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named
@Aspect
@Order(2)
public class OrderLog2 {

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        System.out.println("执行前置通知方法Order2");
    }

    @Pointcut("execution(cn.bdqfork.model.proxy.*)")
    public void pointcut() {
    }
}
