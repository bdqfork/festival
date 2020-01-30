package cn.bdqfork.aop.proxy;

import cn.bdqfork.aop.advice.*;
import cn.bdqfork.core.factory.processor.OrderAware;
import cn.bdqfork.core.util.BeanUtils;
import org.aspectj.lang.annotation.AfterThrowing;

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
        Collection<Advisor> beforeAdvisor = advisors.stream()
                .filter(advisor -> advisor.isMatch(method, BeforeAdvice.class))
                .collect(Collectors.toSet());
        List<Advisor> sortedAdvisor = BeanUtils.sortByOrder(beforeAdvisor);
        return sortedAdvisor.stream()
                .map(advisor -> (MethodBeforeAdvice) advisor.getAdvice())
                .collect(Collectors.toList());
    }

    public List<AroundAdvice> getAroundAdvice(Method method) {
        Collection<Advisor> aroundAdvisor = advisors.stream()
                .filter(advisor -> advisor.isMatch(method, AroundAdvice.class))
                .collect(Collectors.toSet());
        List<Advisor> sortedAdvisor = BeanUtils.sortByOrder(aroundAdvisor);
        return sortedAdvisor.stream()
                .map(advisor -> (AroundAdvice) advisor.getAdvice())
                .collect(Collectors.toList());
    }

    public List<AfterReturningAdvice> getAfterAdvice(Method method) {
        Collection<Advisor> afterAdvisor = advisors.stream()
                .filter(advisor -> advisor.isMatch(method, AfterReturningAdvice.class))
                .collect(Collectors.toSet());
        List<Advisor> sortedAdvisor = BeanUtils.sortByOrder(afterAdvisor);
        return sortedAdvisor.stream()
                .map(advisor -> (AfterReturningAdvice) advisor.getAdvice())
                .collect(Collectors.toList());
    }

    public List<ThrowsAdvice> getThrowsAdvice(Method method) {
        Collection<Advisor> throwsAdvisor = advisors.stream()
                .filter(advisor -> advisor.isMatch(method, ThrowsAdvice.class))
                .collect(Collectors.toSet());
        List<Advisor> sortedAdvisor = BeanUtils.sortByOrder(throwsAdvisor);
        return sortedAdvisor.stream()
                .map(advisor -> (ThrowsAdvice) advisor.getAdvice())
                .collect(Collectors.toList());
    }
}
