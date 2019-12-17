package cn.bdqfork.core.factory.support;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.UnsatisfiedBeanException;
import cn.bdqfork.model.cycle.*;
import org.junit.Test;

public class AnnotationBeanfactoryTest {

    @Test(expected = UnsatisfiedBeanException.class)
    public void testConstructorCycle() throws BeansException {
        AnnotationBeanfactory annotationBeanfactory = new AnnotationBeanfactory("cn.bdqfork.model");
        annotationBeanfactory.getBean(ConstructorCycleService.class);
        annotationBeanfactory.getBean(PrototypeConstructorCycleService.class);
    }

    @Test
    public void testFieldCycle() throws BeansException {
        AnnotationBeanfactory annotationBeanfactory = new AnnotationBeanfactory("cn.bdqfork.model");
        annotationBeanfactory.getBean(FieldCycleService.class);
    }

    @Test(expected = UnsatisfiedBeanException.class)
    public void testPrototypeFieldCycle() throws BeansException {
        AnnotationBeanfactory annotationBeanfactory = new AnnotationBeanfactory("cn.bdqfork.model");
        annotationBeanfactory.getBean(PrototypeFieldCycleService.class);
    }

    @Test
    public void testSetterCycle() throws BeansException {
        AnnotationBeanfactory annotationBeanfactory = new AnnotationBeanfactory("cn.bdqfork.model");
        annotationBeanfactory.getBean(SetterCycleService.class);
    }

    @Test(expected = UnsatisfiedBeanException.class)
    public void testPrototypeSetterCycle() throws BeansException {
        AnnotationBeanfactory annotationBeanfactory = new AnnotationBeanfactory("cn.bdqfork.model");
        annotationBeanfactory.getBean(PrototypeSetterCycleService.class);
    }
}