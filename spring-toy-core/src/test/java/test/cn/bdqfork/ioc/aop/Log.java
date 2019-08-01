package test.cn.bdqfork.ioc.aop;

import cn.bdqfork.core.annotation.Component;
import cn.bdqfork.core.annotation.Scope;
import cn.bdqfork.core.annotation.ScopeType;
import org.apache.tools.ant.taskdefs.Echo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * @author bdq
 * @since 2019-07-28
 */
@Scope(ScopeType.PROTOTYPE)
@Component
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

    @Around("execution(test.cn.bdqfork.ioc.aop.*)")
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

    @Pointcut("execution(test.cn.bdqfork.ioc.aop.*)")
    public void pointcut() {

    }

}
