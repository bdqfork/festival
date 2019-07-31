package test.cn.bdqfork.ioc.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;

/**
 * @author bdq
 * @since 2019-07-30
 */
public class Aspect {

    @Before(".*")
    public void before(JoinPoint joinPoint) throws Throwable {
        System.out.println(joinPoint.getSignature());
        System.out.println("before");
    }

    @AfterReturning(value = ".*", returning = "returnValue")
    public void after(JoinPoint joinPoint, Object returnValue) throws Throwable {
        System.out.println(joinPoint.getSignature());
        System.out.println("after value : " + returnValue);
    }

    @Around(value = ".*")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result;
        System.out.println("around start");
        result = proceedingJoinPoint.proceed();
        System.out.println("around end");
        return result;
    }

    @AfterThrowing(value = ".*")
    public void afterThrows(JoinPoint joinPoint, Exception ex) throws Throwable {
        System.out.println("afterThrows : " + ex.toString());
    }
}
