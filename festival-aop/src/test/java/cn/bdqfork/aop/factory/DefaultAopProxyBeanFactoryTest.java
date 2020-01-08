package cn.bdqfork.aop.factory;

import cn.bdqfork.aop.context.AspectApplicationContext;
import cn.bdqfork.core.exception.BeansException;
import org.junit.Test;

public class DefaultAopProxyBeanFactoryTest {

    @Test
    public void getBean() throws BeansException {
        AspectApplicationContext aspectBeanFactory = new AspectApplicationContext();
        aspectBeanFactory.scan("cn.bdqfork.aop.factory");
        aspectBeanFactory.refresh();
        aspectBeanFactory.refresh();
        UserDaoImpl userDao1 = aspectBeanFactory.getBean(UserDaoImpl.class);
        UserDaoImpl userDao2 = aspectBeanFactory.getBean(UserDaoImpl.class);
        System.out.println(userDao1 == userDao2);
        userDao2.testAop();
        userDao2.testThrowing();
    }

}