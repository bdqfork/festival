package cn.bdqfork.core.container;

import cn.bdqfork.core.aop.advice.Advisor;

import java.util.List;

/**
 * 添加AOP功能
 *
 * @author bdq
 * @since 2019-08-01
 */
public interface AdvisorBeanFactory extends BeanFactory {
    /**
     * 注册顾问
     *
     * @param advisor 顾问
     */
    void registerAdvisor(Advisor advisor);

    /**
     * 获取顾问
     *
     * @return List<Advisor>
     */
    List<Advisor> getAdvisors();
}
