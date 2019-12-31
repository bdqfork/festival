package cn.bdqfork.aop.factory;

import cn.bdqfork.core.exception.BeansException;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultAopProxyBeanFactoryTest {

    @Test
    public void getBean() throws BeansException {
        AspectAnnotationBeanFactory aspectBeanFactory = new AspectAnnotationBeanFactory();
        aspectBeanFactory.scan("cn.bdqfork.aop.factory");
        JSR250FieldService jsr250FieldService = aspectBeanFactory.getBean(JSR250FieldService.class);
        System.out.println(jsr250FieldService);
        System.out.println(jsr250FieldService.getJsr250FieldCycleDao());
    }
}