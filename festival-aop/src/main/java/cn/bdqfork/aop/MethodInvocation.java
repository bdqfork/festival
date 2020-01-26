package cn.bdqfork.aop;


import cn.bdqfork.aop.advice.AspectAdvice;
import cn.bdqfork.aop.advice.MethodBeforeAdvice;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class MethodInvocation implements ProceedingJoinPoint {
    /**
     * 目标实例
     */
    private Object target;
    /**
     * 代理方法
     */
    private Method method;
    /**
     * 代理方法参数
     */
    private Object[] args;
    /**
     * 前置增强
     */
    private MethodBeforeAdvice[] beforeAdvices;
    /**
     * 签名
     */
    private StaticPart staticPart;

    public MethodInvocation(Object target, Method method, Object[] args, MethodBeforeAdvice[] beforeAdvices) {
        this.target = target;
        this.method = method;
        this.args = args;
        this.beforeAdvices = beforeAdvices;
        this.staticPart = new StaticPartImpl(method);
    }

    @Override
    public void set$AroundClosure(AroundClosure arc) {
        //AspectJ实现，不支持
    }

    @Override
    public Object proceed() throws Throwable {
        for (MethodBeforeAdvice methodBeforeAdvice : beforeAdvices) {
            if (methodBeforeAdvice instanceof AspectAdvice) {
                AspectAdvice aspectAdvice = (AspectAdvice) methodBeforeAdvice;
                aspectAdvice.setJoinPoint(this);
            }
            methodBeforeAdvice.before(method, args, target);
        }
        return method.invoke(target, args);
    }

    @Override
    public Object proceed(Object[] args) throws Throwable {
        for (MethodBeforeAdvice methodBeforeAdvice : beforeAdvices) {
            methodBeforeAdvice.before(method, args, target);
        }
        return method.invoke(target, args);
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toShortString() {
        return staticPart.toShortString();
    }

    @Override
    public String toLongString() {
        return staticPart.toLongString();
    }

    @Override
    public Object getThis() {
        return null;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Object[] getArgs() {
        return args;
    }

    @Override
    public Signature getSignature() {
        return staticPart.getSignature();
    }

    @Override
    public SourceLocation getSourceLocation() {
        return staticPart.getSourceLocation();
    }

    @Override
    public String getKind() {
        return staticPart.getKind();
    }

    @Override
    public StaticPart getStaticPart() {
        return staticPart;
    }

    static class StaticPartImpl implements StaticPart {
        private Signature signature;

        private StaticPartImpl(Method method) {
            this.signature = new MethodSignature(method);
        }

        @Override
        public Signature getSignature() {
            return signature;
        }

        @Override
        public SourceLocation getSourceLocation() {
            return null;
        }

        @Override
        public String getKind() {
            return null;
        }

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public String toShortString() {
            return signature.toShortString();
        }

        @Override
        public String toLongString() {
            return signature.toLongString();
        }
    }
}
