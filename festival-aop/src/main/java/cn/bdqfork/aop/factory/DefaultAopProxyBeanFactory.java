package cn.bdqfork.aop.factory;

import cn.bdqfork.aop.MethodSignature;
import cn.bdqfork.aop.advice.*;
import cn.bdqfork.aop.proxy.ProxyInvocationHandler;
import cn.bdqfork.aop.proxy.cglib.CglibMethodInterceptor;
import cn.bdqfork.aop.proxy.javassist.JavassistInvocationHandler;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.DefaultJSR250BeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;

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

    private final Set<Advisor> advisors = Collections.newSetFromMap(new ConcurrentHashMap<>(32));
    private final Map<String, Object> proxyInstances = new ConcurrentHashMap<>(256);

    public DefaultAopProxyBeanFactory() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String beanName) throws BeansException {
        if (!beanName.startsWith(PREFIX) && proxyInstances.containsKey(beanName)) {
            return (T) proxyInstances.get(beanName);
        }
        return super.getBean(beanName);
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
        return proxyInstances.get(beanName);
    }

    @Override
    protected void afterPropertiesSet(String beanName, Object bean) throws BeansException {
        super.afterPropertiesSet(beanName, bean);
        if (beanName.startsWith(PREFIX)) {
            return;
        }
        Object proxyBean = getAopProxyInstance(beanName, bean, null);
        registerProxyBean(beanName, proxyBean);
    }

    public void registerProxyBean(String beanName, Object proxyBean) throws BeansException {
        if (proxyInstances.containsKey(beanName)) {
            throw new BeansException("");
        }
        proxyInstances.put(beanName, proxyBean);
    }

    @Override
    public void registerAdvisor(Advisor advisor) throws BeansException {
        if (advisor == null) {
            throw new BeansException("register advisor is null");
        }
        advisors.add(advisor);
    }

    @Override
    public Object getAopProxyInstance(String beanName, Object bean, Class<?>[] interfaces) throws BeansException {
        ProxyInvocationHandler invocationHandler;

        if (interfaces != null && interfaces.length > 0) {
            invocationHandler = new JavassistInvocationHandler();
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
