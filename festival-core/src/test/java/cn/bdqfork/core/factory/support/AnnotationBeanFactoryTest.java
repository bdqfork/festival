package cn.bdqfork.core.factory.support;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.CircularDependencyException;
import cn.bdqfork.model.cycle.*;
import cn.bdqfork.model.jsr250.JSR250FieldCycleDao;
import cn.bdqfork.model.jsr250.JSR250FieldService;
import org.junit.Test;

public class AnnotationBeanFactoryTest {

    @Test(expected = CircularDependencyException.class)
    public void testConstructorCycle() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model");
        annotationBeanfactory.getBean(ConstructorCycleService.class);
        annotationBeanfactory.getBean(PrototypeConstructorCycleService.class);
    }

    @Test
    public void testFieldCycle() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model");
        annotationBeanfactory.getBean(FieldCycleService.class);
        annotationBeanfactory.getBean(ProviderFieldCycleService.class);
    }

    @Test(expected = CircularDependencyException.class)
    public void testPrototypeFieldCycle() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model");
        annotationBeanfactory.getBean(PrototypeFieldCycleService.class);
    }

    @Test
    public void testSetterCycle() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model");
        annotationBeanfactory.getBean(SetterCycleService.class);
        annotationBeanfactory.getBean(ProviderSetterCycleService.class);
    }

    @Test(expected = CircularDependencyException.class)
    public void testPrototypeSetterCycle() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model");
        annotationBeanfactory.getBean(PrototypeSetterCycleService.class);
    }

    @Test
    public void testJSR250Intercept() throws BeansException {
        AnnotationBeanFactory annotationBeanfactory = new AnnotationBeanFactory();
        annotationBeanfactory.scan("cn.bdqfork.model.jsr250");
        annotationBeanfactory.getBean(JSR250FieldService.class);
        annotationBeanfactory.destroy();
    }
}