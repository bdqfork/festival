package cn.bdqfork.aop.factory;

import cn.bdqfork.aop.MethodSignature;
import cn.bdqfork.aop.advice.*;
import cn.bdqfork.aop.proxy.ProxyFactory;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanDefinition;
import cn.bdqfork.core.factory.DefaultJSR250BeanFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class DefaultAopProxyBeanFactory extends DefaultJSR250BeanFactory implements AopProxyBeanFactory {
    public static final String PREFIX = "$";

    private static final Set<Advisor> beforeAdvisors = Collections.newSetFromMap(new ConcurrentHashMap<>(32));
    private static final Set<Advisor> aroundAdvisors = Collections.newSetFromMap(new ConcurrentHashMap<>(32));
    private static final Set<Advisor> afterAdvisors = Collections.newSetFromMap(new ConcurrentHashMap<>(32));
    private static final Set<Advisor> throwsAdvisors = Collections.newSetFromMap(new ConcurrentHashMap<>(32));

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
        if (advisor.isAdviceTypeOf(BeforeAdvice.class)) {
            beforeAdvisors.add(advisor);
        }
        if (advisor.isAdviceTypeOf(AroundAdvice.class)) {
            aroundAdvisors.add(advisor);
        }
        if (advisor.isAdviceTypeOf(AfterAdvice.class)) {
            afterAdvisors.add(advisor);
        }
        if (advisor.isAdviceTypeOf(ThrowsAdvice.class)) {
            throwsAdvisors.add(advisor);
        }
    }

    @Override
    public Object getAopProxyInstance(String beanName, Object bean, Class<?>[] interfaces) throws BeansException {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(bean);
        if (interfaces == null) {
            proxyFactory.setInterfaces(bean.getClass().getInterfaces());
        } else {
            proxyFactory.setInterfaces(interfaces);
        }
        return proxyFactory.getProxy();
    }

    @Override
    public Map<String, Set<BeforeAdvice>> resolveBeforeAdvice(String beanName, Class<?> beanClass) {
        Map<String, Set<BeforeAdvice>> beforeAdviceMap = new HashMap<>();
        for (Method method : beanClass.getDeclaredMethods()) {
            Set<BeforeAdvice> beforeAdvices = beforeAdvisors.stream()
                    .filter(advisor -> advisor.isMatch(method, BeforeAdvice.class))
                    .map(advisor -> (BeforeAdvice) advisor.getAdvice())
                    .collect(Collectors.toSet());
            MethodSignature signature = new MethodSignature(beanClass, method);
            beforeAdviceMap.put(signature.toLongString(), beforeAdvices);
        }
        return beforeAdviceMap;
    }

    @Override
    public Map<String, Set<AfterAdvice>> resolveAfterAdvice(String beanName, Class<?> beanClass) {
        Map<String, Set<AfterAdvice>> afterAdviceMap = new HashMap<>();
        for (Method method : beanClass.getDeclaredMethods()) {
            Set<AfterAdvice> afterAdvices = afterAdvisors.stream()
                    .filter(advisor -> advisor.isMatch(method, AfterAdvice.class))
                    .map(advisor -> (AfterAdvice) advisor.getAdvice())
                    .collect(Collectors.toSet());
            MethodSignature signature = new MethodSignature(beanClass, method);
            afterAdviceMap.put(signature.toLongString(), afterAdvices);
        }
        return afterAdviceMap;
    }

    @Override
    public Map<String, Set<AroundAdvice>> resolveAroundAdvice(String beanName, Class<?> beanClass) {
        Map<String, Set<AroundAdvice>> aroundAdviceMap = new HashMap<>();
        for (Method method : beanClass.getDeclaredMethods()) {
            Set<AroundAdvice> aroundAdvices = aroundAdvisors.stream()
                    .filter(advisor -> advisor.isMatch(method, AroundAdvice.class))
                    .map(advisor -> (AroundAdvice) advisor.getAdvice())
                    .collect(Collectors.toSet());
            MethodSignature signature = new MethodSignature(beanClass, method);
            aroundAdviceMap.put(signature.toLongString(), aroundAdvices);
        }
        return aroundAdviceMap;
    }

    @Override
    public Map<String, Set<ThrowsAdvice>> resolveThrowsAdvice(String beanName, Class<?> beanClass) {
        Map<String, Set<ThrowsAdvice>> throwsAdviceMap = new HashMap<>();
        for (Method method : beanClass.getDeclaredMethods()) {
            Set<ThrowsAdvice> throwsAdvices = throwsAdvisors.stream()
                    .filter(advisor -> advisor.isMatch(method, ThrowsAdvice.class))
                    .map(advisor -> (ThrowsAdvice) advisor.getAdvice())
                    .collect(Collectors.toSet());
            MethodSignature signature = new MethodSignature(beanClass, method);
            throwsAdviceMap.put(signature.toLongString(), throwsAdvices);
        }
        return throwsAdviceMap;
    }
}
