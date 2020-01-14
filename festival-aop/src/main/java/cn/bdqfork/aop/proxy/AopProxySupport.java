package cn.bdqfork.aop.proxy;

import cn.bdqfork.aop.advice.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
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

    public Set<MethodBeforeAdvice> getBeforeAdvice(Method method) {
        return advisors.stream()
                .filter(advisor -> advisor.isMatch(method, MethodBeforeAdvice.class))
                .map(advisor -> (MethodBeforeAdvice) advisor.getAdvice())
                .collect(Collectors.toSet());
    }

    public Set<AroundAdvice> getAroundAdvice(Method method) {
        return advisors.stream()
                .filter(advisor -> advisor.isMatch(method, AroundAdvice.class))
                .map(advisor -> (AroundAdvice) advisor.getAdvice())
                .collect(Collectors.toSet());
    }

    public Set<AfterReturningAdvice> getAfterAdvice(Method method) {
        return advisors.stream()
                .filter(advisor -> advisor.isMatch(method, AfterReturningAdvice.class))
                .map(advisor -> (AfterReturningAdvice) advisor.getAdvice())
                .collect(Collectors.toSet());
    }

    public Set<ThrowsAdvice> getThrowsAdvice(Method method) {
        return advisors.stream()
                .filter(advisor -> advisor.isMatch(method, ThrowsAdvice.class))
                .map(advisor -> (ThrowsAdvice) advisor.getAdvice())
                .collect(Collectors.toSet());
    }
}
