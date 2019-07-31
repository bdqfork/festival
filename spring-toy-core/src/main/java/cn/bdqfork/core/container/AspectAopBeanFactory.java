package cn.bdqfork.core.container;

import cn.bdqfork.core.aop.Advisor;

import java.util.List;

/**
 * @author bdq
 * @since 2019-07-31
 */
public interface AspectAopBeanFactory extends BeanFactory {
    void registerAdvisor(String beanName, Advisor advisor);

    List<Advisor> getAdvisors();
}
