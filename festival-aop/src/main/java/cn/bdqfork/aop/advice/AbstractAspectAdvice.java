package cn.bdqfork.aop.advice;

import cn.bdqfork.core.util.BeanUtils;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * @author bdq
 * @since 2019/12/23
 */
public abstract class AbstractAspectAdvice implements AspectAdvice {
    /**
     * Aspect注解修饰的切面类实例
     */
    protected Object aspectInstance;
    /**
     * Before、AfterReturning、Around、AfterThrowing注解修饰的切面通知方法
     */
    protected Method aspectAdviceMethod;
    /**
     * 连接点
     */
    protected JoinPoint joinPoint;

    /**
     * 获取Before、AfterReturning、Around、AfterThrowing增强方法所需要回调的参数
     *
     * @param value 参数，可能是被增强方法的返回值或者是异常
     * @param clazz JointPoint类型
     * @return Object[]
     */
    protected Object[] getAdviceArgs(Object value, Class<?> clazz) {
        Parameter[] parameters = aspectAdviceMethod.getParameters();
        Object[] adviceArgs = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (BeanUtils.isSubType(parameters[i].getType(), clazz)) {
                adviceArgs[i] = joinPoint;
            } else {
                adviceArgs[i] = value;
            }
        }
        return adviceArgs;
    }

    @Override
    public void setJoinPoint(JoinPoint joinPoint) {
        this.joinPoint = joinPoint;
    }

    @Override
    public void setAspectInstance(Object aspectInstance) {
        this.aspectInstance = aspectInstance;
    }

    @Override
    public Object getAspectInstance() {
        return aspectInstance;
    }

    @Override
    public void setAspectAdviceMethod(Method aspectAdviceMethod) {
        this.aspectAdviceMethod = aspectAdviceMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractAspectAdvice that = (AbstractAspectAdvice) o;
        return Objects.equals(aspectInstance, that.aspectInstance) &&
                Objects.equals(aspectAdviceMethod, that.aspectAdviceMethod) &&
                Objects.equals(joinPoint, that.joinPoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aspectInstance, aspectAdviceMethod, joinPoint);
    }
}
