package cn.bdqfork.core.container;

import cn.bdqfork.core.aop.Advisor;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.aop.proxy.ProxyFactory;
import cn.bdqfork.core.aop.proxy.ProxyFactoryBean;
import cn.bdqfork.core.utils.BeanUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @since 2019-08-01
 */
public class AdvisorBeanFactoryImpl extends AbstractBeanFactory implements AdvisorBeanFactory {
    /**
     * 实例前缀，以$开头的beanName，在执行getBean时会获取真实实例，而非代理类
     */
    public static final String INSTANCE_PREFIX = "$";
    /**
     * Advisor切面
     */
    private List<Advisor> advisors;

    public AdvisorBeanFactoryImpl() {
        this.advisors = new LinkedList<>();
    }

    @Override
    public void registerSingleBean(String beanName, FactoryBean factoryBean) throws BeansException {
        if (BeanUtils.isSubType(factoryBean.getObjectType(), Advisor.class)) {
            registerAdvisor((Advisor) factoryBean.getObject());
            return;
        }
        super.registerSingleBean(beanName, factoryBean);
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        String actualBeanName = beanName;

        boolean requireProxy = !beanName.startsWith(INSTANCE_PREFIX);
        if (!requireProxy) {
            actualBeanName = beanName.substring(1);
        }

        Object instance = getInstances().get(beanName);

        //特殊处理ProxyFactoryBean
        if (instance instanceof ProxyFactoryBean) {
            ProxyFactoryBean proxyFactoryBean = (ProxyFactoryBean) instance;
            return proxyFactoryBean.getObject();
        }

        instance = super.getBean(actualBeanName);

        if (instance == null) {
            return null;
        }

        if (!requireProxy) {
            return instance;
        }
        BeanDefinition beanDefinition = getBeanDefinations().get(actualBeanName);
        return createProxyBean(beanDefinition, instance);
    }

    private Object createProxyBean(BeanDefinition beanDefinition, Object target) throws BeansException {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        proxyFactory.setBeanFactory(this);
        proxyFactory.setInterfaces(beanDefinition.getClazz().getInterfaces());

        return proxyFactory.getProxy();
    }

    @Override
    public void registerAdvisor(Advisor advisor) {
        advisors.add(advisor);
    }

    @Override
    public List<Advisor> getAdvisors() {
        return advisors;
    }
}
