package cn.bdqfork.aop.proxy;

import cn.bdqfork.aop.advice.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2020/1/14
 */
public class AopProxySupport extends AopProxyConfig {
    private Set<Advisor> advisors = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void addAdvisor(Advisor advisor) {
        advisors.add(advisor);
    }

    public void addAdvisors(Collection<Advisor> advisors) {
        this.advisors.addAll(advisors);
    }

    public List<MethodBeforeAdvice> getBeforeAdvice(Method method) {
        return advisors.stream()
                .filter(advisor -> advisor.isMatch(method, MethodBeforeAdvice.class))
                .sorted(Comparator.comparing(Advisor::getOrder))
                .map(advisor -> (MethodBeforeAdvice) advisor.getAdvice())
                .collect(Collectors.toList());
    }

    public List<AroundAdvice> getAroundAdvice(Method method) {
        return advisors.stream()
                .filter(advisor -> advisor.isMatch(method, AroundAdvice.class))
                .sorted(Comparator.comparing(Advisor::getOrder))
                .map(advisor -> (AroundAdvice) advisor.getAdvice())
                .collect(Collectors.toList());
    }

    public List<AfterReturningAdvice> getAfterAdvice(Method method) {
        return advisors.stream()
                .filter(advisor -> advisor.isMatch(method, AfterReturningAdvice.class))
                .sorted(Comparator.comparing(Advisor::getOrder))
                .map(advisor -> (AfterReturningAdvice) advisor.getAdvice())
                .collect(Collectors.toList());
    }

    public List<ThrowsAdvice> getThrowsAdvice(Method method) {
        return advisors.stream()
                .filter(advisor -> advisor.isMatch(method, ThrowsAdvice.class))
                .sorted(Comparator.comparing(Advisor::getOrder))
                .map(advisor -> (ThrowsAdvice) advisor.getAdvice())
                .collect(Collectors.toList());
    }
}
