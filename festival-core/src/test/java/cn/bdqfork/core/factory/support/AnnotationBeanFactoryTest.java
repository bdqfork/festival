package cn.bdqfork.core.factory.support;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.CircularDependencyException;
import cn.bdqfork.model.cycle.*;
import cn.bdqfork.model.jsr250.JSR250FieldService;
import cn.bdqfork.model.jsr250.JSR250SetterCycleService;
import org.junit.Test;

public class AnnotationBeanFactoryTest {

    /**
     * 构造方法注入，单例/多例
     * 测试循环依赖异常功能
     * @throws BeansException
     */
    @Test(expected = CircularDependencyException.class)
    public void testConstructorCycle() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model");
        annotationBeanfactory.getBean(ConstructorCycleService.class);
        annotationBeanfactory.getBean(PrototypeConstructorCycleService.class);
    }

    /**
     * 自动注入功能测试
     * 获取单例bean对象
     * @throws BeansException
     */
    @Test
    public void testFieldCycle() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model");
        annotationBeanfactory.getBean(FieldCycleService.class);
        annotationBeanfactory.getBean(ProviderFieldCycleService.class);
    }

    /**
     * 自动注入循环依赖异常
     * @throws BeansException
     */
    @Test(expected = CircularDependencyException.class)
    public void testPrototypeFieldCycle() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model");
        annotationBeanfactory.getBean(PrototypeFieldCycleService.class);
    }

    /**
     * Setter方法注入，获取单例bean对象
     * @throws BeansException
     */
    @Test
    public void testSetterCycle() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model");
        annotationBeanfactory.getBean(SetterCycleService.class);
        annotationBeanfactory.getBean(ProviderSetterCycleService.class);
    }

    /**
     * Setter方法注入,获取多例对象循环依赖异常
     * @throws BeansException
     */
    @Test(expected = CircularDependencyException.class)
    public void testPrototypeSetterCycle() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model");
        annotationBeanfactory.getBean(PrototypeSetterCycleService.class);
    }

    /**
     * 使用jsr250注解，指定类型对象注入数据。获取bean对象
     * @throws BeansException
     */
    @Test
    public void testJSR250Intercept() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model.jsr250");
        annotationBeanfactory.getBean(JSR250FieldService.class);
        annotationBeanfactory.destroy();
    }

    /**
     * 使用Setter方法注入，循环依赖
     * @throws BeansException
     */
    @Test
    public void testJSR250Setter() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model.jsr250");
        annotationBeanfactory.getBean(JSR250SetterCycleService.class);
        annotationBeanfactory.destroy();
    }

    @Test
    public void testGetBeanByName() throws BeansException

    {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model.cycle");
        annotationBeanfactory.getBean("setterCycleService");
    }

    @Test
    public void testGetSetterBeans() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model.cycle");
        annotationBeanfactory.getBeans(SetterCycleService.class);
    }

    @Test
    public void testIsSingleton() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model.cycle");
        assert annotationBeanfactory.isSingleton("setterCycleService")==true;
        }

    @Test
    public void testIsPrototype() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model.cycle");
        assert annotationBeanfactory.isPrototype("setterCycleService")==false;
        assert annotationBeanfactory.isPrototype("prototypeSetterCycleService")==true;
    }

    @Test
    public void testContainBean() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model.cycle");
        assert annotationBeanfactory.containBean("setterCycleService")==true;
        assert annotationBeanfactory.containBean("prototypeSetterCycleService")==true;
    }

    @Test
    public void testGetBeanByBeanNameAndType() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model.cycle");
        annotationBeanfactory. getSpecificBean("setterCycleService", SetterCycleService.class);
    }
}