package cn.bdqfork.aop.factory;

import cn.bdqfork.core.exception.BeansException;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultAopProxyBeanFactoryTest {

    @Test
    public void getBean() throws BeansException {
        AspectAnnotationBeanFactory aspectBeanFactory = new AspectAnnotationBeanFactory();
        aspectBeanFactory.scan("cn.bdqfork.aop.factory");
        UserDaoImpl userDao = aspectBeanFactory.getBean(UserDaoImpl.class);
        System.out.println(userDao);
        userDao.testAop();
        userDao.testThrowing();
    }
}