package cn.bdqfork.core.factory.support;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanDefinition;
import cn.bdqfork.core.factory.DefaultBeanFactory;
import cn.bdqfork.model.bean.SingletonBeanService;
import cn.bdqfork.model.bean.SingletonBeanServiceImpl;
import cn.bdqfork.model.cycle.SetterCycleService;
import org.junit.Test;


/**
 * DefaultBeanFactory类
 * 1.先新建一个DefaultBeanFactory对象
 * 2.手动将要创建的bean的描述信息注册到容器中
 *
 * @author FBW
 * @since 2020/1/6
 */
public class DefaultBeanFactoryTest {

    /**
     * 通过beanName创建Bean对象
     *
     * @throws BeansException
     */
    @Test
    public void testCreateBean() throws BeansException {
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.registerBeanDefinition("singletonBeanService",
                new BeanDefinition("singletonBeanService", SingletonBeanServiceImpl.class,
                        BeanDefinition.SINGLETON));
        defaultBeanFactory.createBean("singletonBeanService");
        defaultBeanFactory.getBean("singletonBeanService");
    }

    /**
     * 获取指定类型的Bean
     * @throws BeansException
     */
    @Test
    public void testGetBeans() throws BeansException {
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.registerBeanDefinition("singletonBeanService",
                new BeanDefinition("singletonBeanService", SingletonBeanServiceImpl.class,
                        BeanDefinition.SINGLETON));
        defaultBeanFactory.getBeans(SingletonBeanServiceImpl.class);
    }
    /**
     * 通过beanName获取bean的描述
     *
     * @throws BeansException
     */
    @Test
    public void testGetBeanDefinition() throws BeansException {
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.registerBeanDefinition("singletonBeanService",
                new BeanDefinition("singletonBeanService", SingletonBeanService.class,
                        BeanDefinition.SINGLETON));
        defaultBeanFactory.getBeanDefinition("singletonBeanService");
    }

    /**
     * 获取bean的描述
     * @throws BeansException
     */
    @Test
    public void testGetBeanDefinitions() throws BeansException {
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.registerBeanDefinition("singletonBeanService",
                new BeanDefinition("singletonBeanService", SingletonBeanService.class,
                        BeanDefinition.SINGLETON));
        defaultBeanFactory.getBeanDefinitions(SingletonBeanService.class);
        defaultBeanFactory.getBeanDefinitions();
    }

    /**
     * 判断beanName指定的bean是否为单例
     *
     * @throws BeansException
     */
    @Test
    public void testIsSingleton() throws BeansException {
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.registerBeanDefinition("singletonBeanService",
                new BeanDefinition("singletonBeanService", SingletonBeanServiceImpl.class,
                        BeanDefinition.SINGLETON));
        assert defaultBeanFactory.isSingleton("singletonBeanService") == true;
    }

    /**
     * 判断beanName指定的bean是否为多例
     *
     * @throws BeansException
     */
    @Test
    public void testIsPrototype() throws BeansException {
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.registerBeanDefinition("singletonBeanService",
                new BeanDefinition("singletonBeanService", SingletonBeanServiceImpl.class,
                        BeanDefinition.SINGLETON));
        assert defaultBeanFactory.isPrototype("singletonBeanService") == false;
    }

    /**
     * 设置委托工厂并获取
     * @throws BeansException
     */
    @Test
    public void testSetAndGetParentFactory() throws BeansException {
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.setParentBeanFactory(new AnnotationBeanFactory());
        defaultBeanFactory.getParentBeanFactory();
    }

}
