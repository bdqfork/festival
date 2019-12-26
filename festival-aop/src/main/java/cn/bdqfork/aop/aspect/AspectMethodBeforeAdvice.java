package cn.bdqfork.aop.aspect;

import cn.bdqfork.aop.advice.MethodBeforeAdvice;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class AspectMethodBeforeAdvice extends AbstractAspectAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        Object[] adviceArgs = getAdviceArgs(null, JoinPoint.class);
        //执行切面通知方法
        aspectAdviceMethod.invoke(aspectInstance, adviceArgs);
    }

}
