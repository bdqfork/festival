package test.cn.bdqfork.ioc.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * @author bdq
 * @since 2019-07-28
 */
@Aspect
public class Log {
    @Before("execution(* *..SomeService.doSecond(..))")
    public void before(JoinPoint joinPoint) {
    }

    @Around("execution(* *..SomeService.doSecond(..))")
    public Object myAround(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("执行环绕通知方法，目标方法执行之前");
        Object result = pjp.proceed();
        System.out.println("执行环绕通知方法，目标方法执行之后");
        if (result != null) {                                //可以修改目标方法的返回结果
            result = ((String) result).toUpperCase();
        }
        return result;
    }

}
