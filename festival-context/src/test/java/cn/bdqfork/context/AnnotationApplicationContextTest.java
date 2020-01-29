package cn.bdqfork.context;


import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.CircularDependencyException;
import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.exception.UnsatisfiedBeanException;
import cn.bdqfork.model.bean.exception.ResolveExceptionBean;
import cn.bdqfork.model.bean.exception.unsatisfied.UnsatisfiedBeanExceptionBean;
import cn.bdqfork.model.bean.normal.SingletonBeanService;
import cn.bdqfork.model.collection.CollectionPropertyService;
import cn.bdqfork.model.configration.FactoryBean;
import cn.bdqfork.model.configration.Server;
import cn.bdqfork.model.configration.ServerConfig;
import cn.bdqfork.model.cycle.*;
import cn.bdqfork.model.jsr250.JSR250FieldService;
import cn.bdqfork.model.proxy.AopProxyTestBean;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author fbw
 * @since 2020/1/8
 */
public class AnnotationApplicationContextTest {

    /**
     * 自动注入功能测试
     * 获取单例bean对象
     *
     * @throws BeansException
     */

    @Test
    public void testFieldCycle() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.cycle");
        annotationApplicationContext.start();
        annotationApplicationContext.getBean(FieldCycleService.class);
        annotationApplicationContext.getBean(ProviderFieldCycleService.class);
    }

    /**
     * 构造方法注入，单例/多例
     * 测试循环依赖异常功能
     *
     * @throws BeansException
     */
    @Test(expected = CircularDependencyException.class)
    public void testConstructorCycle() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.cycle");
        annotationApplicationContext.start();
        annotationApplicationContext.getBean(ConstructorCycleService.class);
        annotationApplicationContext.getBean(PrototypeConstructorCycleService.class);
    }

    /**
     * 自动注入循环依赖异常
     *
     * @throws BeansException
     */
    @Test(expected = CircularDependencyException.class)
    public void testPrototypeFieldCycle() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.cycle");
        annotationApplicationContext.start();
        annotationApplicationContext.getBean(PrototypeFieldCycleService.class);
    }

    /**
     * Setter方法注入，获取单例bean对象
     *
     * @throws BeansException
     */
    @Test
    public void testSetterCycle() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.cycle");
        annotationApplicationContext.start();
        annotationApplicationContext.getBean(SetterCycleService.class);
        annotationApplicationContext.getBean(ProviderSetterCycleService.class);
    }

    /**
     * Setter方法注入,获取多例对象循环依赖异常
     *
     * @throws BeansException
     */
    @Test(expected = CircularDependencyException.class)
    public void testPrototypeSetterCycle() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.cycle");
        annotationApplicationContext.start();
        annotationApplicationContext.getBean(PrototypeSetterCycleService.class);
    }

    /**
     * 使用jsr250注解，指定类型对象注入数据。获取bean对象
     *
     * @throws BeansException
     */
    @Test
    public void testJSR250Intercept() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.jsr250");
        annotationApplicationContext.start();
        annotationApplicationContext.getBean(JSR250FieldService.class);
        annotationApplicationContext.close();
    }

    /**
     * 使用Setter方法注入，循环依赖
     *
     * @throws BeansException
     */
    @Test
    public void testJSR250Setter() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.jsr250");
        annotationApplicationContext.start();
        annotationApplicationContext.getBean(JSR250FieldService.class);
        annotationApplicationContext.close();
    }

    /**
     * 使用@Named注解的工厂方法配置bean
     *
     * @throws BeansException
     */

    @Test
    public void testGetFactoryBeanDefinition() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.configration");
        annotationApplicationContext.start();
        FactoryBean factoryBean = annotationApplicationContext.getBean("factoryBean");
        System.out.println(factoryBean);
        assert factoryBean != null;
        assert factoryBean.getServer() != null;
    }

    /**
     * 通过BeanName获取Bean
     *
     * @throws BeansException
     */
    @Test
    public void testGetBeanByName() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.bean.normal");
        annotationApplicationContext.start();
        assert annotationApplicationContext.getBean("singletonBeanServiceImpl") instanceof SingletonBeanService;
    }

    /**
     * 通过类型获取Bean
     *
     * @throws BeansException
     */
    @Test
    public void testGetBeans() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.bean.normal");
        annotationApplicationContext.start();
        assert annotationApplicationContext.getBeans(SingletonBeanService.class) != null;
    }

    /**
     * 判断Bean是否为多例
     *
     * @throws BeansException
     */
    @Test
    public void testIsPrototype() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.bean.normal");
        annotationApplicationContext.start();
        assert annotationApplicationContext.isPrototype("singletonBeanServiceImpl") == false;
    }

    /**
     * 判断Bean是否为单例
     *
     * @throws BeansException
     */
    @Test
    public void testIsSingleton() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.bean.normal");
        annotationApplicationContext.start();
        assert annotationApplicationContext.isSingleton("singletonBeanServiceImpl") == true;
    }

    /**
     * 判断容器中是否包含Bean
     *
     * @throws BeansException
     */
    @Test
    public void testContainBean() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.bean.normal");
        annotationApplicationContext.start();
        assert annotationApplicationContext.containBean("singletonBeanServiceImpl") == true;
    }

    /**
     * 测试属性集合注入
     *
     * @throws BeansException
     */
    @Test
    public void testCollectionPropertyInjected() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.collection");
        annotationApplicationContext.start();
        CollectionPropertyService collectionPropertyService = annotationApplicationContext.getBean(CollectionPropertyService.class);
        assert collectionPropertyService.getDaos().size() > 0;
        assert collectionPropertyService.getDaoMap().size() > 0;
    }

    /**
     * 通过beanName和类型获取指定的Bean
     *
     * @throws BeansException
     */
    @Test
    public void testGetSpecificBean() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.bean.normal");
        annotationApplicationContext.start();
        assert annotationApplicationContext.getSpecificBean("singletonBeanServiceImpl", SingletonBeanService.class) != null;
    }

    /**
     * 配置文件读取功能测试
     *
     * @throws BeansException
     */
    @Test
    public void testConfigration() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.configration");
        annotationApplicationContext.start();
        Server server = annotationApplicationContext.getBean(Server.class);
        ServerConfig serverConfig = server.getServerConfig();
        assertEquals(serverConfig.getLocalhost(), "127.0.0.1");
        for (String name : serverConfig.getNames()) {
            System.out.println(name);
        }
    }

    /**
     * ResolveException异常测试
     *
     * @throws BeansException
     */
    @Test(expected = ResolvedException.class)
    public void testResolveException() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.bean.exception");
        annotationApplicationContext.start();
        annotationApplicationContext.getBean(ResolveExceptionBean.class);
    }

    /**
     * UnsatisfiedBeanException异常测试
     *
     * @throws BeansException
     */
    @Test(expected = UnsatisfiedBeanException.class)
    public void testUnsatisfiedBeanException() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.bean.exception.unsatisfied");
        annotationApplicationContext.start();
        assert annotationApplicationContext.getBean(UnsatisfiedBeanExceptionBean.class) != null;
    }

    /**
     * 测试AOP功能
     *
     * @throws BeansException
     */
    @Test
    public void testAop() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.proxy");
        annotationApplicationContext.start();
        AopProxyTestBean aopProxyTestBean = annotationApplicationContext.getBean(AopProxyTestBean.class);
        aopProxyTestBean.testAop();
        aopProxyTestBean.testThrowing();
    }

}
