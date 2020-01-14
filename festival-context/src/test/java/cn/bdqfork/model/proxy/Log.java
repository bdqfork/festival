package cn.bdqfork.model.proxy;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2019/12/31
 */
@Singleton
@Named
@Aspect
public class Log {
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        System.out.println("执行前置通知方法");
    }

    @AfterReturning(value = "pointcut()", returning = "result")
    public void after(JoinPoint joinPoint, Object result) {
        System.out.println("执行后置通知方法，return : " + result);
    }

    @Around("execution(cn.bdqfork.model.proxy.*)")
    public Object myAround(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("执行环绕通知方法，目标方法执行之前");
        Object result = pjp.proceed();
        System.out.println("执行环绕通知方法，目标方法执行之后");
        if (result != null) {                                //可以修改目标方法的返回结果
            result = ((String) result).toUpperCase();
        }
        return result;
    }

    @AfterThrowing(value = "pointcut()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Exception ex) {
        System.out.println("执行异常抛出通知方法，Exception : " + ex);
    }

    @Pointcut("execution(cn.bdqfork.model.proxy.*)")
    public void pointcut() {
    }
}
