package test.cn.bdqfork.ioc.factorybean;

import cn.bdqfork.core.aop.advice.Advisor;
import cn.bdqfork.core.aop.aspect.RegexpMethodAdvisor;
import cn.bdqfork.core.container.FactoryBean;
import cn.bdqfork.core.context.AnnotationApplicationContext;
import cn.bdqfork.core.context.ApplicationContext;
import cn.bdqfork.core.exception.ApplicationContextException;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.aop.proxy.ProxyFactoryBean;
import org.junit.Test;

/**
 * @author bdq
 * @since 2019-07-30
 */
public class TestFactoryBean {

    @Test
    public void testFactoryBean() throws ApplicationContextException {
        ApplicationContext applicationContext = new AnnotationApplicationContext("test.cn.bdqfork.ioc.factorybean");
        applicationContext.registerSingleBean(new FactoryBean() {
            @Override
            public Object getObject() throws BeansException {
                return new UserDao();
            }

            @Override
            public Class<?> getObjectType() {
                return UserDao.class;
            }
        });
        UserDao userDao = applicationContext.getBean(UserDao.class);
        System.out.println(userDao);
    }

    @Test
    public void testProxyFactoryBean() throws ApplicationContextException {
        ApplicationContext applicationContext = new AnnotationApplicationContext("test.cn.bdqfork.ioc.factorybean");
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setInterfaces(UserDao.class);
        proxyFactoryBean.setTarget(new UserDao());
        proxyFactoryBean.addAdvice(new Before());
        applicationContext.registerSingleBean(proxyFactoryBean);
        UserDao userDao = applicationContext.getBean(UserDao.class);
        userDao.test();
    }

    @Test
    public void testAdvisorProxyFactoryBean() throws ApplicationContextException {
        ApplicationContext applicationContext = new AnnotationApplicationContext("test.cn.bdqfork.ioc.factorybean");
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setInterfaces(UserDao.class);
        proxyFactoryBean.setTarget(new UserDao());
        Before before = new Before();
        Advisor advisor = new RegexpMethodAdvisor();
        advisor.setPointcut(".*test.*");
        advisor.setAdvice(before);
        proxyFactoryBean.addAdvice(advisor);
        applicationContext.registerSingleBean(proxyFactoryBean);
        UserDao userDao = applicationContext.getBean(UserDao.class);
        userDao.test();
    }

    @Test
    public void testAdvisor() throws ApplicationContextException {
        ApplicationContext applicationContext = new AnnotationApplicationContext("test.cn.bdqfork.ioc.factorybean");
        Before before = new Before();
        Advisor advisor = new RegexpMethodAdvisor();
        advisor.setPointcut(".*test.*");
        advisor.setAdvice(before);
        applicationContext.registerSingleBean(new FactoryBean() {
            @Override
            public Object getObject() throws BeansException {
                return advisor;
            }

            @Override
            public Class<?> getObjectType() {
                return RegexpMethodAdvisor.class;
            }
        });
        UserService userService = applicationContext.getBean(UserService.class);
        userService.test();
    }
}
