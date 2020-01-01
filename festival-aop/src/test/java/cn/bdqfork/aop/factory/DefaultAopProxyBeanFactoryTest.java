package cn.bdqfork.aop.factory;

import cn.bdqfork.core.exception.BeansException;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultAopProxyBeanFactoryTest {

    @Test
    public void getBean() throws BeansException {
        AspectAnnotationBeanFactory aspectBeanFactory = new AspectAnnotationBeanFactory();
        aspectBeanFactory.scan("cn.bdqfork.aop.factory");
        UserDaoImpl userDao1 = aspectBeanFactory.getBean(UserDaoImpl.class);
        UserDaoImpl userDao2 = aspectBeanFactory.getBean(UserDaoImpl.class);
        System.out.println(userDao1 == userDao2);
        userDao2.testAop();
        userDao2.testThrowing();
    }

}