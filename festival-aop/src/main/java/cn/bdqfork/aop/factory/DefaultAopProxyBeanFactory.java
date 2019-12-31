package cn.bdqfork.aop.factory;

import cn.bdqfork.aop.MethodSignature;
import cn.bdqfork.aop.advice.*;
import cn.bdqfork.aop.proxy.ProxyInvocationHandler;
import cn.bdqfork.aop.proxy.cglib.CglibMethodInterceptor;
import cn.bdqfork.aop.proxy.javassist.JavassistInvocationHandlerDefault;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanDefinition;
import cn.bdqfork.core.factory.DefaultJSR250BeanFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class DefaultAopProxyBeanFactory extends DefaultJSR250BeanFactory implements AopProxyBeanFactory {
    public static final String PREFIX = "$";

    private static final Set<Advisor> advisors = Collections.newSetFromMap(new ConcurrentHashMap<>(32));

    public DefaultAopProxyBeanFactory() {
        super();
    }

    @Override
    protected Object doCreateBean(String beanName, BeanDefinition beanDefinition, Object[] explicitArgs) throws BeansException {
        Object instance = super.doCreateBean(beanName, beanDefinition, explicitArgs);
        if (instance == null) {
            return null;
        }
        if (beanName.startsWith(PREFIX)) {
            return instance;
        }
        return getAopProxyInstance(beanName, instance, null);
    }

    @Override
    public void registerAdvisor(Advisor advisor) throws BeansException {
        if (advisor == null) {
            throw new BeansException("");
        }
        advisors.add(advisor);
    }

    @Override
    public Object getAopProxyInstance(String beanName, Object bean, Class<?>[] interfaces) throws BeansException {
        ProxyInvocationHandler invocationHandler;

        if (interfaces != null && interfaces.length > 0) {
            invocationHandler = new JavassistInvocationHandlerDefault();
        } else {
            invocationHandler = new CglibMethodInterceptor();
        }

        invocationHandler.setTarget(bean);

        Class<?> beanClass = bean.getClass();

        if (interfaces == null) {
            interfaces = beanClass.getInterfaces();
        }

        invocationHandler.setInterfaces(interfaces);

        Map<String, Set<MethodBeforeAdvice>> beforeAdvices = resolveBeforeAdvice(beanName, beanClass);
        for (Map.Entry<String, Set<MethodBeforeAdvice>> entry : beforeAdvices.entrySet()) {
            invocationHandler.addAdvice(entry.getKey(), entry.getValue().toArray(new MethodBeforeAdvice[0]));
        }

        Map<String, Set<AroundAdvice>> aroundAdvice = resolveAroundAdvice(beanName, beanClass);
        for (Map.Entry<String, Set<AroundAdvice>> entry : aroundAdvice.entrySet()) {
            invocationHandler.addAdvice(entry.getKey(), entry.getValue().toArray(new AroundAdvice[0]));
        }

        Map<String, Set<AfterReturningAdvice>> afterAdvices = resolveAfterAdvice(beanName, beanClass);
        for (Map.Entry<String, Set<AfterReturningAdvice>> entry : afterAdvices.entrySet()) {
            invocationHandler.addAdvice(entry.getKey(), entry.getValue().toArray(new AfterReturningAdvice[0]));
        }

        Map<String, Set<ThrowsAdvice>> throwsAdvice = resolveThrowsAdvice(beanName, beanClass);
        for (Map.Entry<String, Set<ThrowsAdvice>> entry : throwsAdvice.entrySet()) {
            invocationHandler.addAdvice(entry.getKey(), entry.getValue().toArray(new ThrowsAdvice[0]));
        }

        return invocationHandler.newProxyInstance();
    }

    @Override
    public Map<String, Set<MethodBeforeAdvice>> resolveBeforeAdvice(String beanName, Class<?> beanClass) {
        Map<String, Set<MethodBeforeAdvice>> adviceMap = new HashMap<>();
        for (Method method : beanClass.getDeclaredMethods()) {
            Set<MethodBeforeAdvice> advices = advisors.stream()
                    .filter(advisor -> advisor.isMatch(method, MethodBeforeAdvice.class))
                    .map(advisor -> (MethodBeforeAdvice) advisor.getAdvice())
                    .collect(Collectors.toSet());
            MethodSignature signature = new MethodSignature(beanClass, method);
            adviceMap.put(signature.toLongString(), advices);
        }
        return adviceMap;
    }

    @Override
    public Map<String, Set<AroundAdvice>> resolveAroundAdvice(String beanName, Class<?> beanClass) {
        Map<String, Set<AroundAdvice>> adviceMap = new HashMap<>();
        for (Method method : beanClass.getDeclaredMethods()) {
            Set<AroundAdvice> advices = advisors.stream()
                    .filter(advisor -> advisor.isMatch(method, AroundAdvice.class))
                    .map(advisor -> (AroundAdvice) advisor.getAdvice())
                    .collect(Collectors.toSet());
            MethodSignature signature = new MethodSignature(beanClass, method);
            adviceMap.put(signature.toLongString(), advices);
        }
        return adviceMap;
    }

    @Override
    public Map<String, Set<AfterReturningAdvice>> resolveAfterAdvice(String beanName, Class<?> beanClass) {
        Map<String, Set<AfterReturningAdvice>> adviceMap = new HashMap<>();
        for (Method method : beanClass.getDeclaredMethods()) {
            Set<AfterReturningAdvice> advices = advisors.stream()
                    .filter(advisor -> advisor.isMatch(method, AfterReturningAdvice.class))
                    .map(advisor -> (AfterReturningAdvice) advisor.getAdvice())
                    .collect(Collectors.toSet());
            MethodSignature signature = new MethodSignature(beanClass, method);
            adviceMap.put(signature.toLongString(), advices);
        }
        return adviceMap;
    }

    @Override
    public Map<String, Set<ThrowsAdvice>> resolveThrowsAdvice(String beanName, Class<?> beanClass) {
        Map<String, Set<ThrowsAdvice>> adviceMap = new HashMap<>();
        for (Method method : beanClass.getDeclaredMethods()) {
            Set<ThrowsAdvice> advices = advisors.stream()
                    .filter(advisor -> advisor.isMatch(method, ThrowsAdvice.class))
                    .map(advisor -> (ThrowsAdvice) advisor.getAdvice())
                    .collect(Collectors.toSet());
            MethodSignature signature = new MethodSignature(beanClass, method);
            adviceMap.put(signature.toLongString(), advices);
        }
        return adviceMap;
    }

}
