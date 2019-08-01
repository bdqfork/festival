package cn.bdqfork.core.container;

import cn.bdqfork.core.aop.Advisor;

import java.util.List;

/**
 * @author bdq
 * @since 2019-08-01
 */
public interface AdvisorBeanFactory extends BeanFactory {

    void registerAdvisor(Advisor advisor);

    List<Advisor> getAdvisors();
}
