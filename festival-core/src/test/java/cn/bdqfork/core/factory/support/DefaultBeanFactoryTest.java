package cn.bdqfork.core.factory.support;

import cn.bdqfork.core.exception.*;
import cn.bdqfork.core.factory.DefaultJSR250BeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.processor.BeanPostProcessor;
import cn.bdqfork.core.factory.DefaultBeanFactory;
import cn.bdqfork.model.bean.SingletonBeanService;
import cn.bdqfork.model.bean.SingletonBeanServiceImpl;
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
        assert defaultBeanFactory.createBean("singletonBeanService") instanceof SingletonBeanService;
        assert defaultBeanFactory.getBean("singletonBeanService") instanceof SingletonBeanService;
    }

    /**
     * 通过beanName创建Bean对象
     *
     * @throws BeansException
     */
    @Test
    public void testBeanPostProcessor() throws BeansException {
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.registerBeanDefinition("singletonBeanService",
                new BeanDefinition("singletonBeanService", SingletonBeanServiceImpl.class,
                        BeanDefinition.SINGLETON));
        defaultBeanFactory.addPostBeanProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitializtion(String beanName, Object bean) throws BeansException {
                System.out.println("postProcessBeforeInitializtion......");
                return bean;
            }

            @Override
            public Object postProcessAfterInitializtion(String beanName, Object bean) throws BeansException {
                System.out.println("postProcessAfterInitializtion......");
                return bean;
            }
        });
        assert defaultBeanFactory.createBean("singletonBeanService") instanceof SingletonBeanService;
        assert defaultBeanFactory.getBean("singletonBeanService") instanceof SingletonBeanService;
    }

    /**
     * 获取指定类型的Bean
     *
     * @throws BeansException
     */
    @Test
    public void testGetBeans() throws BeansException {
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.registerBeanDefinition("singletonBeanService",
                new BeanDefinition("singletonBeanService", SingletonBeanServiceImpl.class,
                        BeanDefinition.SINGLETON));
        assert defaultBeanFactory.getBeans(SingletonBeanServiceImpl.class) != null;
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
        assert defaultBeanFactory.getBeanDefinition("singletonBeanService") != null;
    }

    /**
     * 获取bean的描述
     *
     * @throws BeansException
     */
    @Test
    public void testGetBeanDefinitions() throws BeansException {
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.registerBeanDefinition("singletonBeanService",
                new BeanDefinition("singletonBeanService", SingletonBeanService.class,
                        BeanDefinition.SINGLETON));
        assert defaultBeanFactory.getBeanDefinitions(SingletonBeanService.class) != null;
        assert defaultBeanFactory.getBeanDefinitions() != null;
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
     *
     * @throws BeansException
     */
    @Test
    public void testSetAndGetParentFactory() throws BeansException {
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.setParentBeanFactory(new DefaultJSR250BeanFactory());
        assert defaultBeanFactory.getParentBeanFactory() instanceof DefaultJSR250BeanFactory;
    }

    /**
     * ConflictedBeanException测试
     */
    @Test(expected = ConflictedBeanException.class)
    public void testConflictedBeanException() throws BeansException {
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.registerBeanDefinition("singletonBeanService",
                new BeanDefinition("singletonBeanService", SingletonBeanService.class,
                        BeanDefinition.SINGLETON));
        defaultBeanFactory.registerBeanDefinition("singletonBeanService",
                new BeanDefinition("singletonBeanService", SingletonBeanService.class,
                        BeanDefinition.SINGLETON));

    }

    /**
     * NoSuchBeanException异常类测试
     * @throws BeansException
     */
    @Test(expected = NoSuchBeanException.class)
    public void testNoSuchBeanException() throws BeansException {
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.registerBeanDefinition("singletonBeanService",
                new BeanDefinition("singletonBeanService", SingletonBeanService.class,
                        BeanDefinition.SINGLETON));
        //将beanName首字母改为大写，出现该异常
        defaultBeanFactory.getBean("SingletonBeanService");
    }


}