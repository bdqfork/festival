package cn.bdqfork.core.container;

import cn.bdqfork.core.aop.Advisor;
import cn.bdqfork.core.aop.aspect.AspectAdvice;
import cn.bdqfork.core.aop.aspect.AspectAdvisor;
import cn.bdqfork.core.exception.BeansException;

/**
 * @author bdq
 * @since 2019-08-01
 */
public class AspectBeanFactory extends AopBeanFactory {
    public void registerAdvisor(String beanName, Advisor advisor) throws BeansException {
        Object instance = super.getBean(INSTANCE_PREFIX + beanName);
        if (instance instanceof UnSharedInstance) {
            instance = ((UnSharedInstance) instance).getObjectFactory().getObject();
        }
        if (advisor instanceof AspectAdvisor) {
            AspectAdvisor aspectAdvisor = (AspectAdvisor) advisor;
            AspectAdvice aspectAdvice = aspectAdvisor.getAspectAdvice();
            aspectAdvice.setAdviceInstance(instance);
        }
        registerAdvisor(advisor);
    }
}
