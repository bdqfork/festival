package cn.bdqfork.core.container;

import cn.bdqfork.core.annotation.ScopeType;
import cn.bdqfork.core.aop.Advisor;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.aop.proxy.ProxyFactory;
import cn.bdqfork.core.aop.proxy.ProxyFactoryBean;
import cn.bdqfork.core.utils.BeanUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author bdq
 * @since 2019-08-01
 */
public class AdvisorBeanFactoryImpl extends AbstractBeanFactory implements AdvisorBeanFactory {
    /**
     * 实例前缀，以$开头的beanName，在执行getBean时会获取真实实例，而非代理类
     */
    public static final String INSTANCE_PREFIX = "$";
    private Map<String, Object> proxyInstances;
    /**
     * Advisor切面
     */
    private List<Advisor> advisors;

    public AdvisorBeanFactoryImpl() {
        this.advisors = new LinkedList<>();
        this.proxyInstances = new HashMap<>();
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
        boolean requireProxy = !beanName.startsWith(INSTANCE_PREFIX);

        if (requireProxy) {
            if (proxyInstances.containsKey(beanName)) {
                return proxyInstances.get(beanName);
            }
            BeanDefinition beanDefinition = getBeanDefinations().get(beanName);

            Object instance = getInstances().get(beanName);

            //重写FactoryBean的处理
            if (instance instanceof FactoryBean) {
                FactoryBean factoryBean = (FactoryBean) instance;

                //特殊处理ProxyFactoryBean
                if (factoryBean instanceof ProxyFactoryBean) {
                    ProxyFactoryBean proxyFactoryBean = (ProxyFactoryBean) instance;
                    Object proxyInstance = proxyFactoryBean.getObject();
                    proxyInstances.put(beanName, proxyInstance);
                    return proxyInstance;
                }

                instance = factoryBean.getObject();
            }

            if (instance == null) {
                proxyInstances.put(beanName, null);
                return null;
            }

            //如果不是单例，则调用父容器的getBean()方法获取UnSharedInstance
            if (!ScopeType.SINGLETON.equals(beanDefinition.getScope())) {
                instance = super.getBean(beanName);
            }
            Object proxyInstance = createProxyBean(beanDefinition, instance);
            proxyInstances.put(beanName, proxyInstance);
            return proxyInstance;
        } else {
            beanName = beanName.substring(1);
            return super.getBean(beanName);
        }

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
