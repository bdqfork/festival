package cn.bdqfork.core.container;

import cn.bdqfork.core.aop.advice.AspectAdvice;
import cn.bdqfork.core.aop.aspect.AspectAdvisor;
import cn.bdqfork.core.exception.BeansException;

/**
 * @author bdq
 * @since 2019-08-01
 */
public class AspectBeanFactoryImpl extends AdvisorBeanFactoryImpl {
    /**
     * 注册AspectAdvisor
     *
     * @param beanName Aspect注解类实例
     * @param advisor  AspectAdvisor实例
     * @throws BeansException Bean异常
     */
    public void registerAspectAdvisor(String beanName, AspectAdvisor advisor) throws BeansException {
        Object instance = super.getBean(INSTANCE_PREFIX + beanName);

        if (instance instanceof UnSharedInstance) {

            UnSharedInstance unSharedInstance = (UnSharedInstance) instance;

            ObjectFactory<Object> factory = unSharedInstance.getObjectFactory();

            instance = factory.getObject();
        }

        AspectAdvice aspectAdvice = (AspectAdvice) advisor.getAdvice();
        aspectAdvice.setAspectInstance(instance);

        registerAdvisor(advisor);
    }
}
